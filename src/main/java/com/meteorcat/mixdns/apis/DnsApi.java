package com.meteorcat.mixdns.apis;

import com.meteorcat.mixdns.core.utils.BeanUtil;
import com.meteorcat.mixdns.core.utils.RUtils;
import com.meteorcat.mixdns.defines.DnsStatusDefine;
import com.meteorcat.mixdns.model.DnsModel;
import com.meteorcat.mixdns.process.DnsResolver;
import com.meteorcat.mixdns.service.DnsServer;
import com.meteorcat.mixdns.service.impl.DnsServerImpl;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.util.internal.SocketUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author MeteorCat
 */
@RestController
@RequestMapping("/dns")
public class DnsApi {


    /**
     * 日志句柄
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());


    /**
     * 可修改的解析类型
     */
    private final List<Map<String,Object>> types = new ArrayList<Map<String,Object>>(){{
        // A解析
        add(new HashMap<String,Object>(2){{
            put("id",DnsRecordType.A.intValue());
            put("name",DnsRecordType.A.name());
        }});

        // AAAA解析
        add(new HashMap<String,Object>(2){{
            put("id",DnsRecordType.AAAA.intValue());
            put("name",DnsRecordType.AAAA.name());
        }});

    }};


    /**
     * 查询域名接口
     *
     * @param hostname 域名地址
     * @return JSON
     */
    @RequestMapping("/query")
    public RUtils query(@RequestParam String hostname) {
        DnsResolver dnsResolver = BeanUtil.getBean(DnsResolver.class);

        try {
            List<InetSocketAddress> hosts = dnsResolver.resolver(hostname);
            List<InetAddress> address = hosts.stream().map(active -> {
                try {
                    return InetAddress.getByAddress(active.getAddress().getAddress());
                } catch (UnknownHostException e) {
                    logger.error(e.toString());
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());

            return RUtils.success("操作成功", new HashMap<String, Object>(2) {{
                put("hostname", hostname);
                put("address", address);
            }});
        } catch (ExecutionException | InterruptedException exception) {
            logger.error(exception.toString());
            return RUtils.error("找不到对应域名数据");
        }
    }


    /**
     * 获取全部解析列表
     *
     * @return JSON
     */
    @RequestMapping("/list")
    public RUtils list() {
        DnsServer dnsServer = BeanUtil.getBean(DnsServerImpl.class);
        List<DnsModel> address = dnsServer.getList();

        return RUtils.success("操作成功", address.stream().map(active -> new HashMap<String, Object>(address.size()) {{
            // 格式化返回响应
            try {
                put("type",active.getType());
                put("typeName",DnsRecordType.valueOf(active.getType()).name());
                put("hostname", active.getHostname());
                put("address", InetAddress.getByAddress(active.getBytes()));
                put("createDate", new Date(active.getCreateTime() * 1000L));
            } catch (UnknownHostException e) {
                logger.error(e.toString());
                throw new RuntimeException(e);
            }
        }}).collect(Collectors.toList()));
    }


    /**
     * 获取支持的解析类型
     * @return JSON
     */
    @RequestMapping("/types")
    public RUtils types(){
        return RUtils.success("操作成功",types);
    }

    /**
     * 更新解析地址
     * @param type 域名类型
     * @param hostname 域名
     * @param address  地址
     * @return JSON
     */
    @RequestMapping("/update")
    public RUtils update(@RequestParam Integer type, @RequestParam String hostname, @RequestParam String address) {
        try{

            // 先解析成hostname
            InetSocketAddress hostnameAddress = InetSocketAddress.createUnresolved(hostname,80);
            String socketHostname = String.format("%s.",hostnameAddress.getHostName().toLowerCase());

            // 再解析IP地址
            InetAddress ipAddress = SocketUtils.addressByName(address);

            // 打印日志
            logger.info("申请更新解析[{}]: {} -> {}",type,socketHostname,ipAddress.getHostAddress());

            // 清除原先解析记录
            DnsServer dnsServer = BeanUtil.getBean(DnsServerImpl.class);
            dnsServer.removeHost(type,socketHostname);

            // 保存新的解析记录
            dnsServer.saveHost(type,socketHostname,ipAddress.getAddress());
        }catch (UnknownHostException e){
            logger.error(e.toString());
            return RUtils.error("写入错误");
        }

        return RUtils.success("操作完成");
    }


    /**
     * 删除域名解析
     * @param type
     * @param hostname
     * @return
     */
    @RequestMapping("/remove")
    public RUtils remove(@RequestParam Integer type, @RequestParam String hostname){

        // 先解析成hostname
        InetSocketAddress hostnameAddress = InetSocketAddress.createUnresolved(hostname, 80);

        // 打印日志
        logger.info("申请删除解析[{}]: {}", type, hostnameAddress.getHostName().toLowerCase());

        // 清除原先解析记录
        DnsServer dnsServer = BeanUtil.getBean(DnsServerImpl.class);
        if(dnsServer.removeHost(type,hostnameAddress.getHostName().toLowerCase())){
            return RUtils.success("操作成功");
        }else{
            return RUtils.error("操作失败");
        }
    }

}
