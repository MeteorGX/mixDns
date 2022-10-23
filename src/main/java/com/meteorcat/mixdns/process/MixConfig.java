package com.meteorcat.mixdns.process;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author MeteorCat
 */
@Component
public class MixConfig {

    /**
     * DNS访问端口
     */
    @Getter
    @Value("${mix.dns.port}")
    private Integer port;


    /**
     * SO_SNDBUF 系统配置
     */
    @Getter
    @Value("${mix.dns.rcvbuf}")
    private Integer rcvBuf;


    /**
     * SO_RCVBUF 系统配置
     */
    @Getter
    @Value("${mix.dns.sndbuf}")
    private Integer sndBuf;


    /**
     * TTL 解析周期(秒)
     */
    @Getter
    @Value("${mix.dns.ttl}")
    private Integer ttl;

    /**
     * 处理监听DNS服务线程数
     */
    @Getter
    @Value("${mix.dns.listen.thread}")
    private Integer listenThread;


    /**
     * 处理解析DNS服务线程数
     */
    @Getter
    @Value("${mix.dns.resolve.thread}")
    private Integer resolveThread;


    /**
     * 处理解析DNS服务文件
     */
    @Getter
    @Value("${mix.dns.resolve.file}")
    private String resolveFile = "";

}
