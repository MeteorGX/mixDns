package com.meteorcat.mixdns.process;

import com.meteorcat.mixdns.core.AbstractDnsListener;
import com.meteorcat.mixdns.core.AbstractDnsResolver;
import com.meteorcat.mixdns.service.DnsServer;
import com.meteorcat.mixdns.service.impl.DnsServerImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author MeteorCat
 */
@Component
public class MixFactory {


    /**
     * DNS服务器
     * @param dnsServer DNS服务类
     * @return DnsServer
     */
    @Resource
    @Lazy
    @Bean
    public DnsServer dnsServer(DnsServerImpl dnsServer){
        return dnsServer;
    }



    @Bean(initMethod = "init",destroyMethod = "close")
    public static AbstractDnsListener dnsListener(){
        return new DnsListener();
    }


    @Bean(initMethod = "init",destroyMethod = "close")
    public static AbstractDnsResolver dnsResolver() {
        return new DnsResolver();
    }


}
