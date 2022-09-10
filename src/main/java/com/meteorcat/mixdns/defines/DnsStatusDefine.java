package com.meteorcat.mixdns.defines;

import lombok.Getter;

import java.util.Arrays;

/**
 *
 * @author MeteorCat
 */
public enum DnsStatusDefine {

    /**
     * 默认未知错误
     */
    Unknown(0),

    /**
     * 逻辑删除
     */
    Removed(1),

    /**
     * 默认解析, 超时重新处理解析
     */
    Resolved(2),

    /**
     * 锁定解析, 后续域名直接锁定不再重新解析
     */
    Lock(3),


    /**
     * 将该域名直接封禁解析
     */
    Block(4),


    /**
     * 未知状态,占位符
     */
    MASK(99);

    @Getter
    private final Integer status;

    DnsStatusDefine(Integer status){
        this.status = status;
    }


    /**
     * 数值转化成Enum
     * @param status 状态
     * @return DnsStatusDefine
     */
    public static DnsStatusDefine toDnsLockDefine(Integer status){
        return Arrays.stream(DnsStatusDefine.values()).filter(active -> active.ordinal() == status).findFirst().orElse(DnsStatusDefine.Unknown);
    }
}
