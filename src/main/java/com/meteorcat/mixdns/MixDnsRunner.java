package com.meteorcat.mixdns;

import com.meteorcat.mixdns.core.AbstractDnsListener;
import com.meteorcat.mixdns.core.utils.BeanUtil;
import com.meteorcat.mixdns.process.DnsListener;
import com.meteorcat.mixdns.process.MixConfig;
import com.meteorcat.mixdns.process.MixFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.dns.DefaultDnsRawRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

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

        try{
            // 创建监听器
            AbstractDnsListener listener = ctx.getBean(DnsListener.class);

            // 开启监听服务

            listener.start(config.getPort());
        }catch (Exception exception){
            logger.error(exception.toString());
        }

    }
}
