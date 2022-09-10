package com.meteorcat.mixdns.process.protocols;


import com.meteorcat.mixdns.core.AbstractDnsResolver;
import com.meteorcat.mixdns.core.utils.BeanUtil;
import com.meteorcat.mixdns.core.utils.DnsUtils;
import com.meteorcat.mixdns.model.DnsModel;
import com.meteorcat.mixdns.process.DnsResolver;
import com.meteorcat.mixdns.process.MixConfig;
import com.meteorcat.mixdns.service.DnsServer;
import com.meteorcat.mixdns.service.impl.DnsServerImpl;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.dns.*;
import io.netty.resolver.DefaultAddressResolverGroup;
import io.netty.util.internal.SocketUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @author MeteorCat
 */
public class DnsProtocol extends SimpleChannelInboundHandler<DatagramDnsQuery> {

    /**
     * 日志句柄
     */
    private final static Logger logger = LoggerFactory.getLogger(DnsProtocol.class);


    /**
     * DNS解析类型
     */
    private static final Set<DnsRecordType> QUESTION_TYPES = Collections.unmodifiableSet(new LinkedHashSet<>(
            Arrays.asList(
                    DnsRecordType.A,
                    DnsRecordType.AAAA,
                    DnsRecordType.ANY,
                    DnsRecordType.TXT,
                    DnsRecordType.SRV,
                    DnsRecordType.DS,
                    DnsRecordType.SOA,
                    DnsRecordType.NS
            )
    ));




    /**
     * 确实是否支持的解析对象
     * @param dnsRecordType DNS解析类型
     * @return boolean
     */
    private static boolean isValid(DnsRecordType dnsRecordType){
        return QUESTION_TYPES.contains(dnsRecordType);
    }

    /**
     * 确实记录类型是否匹配
     * @param dnsCls DNS类型码
     * @return boolean
     */
    private static boolean isValid(int dnsCls){
        return DnsRecord.CLASS_IN == dnsCls;
    }


    /**
     * 匹配是否为换成名称
     * @param name 解析名
     * @return boolean
     */
    private static boolean isValid(String name){
        if(name == null || name.isEmpty()){
            return false;
        }
        name = name.toLowerCase();
        return true;
    }


    /**
     * 匹配是否需要直接拦截解析
     * @param record DNS解析记录
     * @return boolean
     */
    private static boolean isValid(DnsRecord record){
        if(record == null){
            return false;
        }
        return isValid(record.type()) && isValid(record.dnsClass()) && isValid(record.name());
    }


    /**
     * 返回响应对象
     * @param datagramDnsQuery DNS查询对象
     * @return DatagramDnsResponse
     */
    private static DatagramDnsResponse getResponseHandler(DatagramDnsQuery datagramDnsQuery){
        // 构建出默认响应对象, 并设置响应码
        DatagramDnsResponse response = new DatagramDnsResponse(datagramDnsQuery.recipient(),datagramDnsQuery.sender(), datagramDnsQuery.id());
        response.setCode(DnsResponseCode.NXDOMAIN);
        return response;
    }


    /**
     * 获取查询出来的DNS句柄
     * @param datagramDnsQuery DNS查询对象
     * @return DefaultDnsQuestion
     */
    private static DefaultDnsQuestion getDnsQuestionHandler(DatagramDnsQuery datagramDnsQuery){
        return datagramDnsQuery.recordAt(DnsSection.QUESTION);
    }



    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramDnsQuery datagramDnsQuery) {

        // 获取响应对象和查询解析
        DatagramDnsResponse response = DnsProtocol.getResponseHandler(datagramDnsQuery);
        DefaultDnsQuestion dnsQuestion = DnsProtocol.getDnsQuestionHandler(datagramDnsQuery);
        if(dnsQuestion != null){
            response.addRecord(DnsSection.QUESTION,dnsQuestion);
        }



        // 确认是否为有效的DNS查询, 如果不是有效查询直接返回CONNECT REFUSED
        // 后续不需要解析结果
        if(!isValid(dnsQuestion)){
            response.setCode(DnsResponseCode.REFUSED);
            channelHandlerContext.writeAndFlush(response);
            return ;
        }

        // 获取解析的类型和域名, 缓存周期
        DnsRecordType dnsRecordType = dnsQuestion.type();
        String dnsRecordName = dnsQuestion.name().toLowerCase();
        MixConfig config = BeanUtil.getBean(MixConfig.class);
        logger.info("Received Dns Query[{}]: {}[ttl:{}]",dnsRecordType.name(),dnsRecordName,config.getTtl());

        // 本地拦截解析
        if((dnsRecordType.equals(DnsRecordType.A) || dnsRecordType.equals(DnsRecordType.AAAA))){

            // 确认内部已经拦截解析
            DnsServer dnsResolver = BeanUtil.getBean(DnsServerImpl.class);
            Optional<DnsModel> exists = dnsResolver.findHostAddress(dnsRecordType.intValue(),dnsRecordName);
            if(exists.isPresent()){
                DnsModel active = exists.get();
                dnsRecordType = DnsRecordType.valueOf(active.getType());
                logger.debug("Active Dns Query[{}]: {} -> {}",dnsRecordType,dnsRecordName,active.getBytes());

                // 写入返回记录
                response.addRecord(DnsSection.ANSWER, new DefaultDnsRawRecord(
                        dnsRecordName,
                        dnsRecordType,
                        config.getTtl(),
                        Unpooled.wrappedBuffer(active.getBytes())
                ));
                response.setCode(DnsResponseCode.NOERROR);
                channelHandlerContext.writeAndFlush(response);
                return ;
            }
        }


        // 获取解析工厂对象, 如果无法解析直接返回服务器错误
        List<InetSocketAddress> addresses;
        try{
            DnsResolver resolver = BeanUtil.getBean(DnsResolver.class);
            addresses = resolver.resolver(dnsRecordName);
            logger.debug("Resolvers: {} -> {}",dnsRecordName,addresses);

            if(addresses.isEmpty()){
                response.setCode(DnsResponseCode.REFUSED);
                channelHandlerContext.writeAndFlush(response);
            }

            response.addRecord(DnsSection.ANSWER, new DefaultDnsRawRecord(
                    dnsRecordName,
                    dnsRecordType,
                    config.getTtl(),
                    Unpooled.wrappedBuffer(addresses.get(0).getAddress().getAddress())
            ));
            response.setCode(DnsResponseCode.NOERROR);
            channelHandlerContext.writeAndFlush(response);
        }catch (ExecutionException|InterruptedException exception){
            logger.error(exception.toString());
            response.setCode(DnsResponseCode.SERVFAIL);
            channelHandlerContext.writeAndFlush(response);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        logger.error(cause.toString());
    }
}
