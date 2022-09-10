package com.meteorcat.mixdns.core.utils;

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
    private Object data;

    /**
     * 日期时间
     */
    private Date datetime = new Date();


    public RUtils(Integer status, String message,Object data){
        this.status = status;
        this.message = message;
        this.data = data;
    }


    public static RUtils success(String message,Object data){
        return new RUtils(RUtils.SUCCESS,message,data);
    }

    public static RUtils success(String message){
        return RUtils.success(message,null);
    }

    public static RUtils error(String message,Object data){
        return new RUtils(RUtils.ERROR,message,data);
    }

    public static RUtils error(String message){
        return RUtils.error(message,null);
    }




}
