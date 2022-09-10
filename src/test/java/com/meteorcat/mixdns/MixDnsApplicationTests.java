package com.meteorcat.mixdns;

import ch.qos.logback.core.util.TimeUtil;
import com.meteorcat.mixdns.core.AbstractDnsResolver;
import com.meteorcat.mixdns.core.utils.BeanUtil;
import com.meteorcat.mixdns.process.DnsListener;
import com.meteorcat.mixdns.process.DnsResolver;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.dns.*;
import io.netty.resolver.DefaultAddressResolverGroup;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class MixDnsApplicationTests {

    private static final Logger logger = LoggerFactory.getLogger(MixDnsApplicationTests.class);



    @Test
    void dnsEnv() throws Exception{
        Object udpPort = BeanUtil.getBean("DNS_PORT_BY_UDP");
        logger.info("UDP Port = {}",udpPort);
    }


    @Test
    void dnsParse() throws Exception {

        // 测试解析
        String address = "www.github.com";

        // 上游获取IP地址
        AbstractDnsResolver resolver = BeanUtil.getBean(DnsResolver.class);
        List<InetSocketAddress> lists = resolver.resolver(address);
        logger.info("Resolve = {}",lists);

        // 确认IP池
        Optional<InetSocketAddress> socketAddress = lists.stream().findFirst();
        Assert.isTrue(socketAddress.isPresent(),"error by resolve host");

        // 解析成地址
        InetSocketAddress source = socketAddress.get();
        ByteBuf buf = Unpooled.wrappedBuffer(source.getAddress().getAddress());
        DefaultDnsRawRecord dns = new DefaultDnsRawRecord(address, DnsRecordType.A,10,buf);
        logger.info("{} TO {}",address,dns.content().array());

        // 反向解析成地址
        InetAddress res = Inet4Address.getByAddress(source.getAddress().getAddress());
        logger.info("Source = {}",res.getHostAddress());

    }


    @Test
    void dnsQuery() throws Exception{
        String address = "127.0.0.1";
        int port = (int)BeanUtil.getBean("DNS_PORT_BY_UDP");;

        // 生成执行队列
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup());
        bootstrap.channel(NioDatagramChannel.class);
        bootstrap.handler(new ChannelInitializer<NioDatagramChannel>() {

            @Override
            protected void initChannel(NioDatagramChannel nioDatagramChannel) throws Exception {
                ChannelPipeline pipeline = nioDatagramChannel.pipeline();
                pipeline.addLast(new DatagramDnsQueryEncoder());
                pipeline.addLast(new DatagramDnsQueryDecoder());
                pipeline.addLast(new SimpleChannelInboundHandler<DatagramDnsResponse>(){

                    @Override
                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramDnsResponse datagramDnsResponse) throws Exception {
                        if(datagramDnsResponse.count(DnsSection.QUESTION) > 0){
                            DnsQuestion dnsQuestion = datagramDnsResponse.recordAt(DnsSection.QUESTION,0);
                            logger.info("Hostname[{}] = {}",dnsQuestion.type(),dnsQuestion.name());
                            logger.info("Detail = {}",dnsQuestion);
                        }

                        for (int i = 0; i < datagramDnsResponse.count(DnsSection.ANSWER); i++) {
                            DnsRecord record = datagramDnsResponse.recordAt(DnsSection.ANSWER,i);
                            if(record.type().equals(DnsRecordType.A)){
                                DnsRawRecord rawRecord = (DnsRawRecord) record;
                                logger.info(rawRecord.content().toString());
                            }
                        }
                    }
                });
            }
        });

        Channel channel = bootstrap.bind(0).sync().channel();
        DnsQuery query = new DatagramDnsQuery(null,new InetSocketAddress(address, port),1);
        query.setRecord(DnsSection.QUESTION,new DefaultDnsQuestion("home.meteorcat.com",DnsRecordType.A));
        channel.writeAndFlush(query).sync();
        boolean status = channel.closeFuture().await(10, TimeUnit.SECONDS);
        if(!status){
            logger.error("Dns Query Timeout");

            channel.close().sync();
        }
    }

}
