package com.meteorcat.mixdns.core.utils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * JSON响应对象
 * @author MeteorCat
 */
@Data
public class RUtils implements Serializable {

    /**
     * 常规成功
     */
    public static Integer SUCCESS = 0;

    /**
     * 常规失败
     */
    public static Integer ERROR = 1;

    /**
     * 状态
     */
    private Integer status;


    /**
     * 消息
     */
    private String message;


    /**
     * 数据体
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;

    /**
     * 日期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date datetime = new Date();


    /**
     * 初始化方法
     * @param status 状态码
     * @param message 状态消息
     * @param data 返回数据
     */
    public RUtils(Integer status, String message,Object data){
        this.status = status;
        this.message = message;
        this.data = data;
    }


    /**
     * 自定义返回
     * @param status 状态码
     * @param message 状态消息
     * @param data 返回数据
     * @return 返回数据
     */
    public static RUtils custom(Integer status,String message,Object data){
        return new RUtils(status,message,data);
    }

    /**
     * 成功返回
     * @param message 状态消息
     * @param data 返回数据
     * @return 返回数据
     */
    public static RUtils success(String message,Object data){
        return new RUtils(RUtils.SUCCESS,message,data);
    }

    /**
     * 成功返回
     * @param message 状态消息
     * @return 返回数据
     */
    public static RUtils success(String message){
        return RUtils.success(message,null);
    }

    /**
     * 错误返回
     * @param message 状态消息
     * @param data 返回数据
     * @return 返回数据
     */
    public static RUtils error(String message,Object data){
        return new RUtils(RUtils.ERROR,message,data);
    }


    /**
     * 错误返回
     * @param message 返回数据
     * @return 返回数据
     */
    public static RUtils error(String message){
        return RUtils.error(message,null);
    }

}
