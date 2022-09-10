package com.meteorcat.mixdns.core.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author MeteorCat
 */
@Component
public class BeanUtil implements ApplicationContextAware {


    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BeanUtil.applicationContext = applicationContext;
    }


    public static Object getBean(String name){
        return name != null && BeanUtil.applicationContext != null ? BeanUtil.applicationContext.getBean(name) : null;
    }

    public static <T> T getBean(Class<T> clazz){
        return clazz != null && BeanUtil.applicationContext != null ? BeanUtil.applicationContext.getBean(clazz) : null;
    }

}
