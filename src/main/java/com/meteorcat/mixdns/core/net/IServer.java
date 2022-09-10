package com.meteorcat.mixdns.core.net;

/**
 * @author MeteorCat
 */
public interface IServer {

    /**
     * 初始化回调
     * @throws Exception 异常
     */
    void init() throws Exception;

    /**
     * 服务器启动
     * @param port 端口号
     * @throws Exception 异常
     */
    void start(int port) throws Exception;



    /**
     * 关闭回调
     * @throws Exception 异常
     */
    void close() throws Exception;
}
