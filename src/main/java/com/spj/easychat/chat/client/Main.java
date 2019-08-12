package com.spj.easychat.chat.client;

import com.spj.easychat.chat.client.handler.ClientMessageHandler;
import com.spj.easychat.common.CommandEnum;
import com.spj.easychat.common.CommonMessage;
import com.spj.easychat.common.Message;
import com.spj.easychat.common.codec.MsgDecoder;
import com.spj.easychat.common.codec.MsgEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class Main {

    private static final Logger log = LoggerFactory.getLogger("client>>>:");

    private  static CountDownLatch latch;

    private String userName;
    private Channel channel;


    private Bootstrap bootstrap;
    private EventLoopGroup workerGroup;
    public void run(){
        this.workerGroup = new NioEventLoopGroup(1);
        this.bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new MsgDecoder(Message.class),
                                new MsgEncoder(Message.class),
                                new ClientMessageHandler());
                    }
                })
                .option(ChannelOption.SO_RCVBUF,1024)
                .option(ChannelOption.SO_SNDBUF,1024)
                .option(ChannelOption.TCP_NODELAY,true);
    }
    public ChannelFuture connect(String addr,int port) throws InterruptedException {
        ChannelFuture cf = bootstrap.connect(addr,port);
        channel = cf.channel();
        return cf;
    }

    // 登录操作
    public void login(String remote,String userName,String pass) throws InterruptedException {
        String addr[] = remote.split(":");
        log.info("连接服务器地址: {}, 端口: {}",addr[0],addr[1]);
        ChannelFuture cf = connect(addr[0],Integer.valueOf(addr[1]));
        cf.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.channel().isActive()){
                    log.info("向服务器发送消息");
                    CommonMessage message = new CommonMessage(1, CommandEnum.LOGIN);
                    message.setMsg(userName+":" + pass);
                    Message sendMessage = new Message(message);
                    channelFuture.channel().writeAndFlush(sendMessage);
                }else{
                    log.info("连接失败11111");
                    System.exit(1);
                }

            }
        });
        cf.channel().closeFuture().sync();
    }


    public void handler(String msg){
        if (!channel.isActive()){
            System.exit(999);
        }
        if (msg.length() != 0 && msg.charAt(0) == '@'){
            int nameIndex = msg.indexOf(':');
            String toUserName = msg.substring(1,nameIndex);
            String sendMsg = msg.substring(nameIndex+1);
            CommonMessage msg1 = new CommonMessage(userName,toUserName,sendMsg);
            channel.writeAndFlush(new Message(msg1));
        }else{
            if (msg.equals("exit")){
                channel.close().addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        log.info("退出聊天");
                        System.exit(0);
                    }
                });
            }
            CommonMessage msg1 = new CommonMessage(userName,null,msg);
            channel.writeAndFlush(new Message(msg1));
        }
    }

    public static void downLatch(){
        latch.countDown();
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length < 3){
            log.info("参数个数错误,正确格式为,地址:端口号 用户名 密码");
            System.exit(1);
        }
        latch = new CountDownLatch(1);
        Main client = new Main();
        client.userName = args[1];
        client.run();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.login(args[0],args[1],args[2]);
                } catch (InterruptedException e) {
                    log.info("连接失败");
                    System.exit(1);
                }
            }
        }).start();
        log.info("开始等待");
        latch.await();
        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()){

            String msg = sc.nextLine();
            log.info("发送消息 {}" ,msg );
            client.handler(msg);
            //client.channel.writeAndFlush(new Message(new CommonMessage(client.userName,null,msg)));
        }
    }
}

