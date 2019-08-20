package com.spj.easychat.client;

import com.spj.easychat.client.handler.ClientMessageHandler;
import com.spj.easychat.common.entity.CommandEnum;
import com.spj.easychat.common.entity.CommonMessage;
import com.spj.easychat.common.entity.Message;
import com.spj.easychat.common.codec.MsgDecoder;
import com.spj.easychat.common.codec.MsgEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Client {

    private static final Logger log = LoggerFactory.getLogger("client>>>:");

    private CountDownLatch latch;
    private State state;

    private String userName;
    private String pass;
    private Channel channel;
    private String remoteAddr;
    private int port;

    private Bootstrap bootstrap;
    private EventLoopGroup workerGroup;

    public void run(Client client){
        this.workerGroup = new NioEventLoopGroup(1);
        this.bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(10,0,0)
                                ,new MsgDecoder(Message.class),
                                new MsgEncoder(Message.class),
                                new ClientMessageHandler(client));
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
        this.remoteAddr = addr[0];
        this.port = Integer.valueOf(addr[1]);
        log.info("连接服务器地址: {}, 端口: {}",addr[0],addr[1]);
        ChannelFuture cf = connect(addr[0],Integer.valueOf(addr[1]));
        cf.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.channel().isActive()){
                    CommonMessage message = new CommonMessage(1, CommandEnum.LOGIN);
                    message.setMsg(userName+":" + pass);
                    Message sendMessage = new Message(message);
                    channelFuture.channel().writeAndFlush(sendMessage);
                    state = State.LOGINWAIT;
                }else{
                    log.info("连接失败11111");
                    System.exit(1);
                }

            }
        });
        await();
    }

    public void register(String remote,String userName,String pass) throws InterruptedException {

        String addr[] = remote.split(":");
        this.remoteAddr = addr[0];
        this.port = Integer.valueOf(addr[1]);
        ChannelFuture cf = connect(remoteAddr,port);
        cf.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.channel().isActive()){
                    CommonMessage message = new CommonMessage(1, CommandEnum.REGISTER);
                    message.setMsg(userName+":" + pass);
                    Message sendMessage = new Message(message);
                    channelFuture.channel().writeAndFlush(sendMessage);
                    state = State.REGISTERWAIT;
                }else{
                    log.info("连接失败11111");
                    System.exit(1);
                }

            }
        });
        await();
    }

    public void handler(String msg){
        if (!channel.isActive()){
            System.exit(999);
        }
        if (msg.length() != 0 && msg.charAt(0) == '@'){
            int nameIndex = msg.indexOf(':');
            if (nameIndex == -1){
                log.info("消息格式不合法");
            }
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
            }else if (msg.startsWith("list")){
                CommonMessage message = new CommonMessage(1, CommandEnum.LIST);
                Message sendMessage = new Message(message);
                int index = msg.indexOf(':');
                message.setFromUser(userName);
                if (index != -1){
                    message.setMsg(msg.substring(index+1));
                }
                channel.writeAndFlush(sendMessage);
                return;
            }
            CommonMessage msg1 = new CommonMessage(userName,null,msg);
            channel.writeAndFlush(new Message(msg1));
        }
    }

    public void await() throws InterruptedException {
        latch.await(10, TimeUnit.SECONDS);
    }

    public void downLatch(){
        latch.countDown();
        latch = new CountDownLatch(1);
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length < 3){
            log.info("参数个数错误,正确格式为,地址:端口号 用户名 密码");
            System.exit(1);
        }
        Client client = new Client();
        client.run(client);
        client.setLatch(new CountDownLatch(1));
        if (args[0].equals("register")){
            client.register(args[0],args[1],args[2]);
            client.setUserName(args[2]);
            client.setPass(args[3]);
        }else{
            client.setRemoteAddr(args[0].split(":")[0]);
            client.setPort(Integer.valueOf(args[0].split(":")[1]));
            client.setUserName(args[1]);
            client.setPass(args[2]);
        }

        try {
            client.login(args[0],args[1],args[2]);
        } catch (InterruptedException e) {
            log.info("连接失败");
            System.exit(1);
        }
        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()){
            String msg = sc.next();
            if (client.getState() != State.NOLMAL){
                log.info("服务器无响应或网络不好,请稍后再试");
                System.exit(404);
            }
            msg = msg.trim();
            client.handler(msg);
            //client.channel.writeAndFlush(new Message(new CommonMessage(client.userName,null,msg)));
        }
    }
}

