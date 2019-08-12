package com.spj.easychat.chat;

import com.spj.easychat.chat.server.handler.ServerMessageHandler;
import com.spj.easychat.common.Message;
import com.spj.easychat.common.codec.MsgDecoder;
import com.spj.easychat.common.codec.MsgEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@ConfigurationProperties(prefix = "chat.webserver")
public class Server implements InitializingBean , DisposableBean {

    public static final Logger log = LoggerFactory.getLogger(Server.class);

    private int port;

    private ServerBootstrap serverBootstrap;
    private EventLoopGroup group;
    private EventLoopGroup workGroup;

    private ChannelFuture cf ;

    public Server() {
        serverBootstrap = new ServerBootstrap();
        group = new NioEventLoopGroup();
        workGroup = new NioEventLoopGroup();
        serverBootstrap.group(group,workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new MsgDecoder(Message.class)
                        ,new MsgEncoder(Message.class)
                        , new ServerMessageHandler());
                    }
                })
                .option(ChannelOption.SO_BACKLOG,1024)
                .option(ChannelOption.SO_RCVBUF,1024)
                .option(ChannelOption.SO_SNDBUF,1024)
                .option(ChannelOption.TCP_NODELAY,true)
                .childOption(ChannelOption.SO_KEEPALIVE,true);
    }

    public void shutdown(){
        group.shutdownGracefully();
        workGroup.shutdownGracefully();

    }


    public void run() throws InterruptedException {
         cf = serverBootstrap.bind("0.0.0.0",port);
         log.info("服务器启动与端口: {}",port);
         cf.channel().closeFuture().sync();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        run();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ChannelFuture getCf() {
        return cf;
    }

    @Override
    public void destroy() throws Exception {
        shutdown();
        log.info("服务器关闭");
    }
}
