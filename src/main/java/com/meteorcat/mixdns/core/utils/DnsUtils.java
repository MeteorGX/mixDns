package com.meteorcat.mixdns.core.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.dns.DefaultDnsRawRecord;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import org.springframework.lang.NonNull;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

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


    /**
     * 写入Host地址转为 ByteBuf
     * @param address Socket地址
     * @return ByteBuf
     */
    public static ByteBuf writeHostAddress(@NonNull InetAddress address){
        return Unpooled.wrappedBuffer(address.getAddress());
    }


    /**
     * 写入TXT信息转为 ByteBuf
     * @param url TXT信息
     * @return ByteBuf
     */
    public static ByteBuf writeTxtAddress(@NonNull String url){
        byte[] bytes = url.getBytes(StandardCharsets.US_ASCII);
        int maxBytes = Math.min(255,bytes.length);
        return Unpooled.buffer(maxBytes + 1)
                .writeByte(maxBytes)
                .writeBytes(bytes,0,maxBytes);
    }


    /**
     * 写入DNS解析
     * @param hostname 解析域名
     * @return ByteBuf
     */
    public static ByteBuf writeDnsName(@NonNull String hostname,@NonNull ByteBuf byteBuf){
        if(".".equals(hostname)){
            byteBuf.writeByte(0);
            return byteBuf;
        }

        String[] hosts = hostname.split("\\.");
        for (String host:hosts) {
            int sz = host.length();
            if(sz > 63){
                throw new IllegalArgumentException(String.format(
                        "Can't Parse Hostname [%s]; Host Length = %d",hostname,sz
                ));
            }
            if(sz == 0){
                break;
            }
            byteBuf.writeByte(sz);
            ByteBufUtil.writeAscii(byteBuf,host);
        }

        byteBuf.writeByte(0);
        return byteBuf;
    }


    /**
     * 创建 A|AAAA 域名解析记录
     * @param hostname 域名
     * @param address Socket地址
     * @param ttl 缓存周期
     * @return DnsRecord
     */
    public static DnsRecord createHostRecord(@NonNull String hostname,@NonNull InetAddress address,int ttl){
        return new DefaultDnsRawRecord(
                hostname,
                isIpv6Address(address) ? DnsRecordType.AAAA : DnsRecordType.A,
                ttl,
                writeHostAddress(address)
        );
    }


    /**
     * 创建 TXT 解析记录
     * @param hostname 域名
     * @param url txt信息
     * @param ttl 缓存周期
     * @return DnsRecord
     */
    public static DnsRecord createTxtRecord(@NonNull String hostname,@NonNull String url,int ttl){
        return new DefaultDnsRawRecord(
                hostname,
                DnsRecordType.TXT,
                ttl,
                writeTxtAddress(url)
        );
    }

    /**
     * 创建 NS 解析记录
     * @param hostname 域名
     * @param address 地址域名
     * @param ttl 缓存周期
     * @return
     */
    public static DnsRecord createNsRecord(@NonNull String hostname,@NonNull String address,int ttl){
        return new DefaultDnsRawRecord(
                hostname,
                DnsRecordType.NS,
                ttl,
                writeDnsName(address,Unpooled.buffer())
        );
    }



}
