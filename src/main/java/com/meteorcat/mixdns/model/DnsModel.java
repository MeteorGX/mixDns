package com.meteorcat.mixdns.model;

import lombok.Data;

/**
 * @author MeteorCat
 */
@Data
public class DnsModel {

    /**
     * 解析类型
     */
    private Integer type;

    /**
     * 域名
     */
    private String hostname;


    /**
     * 域名指向IP返回
     */
    private byte[] bytes;


    /**
     * 创建时间
     */
    private Integer createTime;

    /**
     * 状态
     */
    private Integer status;

}
