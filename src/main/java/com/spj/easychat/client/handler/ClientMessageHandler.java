package com.spj.easychat.client.handler;

import com.spj.easychat.client.Client;
import com.spj.easychat.client.State;
import com.spj.easychat.common.entity.CommonMessage;
import com.spj.easychat.common.entity.HeartMessage;
import com.spj.easychat.common.entity.Message;
import com.spj.easychat.common.entity.Status;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientMessageHandler extends SimpleChannelInboundHandler {
    private Client client;

    public ClientMessageHandler(Client client){
        this.client = client;
    }

    private static final Logger log = LoggerFactory.getLogger("client>>>:");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        if (o instanceof Message){
            Object obj =((Message)o).getMsg();
            if (obj instanceof HeartMessage){
                HeartMessage heartMessage = (HeartMessage)obj;
                heartMessage.setType(2);
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
                    client.downLatch();
                    client.setState(State.NOLMAL);
                    log.info("登录成功,可以发送消息了");
                }else if (obj.equals(Status.NOT_LOGIN_ERROR)){
                    log.info("还未登录,请重新登录");
                }

            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("连接成功");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.WRITER_IDLE){
                //DefaultMessageHandler.logout(ctx.channel());
                ctx.writeAndFlush(new Message(new HeartMessage(1)));
            }
        }
    }
}
