package com.meteorcat.mixdns.core.hooks;

import com.meteorcat.mixdns.core.utils.RUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;


/**
 * RestApi请求拦截错误返回
 * @author MeteorCat
 */
@Slf4j
@RestControllerAdvice
public class RestControllerAdviceConfig {


    /**
     * 常规的错误拦截
     * @param e 错误句柄
     * @return JSON
     */
    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e){
        log.error("[Hook] Exception = {}",e.getMessage());
        return RUtils.error("Server Error");
    }


    /**
     * 匹配不到控制器的返回
     * @param e 错误句柄
     * @return JSON
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Object handleNoHandlerFoundException(NoHandlerFoundException e){
        log.error("[Hook] NoHandlerFoundException = {}",e.getMessage());
        return RUtils.error("Server Error");
    }



}
