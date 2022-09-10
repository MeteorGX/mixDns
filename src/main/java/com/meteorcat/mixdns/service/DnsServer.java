package com.meteorcat.mixdns.service;

import com.meteorcat.mixdns.model.DnsModel;
import io.netty.buffer.ByteBuf;

import java.util.List;
import java.util.Optional;

/**
 * @author MeteorCat
 */
public interface DnsServer {



    /**
     * 保存解析记录
     * @param type 域名类型
     * @param hostname 域名
     * @param bytes 地址二进制数据
     */
    void saveHost(Integer type,String hostname,byte[] bytes);


    /**
     * 检索内部的解析域名
     * @param hostname 域名
     * @return ByteBuf
     */
    Optional<ByteBuf> findHostAddress(String hostname);


    /**
     * 检索内部的解析域名
     * @param type 解析类型
     * @param hostname 域名
     * @return ByteBuf
     */
    Optional<DnsModel> findHostAddress(Integer type,String hostname);


    /**
     * 获取列表数据
     * @return List
     */
    List<DnsModel> getList();

    /**
     * 删除解析地址
     * @param type 域名类型
     * @param hostname 域名
     * @return boolean
     */
    boolean removeHost(Integer type,String hostname);


}
