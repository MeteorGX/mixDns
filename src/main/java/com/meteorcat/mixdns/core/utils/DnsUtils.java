package com.meteorcat.mixdns.core.utils;


import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;

/**
 * DNS工具
 * @author MeteorCat
 */
public class DnsUtils {


    /**
     * 判断是否为IPv6地址
     * @param address Socket地址
     * @return boolean
     */
    public static boolean isIpv6Address(InetAddress address){
        return address instanceof Inet6Address;
    }

    /**
     * 判断是否为IPv4地址
     * @param address Socket地址
     * @return boolean
     */
    public static boolean isIpv4Address(InetAddress address){
        return address instanceof Inet4Address;
    }


}
