package com.meteorcat.mixdns.process;

import com.meteorcat.mixdns.core.AbstractDnsListener;
import com.meteorcat.mixdns.core.utils.BeanUtil;
import com.meteorcat.mixdns.process.protocols.DnsProtocol;
import com.meteorcat.mixdns.service.DnsServer;
import com.meteorcat.mixdns.service.impl.DnsServerImpl;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.dns.DatagramDnsQueryDecoder;
import io.netty.handler.codec.dns.DatagramDnsQueryEncoder;
import io.netty.handler.codec.dns.DatagramDnsResponseEncoder;
import io.netty.resolver.NoopAddressResolverGroup;

import javax.annotation.Resource;

/**
 * @author MeteorCat
 */
public class DnsListener extends AbstractDnsListener {


    public static class DnsChannelInitializer extends ChannelInitializer<NioDatagramChannel>{


        @Override
        protected void initChannel(NioDatagramChannel nioDatagramChannel) throws Exception {
            ChannelPipeline pipeline = nioDatagramChannel.pipeline();
            pipeline.addLast(new DatagramDnsQueryDecoder());
            pipeline.addLast(new DatagramDnsResponseEncoder());
            pipeline.addLast(new DnsProtocol());
        }
    }

    @Override
    public void init() throws Exception {
        super.init();

        // 配置设置
        MixConfig config = BeanUtil.getBean(MixConfig.class);

        setBoostrap(new Bootstrap());
        setEventLoopGroup(new NioEventLoopGroup(config.getListenThread()));
        getBoostrap().group(getEventLoopGroup())
                .resolver(NoopAddressResolverGroup.INSTANCE)
                .option(ChannelOption.SO_REUSEADDR,true)
                .option(ChannelOption.SO_BROADCAST,true)
                .option(ChannelOption.SO_RCVBUF,config.getRcvBuf())
                .option(ChannelOption.SO_SNDBUF,config.getSndBuf())
                .channel(NioDatagramChannel.class)
                .handler(new DnsChannelInitializer());
    }

    @Override
    public void start(int port) throws Exception {
        getBoostrap().bind(port);
    }
}
