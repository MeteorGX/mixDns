package com.meteorcat.mixdns.core.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;

/**
 * @author MeteorCat
 */
public interface INetty extends IServer{


    /**
     * 获取Netty引导器
     * @return Bootstrap
     */
    Bootstrap getBoostrap();

    /**
     * 设置Netty引导器
     * @param boostrap Bootstrap
     */
    void setBoostrap(Bootstrap boostrap);


    /**
     * 获取事件循环
     * @return EventLoopGroup
     */
    EventLoopGroup getEventLoopGroup();

    /**
     * 设置事件循环
     * @param eventLoopGroup 事件队列
     */
    void setEventLoopGroup(EventLoopGroup eventLoopGroup);


}
