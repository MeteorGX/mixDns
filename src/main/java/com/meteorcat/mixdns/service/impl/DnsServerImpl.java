package com.meteorcat.mixdns.service.impl;

import com.meteorcat.mixdns.defines.DnsStatusDefine;
import com.meteorcat.mixdns.model.DnsModel;
import com.meteorcat.mixdns.service.DnsServer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author MeteorCat
 */
@Service
public class DnsServerImpl implements DnsServer {

    private final List<DnsModel> model = new ArrayList<>();

    @Override
    public void saveHost(Integer type, String hostname, byte[] bytes) {
        Integer sec = Math.toIntExact(System.currentTimeMillis() / 1000);
        DnsModel record = new DnsModel();
        record.setType(type);
        record.setHostname(hostname);
        record.setBytes(bytes);
        record.setCreateTime(sec);
        record.setStatus(DnsStatusDefine.Resolved.ordinal());
        model.add(record);
    }

    @Override
    public Optional<ByteBuf> findHostAddress(String hostname) {
        Optional<DnsModel> exists = model.stream().filter(active -> active.getHostname().equals(hostname)).findFirst();
        if(exists.isPresent()){
            return Optional.empty();
        }
        return Optional.empty();
    }

    @Override
    public Optional<DnsModel> findHostAddress(Integer type, String hostname) {
        return model.stream().filter(active -> active.getHostname().equals(hostname) && active.getType().equals(type)).findFirst();
    }


    @Override
    public List<DnsModel> getList() {
        return model;
    }



    @Override
    public boolean removeHost(Integer type, String hostname) {
        return model.removeIf(active->active.getHostname().equals(hostname) && active.getType().equals(type));
    }



    @Override
    public String toString() {
        return this.model.toString();
    }
}
