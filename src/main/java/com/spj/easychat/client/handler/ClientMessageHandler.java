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

    private static final Logger log = LoggerFactory.getLogger("");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {

        if (o instanceof Message){
            Object obj =((Message)o).getMsg();
            if (obj instanceof HeartMessage){
                HeartMessage heartMessage = (HeartMessage)obj;
                heartMessage.setType(2);
                ctx.writeAndFlush(new Message(heartMessage));
            }else if(obj instanceof CommonMessage){
                CommonMessage msg = (CommonMessage)obj;
                log.info("{}:{}: {} ",msg.getSendTime(),msg.getFromUser(),msg.getMsg());
            }else if (obj instanceof Status){
                if (obj.equals(Status.LOGINSUCCESS)){
                    client.downLatch();
                    client.setState(State.NOLMAL);
                    log.info(((Status) obj).getMsg());
                }else if (obj.equals(Status.REGISTERSUCCESS)){
                    log.info(((Status) obj).getMsg());
                    client.downLatch();
                    client.setState(State.NOLMAL);
                }else if (obj.equals(Status.NOT_LOGIN_ERROR)){
                    log.info(((Status) obj).getMsg());
                    System.exit(((Status) obj).getStatusNo());
                }else if (obj.equals(Status.USERNAMEEXIST)){
                    log.info(((Status) obj).getMsg());
                    System.exit(((Status) obj).getStatusNo());
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
            if (e.state() == IdleState.READER_IDLE){
                ctx.close();
                client.login(client.getRemoteAddr()+":"+client.getPort(),client.getUserName(),client.getPass());
            }
        }
    }
}
