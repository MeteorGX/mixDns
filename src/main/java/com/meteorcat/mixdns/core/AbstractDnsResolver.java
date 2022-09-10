package com.meteorcat.mixdns.core;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author MeteorCat
 */
public abstract class AbstractDnsResolver {

    public void init() throws Exception{}


    /**
     * 解析方法
     * @param name 域名
     * @return 地址列表
     * @throws Exception 异常
     */
    public abstract List<InetSocketAddress> resolver(String name) throws Exception;

    public void close() throws Exception{};

}
