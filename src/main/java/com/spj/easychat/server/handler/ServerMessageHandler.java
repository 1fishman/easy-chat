package com.spj.easychat.server.handler;

import com.spj.easychat.common.entity.CommonMessage;
import com.spj.easychat.common.entity.HeartMessage;
import com.spj.easychat.common.entity.Message;
import com.spj.easychat.common.entity.Status;
import com.spj.easychat.server.ChatEventLoop;
import com.spj.easychat.server.DefaultMessageHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableListableBeanFactory.SCOPE_PROTOTYPE)
public class ServerMessageHandler extends SimpleChannelInboundHandler {

    private static final Logger log = LoggerFactory.getLogger(ServerMessageHandler.class);


    @Autowired
    private DefaultMessageHandler messageHandler ;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        if (o instanceof Message){
            Object obj =((Message)o).getMsg();
            if (obj instanceof HeartMessage){
                HeartMessage heartMessage = (HeartMessage)obj;
                doHeartMessage(heartMessage,ctx);
            }else if(obj instanceof CommonMessage){
                CommonMessage msg = (CommonMessage)obj;
                log.info(msg.toString());
                handleCommonMessage(msg,ctx);
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        super.channelActive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE){
                log.info("有一定的时间没有读取消息了");
                messageHandler.logout(ctx.channel());
                //ctx.writeAndFlush(new Message(new HeartMessage(1)));
            }else if (e.state() == IdleState.WRITER_IDLE){
                ctx.writeAndFlush(new Message(new HeartMessage(1)));
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        messageHandler.logout(ctx.channel());
        //String userName = onlineUser.get(ctx.channel());
        //onlineUser.remove(ctx.channel());
        //userChannel.remove(userName);
        super.channelInactive(ctx);
    }

    public void handleCommonMessage(CommonMessage commonMessage, ChannelHandlerContext ctx){
        if (commonMessage.getType() == 1){
            switch (commonMessage.getCommand()){
                case LOGIN:{
                    login(ctx,commonMessage);
                }
            }
        }else{
            if (commonMessage.getToUser() != null){
                log.info("单发消息: {} -> {}",commonMessage.getFromUser(),commonMessage.getToUser());
                messageHandler.sendMessage(commonMessage);
            }else{
                messageHandler.broadcast(commonMessage);
            }
        }
    }

    public void login(ChannelHandlerContext ctx,CommonMessage commonMessage){
        String[] userInfo = commonMessage.getMsg().split(":");
        if (userInfo.length != 2){
            ctx.writeAndFlush(new Message((Status.AUTHENTICATIONERROT)));
        }
        log.info("userInfo[1] {}, 2{}",userInfo[0],userInfo[1]);
        ChatEventLoop.executor(new Runnable() {
            @Override
            public void run() {
                messageHandler.login(userInfo[0],userInfo[1],ctx.channel());
            }
        });
    }

    public void doHeartMessage(HeartMessage heartMessage,ChannelHandlerContext ctx){
        if (heartMessage.getType() == 1){
            HeartMessage heartMsg = new HeartMessage(0);
            Message res = new Message(heartMsg);
            ctx.writeAndFlush(res);
        }else{
            //TODO 更新用户的连接时间, 用户不需要发送心跳包,服务器发送就好了.
        }
    }


}
