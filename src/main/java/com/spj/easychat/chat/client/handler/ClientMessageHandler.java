package com.spj.easychat.chat.client.handler;

import com.spj.easychat.chat.client.Main;
import com.spj.easychat.common.CommonMessage;
import com.spj.easychat.common.HeartMessage;
import com.spj.easychat.common.Message;
import com.spj.easychat.common.Status;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientMessageHandler extends SimpleChannelInboundHandler {

    private static final Logger log = LoggerFactory.getLogger(ClientMessageHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        ;
        if (o instanceof Message){
            Object obj =((Message)o).getMsg();
            if (obj instanceof HeartMessage){
                HeartMessage heartMessage = (HeartMessage)obj;
                heartMessage.setType(1);
                ctx.writeAndFlush(new Message(heartMessage));
            }else if(obj instanceof CommonMessage){
                log.info("接收到服务器的应答消息 {}" ,((CommonMessage)obj).toString());
                CommonMessage msg = (CommonMessage)obj;
                if (msg.getType() == 1){
                    if (msg.getMsg().equals(Status.SUCCESS)){

                    }
                }else{
                    log.info("{}:来自{}的消息: {} ",((Message) o).getTime(),msg.getFromUser(),msg.getMsg());
                }
            }else if (obj instanceof Status){
                log.info("接收到服务器的应答消息 {}" ,(obj).toString());
                if (obj.equals(Status.SUCCESS)){
                    Main.downLatch();
                    log.info("登录成功,可以发送消息了");
                }else if (obj.equals(Status.NOT_LOGIN_ERROR)){

                }

            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("连接成功");
    }
}
