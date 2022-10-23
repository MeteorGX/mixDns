package com.meteorcat.mixdns;

import com.meteorcat.mixdns.core.AbstractDnsListener;
import com.meteorcat.mixdns.core.utils.BeanUtil;
import com.meteorcat.mixdns.process.DnsListener;
import com.meteorcat.mixdns.process.MixConfig;
import com.meteorcat.mixdns.process.MixFactory;
import com.meteorcat.mixdns.service.DnsServer;
import com.meteorcat.mixdns.service.impl.DnsServerImpl;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.dns.DefaultDnsRawRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.util.internal.SocketUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * @author MeteorCat
 */
@Component
public class MixDnsRunner implements ApplicationRunner {

    /**
     * 日志句柄
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void run(ApplicationArguments args) {
        logger.info("MixDns Start");

        // 创建注入工厂
        AbstractApplicationContext ctx = new AnnotationConfigApplicationContext(MixFactory.class);
        ctx.registerShutdownHook();

        // 打印工厂内部端口信息
        MixConfig config = BeanUtil.getBean(MixConfig.class);
        logger.info("Listener Udp Port = {}", config.getPort());

        // 获取DNS配置
        parseResolveFile(config);


        try{
            // 创建监听器
            AbstractDnsListener listener = ctx.getBean(DnsListener.class);

            // 开启监听服务
            listener.start(config.getPort());
        }catch (Exception exception){
            logger.error(exception.toString());
        }

    }


    /**
     * 解析配置域名得地址
     * @param config MixConfig
     */
    public void parseResolveFile(MixConfig config){
        String resolveFilename = config.getResolveFile();
        resolveFilename = resolveFilename.trim();
        File file = new File(resolveFilename);
        if(!resolveFilename.isEmpty() && file.exists()){
            logger.info("Resolve File = {}",resolveFilename);
            try(FileReader reader = new FileReader(file)){
                BufferedReader bufferedReader = new BufferedReader(reader);

                // 读取行
                String line;
                while ((line = bufferedReader.readLine()) != null){
                    line = line.trim();
                    if(!line.isEmpty()){
                        int ignoreChar = line.charAt(0);

                        // 排除 `#` 开头得注释
                        if(ignoreChar == '#'){
                            continue;
                        }

                        // 过滤分段配置
                        String[] hosts = line.split(" ");
                        if(hosts.length != 3){
                            continue;
                        }

                        DnsRecordType type;
                        try {
                            type = DnsRecordType.valueOf(hosts[0]);
                        }catch (IllegalArgumentException exception){
                            logger.error(exception.getMessage());
                            continue;
                        }

                        // 再解析IP地址
                        InetAddress ipAddress = SocketUtils.addressByName(hosts[1]);

                        // 先解析成hostname
                        InetSocketAddress hostnameAddress = InetSocketAddress.createUnresolved(hosts[2],80);
                        String socketHostname = String.format("%s.",hostnameAddress.getHostName().toLowerCase());


                        // 获取解析记录
                        DnsServer dnsServer = BeanUtil.getBean(DnsServerImpl.class);
                        dnsServer.saveHost(type.intValue(),socketHostname,ipAddress.getAddress());
                        logger.info("[{}]: {} -> {}",type.intValue(),socketHostname,ipAddress.getHostAddress());
                    }
                }

            }catch (IOException exception){
                exception.printStackTrace();
                logger.error(exception.getMessage());
            }
        }
    }
}
