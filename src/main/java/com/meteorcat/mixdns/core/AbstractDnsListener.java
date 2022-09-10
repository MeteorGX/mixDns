package com.meteorcat.mixdns.core;

import com.meteorcat.mixdns.core.net.INetty;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;

/**
 * @author MeteorCat
 */
public abstract class AbstractDnsListener implements INetty {


    private Bootstrap bootstrap;


    private EventLoopGroup eventLoopGroup;

    @Override
    public void setBoostrap(Bootstrap boostrap) {
        this.bootstrap = boostrap;
    }

    @Override
    public Bootstrap getBoostrap() {
        return this.bootstrap;
    }


    @Override
    public void setEventLoopGroup(EventLoopGroup eventLoopGroup) {
        this.eventLoopGroup = eventLoopGroup;
    }

    @Override
    public EventLoopGroup getEventLoopGroup() {
        return eventLoopGroup;
    }

    @Override
    public void init() throws Exception {}

    @Override
    public void close() throws Exception {}




}
