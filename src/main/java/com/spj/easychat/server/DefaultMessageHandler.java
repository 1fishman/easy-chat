package com.spj.easychat.server;

import com.spj.easychat.common.entity.CommonMessage;
import com.spj.easychat.common.entity.Message;
import com.spj.easychat.common.entity.Status;
import com.spj.easychat.common.entity.User;
import com.spj.easychat.server.dao.UserMapper;
import io.netty.channel.Channel;
import org.apache.ibatis.annotations.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

class UserChannel{

    private String userName;
    private Channel channel;

    public UserChannel(String userName, Channel channel) {
        this.userName = userName;
        this.channel = channel;
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
}

@Component
public class DefaultMessageHandler{
    private static final Logger log = LoggerFactory.getLogger(DefaultMessageHandler.class);

    // 存储Channel到在线用户的映射
    private ConcurrentHashMap<Channel,String> channelUser = new ConcurrentHashMap<>();

    // 存储用户到Channel的映射
    private ConcurrentHashMap<String, UserChannel> userChannelMap = new ConcurrentHashMap<>();


    // 存储用户监听的群组,键是用户,值是群名
    private ConcurrentHashMap<String,String> groupOfUserListener = new ConcurrentHashMap<>();


    private CopyOnWriteArrayList<UserChannel> userChannelList = new CopyOnWriteArrayList<>();

    private ReentrantLock lock = new ReentrantLock();

    @Autowired
    private UserMapper userMapper;


    /**
     * 获取在线用户列表
     * @return List
     */
    /*public static List<String> getOnlineUser(){
        List<String> ls = new ArrayList<>();
        for (String s : userChannelMap.keySet()){
            if (userChannelMap.get(s).isActive()){
                ls.add(s);
            }else{
                userChannelMap.get(s).close();
            }
        }
        return ls;
    }*/

    /**
     * 获取用户的socketChannel
     * @param userName
     * @return Channel
     */
    private Channel getChannel(String userName){
        return userChannelMap.get(userName).getChannel();
    }

    private String getUserName(Channel ch){
        return channelUser.get(ch);
    }

    /**
     * 登录操作
     * @param  userName
     * @param channel
     * @return boolean
     */
    public void login(String userName,String pass,Channel channel){
        log.info("用户名:{}",userName);
        if (judgeUser(userName,pass)){
            log.info("channel is null ? {},{}",channel,userName );
            UserChannel userChannel = new UserChannel(userName,channel);
            channelUser.put(channel,userName);
            userChannelMap.put(userName,userChannel);
            userChannelList.add(userChannel);
            channel.writeAndFlush(new Message(Status.SUCCESS));
        }else{
            channel.writeAndFlush(new Message(Status.AUTHENTICATIONERROT));
        }


    }

    public void logout(Channel channel){
        String userName = channelUser.get(channel);
        logout(userName);
    }

    public void logout(String userName){
        UserChannel userChannel = userChannelMap.get(userName);
        userChannelMap.remove(userName);
        userChannelList.remove(userChannel);
        channelUser.remove(userChannel.getChannel());
        userChannel.getChannel().close();

    }

    /**
     * 判断用户名与密码是否正确
     * @param userName
     * @param pass
     * @return
     */
    private boolean judgeUser(String userName,String pass){
        //TODO 待做, 从数据库中查找用户名与密码,然后查找
        User user = userMapper.getUserByName(userName);
        if (user.getPass().equals(pass)){
            return true;
        }
        return false;
    }

    public void sendMessage(CommonMessage message){
        Message sendmsg = new Message(message);
        Channel ch = getChannel(message.getToUser());
        ch.writeAndFlush(sendmsg);
    }


    public void broadcast(CommonMessage commonMessage){
        //CommonMessage commonMessage = new CommonMessage(fromUser,null,msg);
        Message message = new Message(commonMessage);
        for (UserChannel user : userChannelList){
            Channel ch = user.getChannel();
            if (ch.isActive()){
                ch.writeAndFlush(message);
            }
        }
    }



}
