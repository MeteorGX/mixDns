package com.meteorcat.mixdns.process;

import com.meteorcat.mixdns.core.AbstractDnsResolver;
import com.meteorcat.mixdns.core.utils.BeanUtil;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.resolver.DefaultAddressResolverGroup;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author MeteorCat
 */
public class DnsResolver extends AbstractDnsResolver {

    public static int DEFAULT_HOST_PORT = 80;

    @Getter
    @Setter
    private EventLoopGroup eventLoopGroup;

    @Getter
    @Setter
    private DefaultAddressResolverGroup resolverGroup;


    public void init() {

        // 配置设置
        MixConfig config = BeanUtil.getBean(MixConfig.class);
        eventLoopGroup = new NioEventLoopGroup(config.getResolveThread());
        resolverGroup = DefaultAddressResolverGroup.INSTANCE;
    }


    @Override
    public List<InetSocketAddress> resolver(String name) throws ExecutionException, InterruptedException {
        return resolverGroup
                .getResolver(eventLoopGroup.next())
                .resolveAll(InetSocketAddress.createUnresolved(name,DEFAULT_HOST_PORT))
                .get();
    }

}
