package com.spj.easychat;

import com.spj.easychat.common.CommonMessage;
import com.spj.easychat.common.Message;
import com.spj.easychat.common.Status;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.ReentrantLock;

public class UserInfoUtil {
    private static final Logger log = LoggerFactory.getLogger(UserInfoUtil.class);

    // 存储Channel到在线用户的映射
    private static ConcurrentHashMap<Channel,String> channelUser = new ConcurrentHashMap<>();

    // 存储用户到Channel的映射
    private static ConcurrentHashMap<String, UserChannel> userChannelMap = new ConcurrentHashMap<>();


    // 存储用户监听的群组,键是用户,值是群名
    private static ConcurrentHashMap<String,String> groupOfUserListener = new ConcurrentHashMap<>();


    private static CopyOnWriteArrayList<UserChannel> userChannelList = new CopyOnWriteArrayList<>();

    private static ReentrantLock lock = new ReentrantLock();


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
    private static Channel getChannel(String userName){
        return userChannelMap.get(userName).getChannel();
    }

    private static String getUserName(Channel ch){
        return channelUser.get(ch);
    }

    /**
     * 登录操作
     * @param  userName
     * @param channel
     * @return boolean
     */
    public static void login(String userName,String pass,Channel channel){
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

    public static void logout(Channel channel){
        String userName = channelUser.get(channel);
        logout(userName);
    }

    public static void logout(String userName){
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
    private static boolean judgeUser(String userName,String pass){
        //TODO 待做, 从数据库中查找用户名与密码,然后查找
        return true;
    }

    public static void sendMessage(String msg, @Nullable String fromUser, @Nullable String toUser){
        CommonMessage commonMessage = new CommonMessage(fromUser,toUser,msg);
        Message message = new Message(commonMessage);
        Channel ch = getChannel(toUser);
        ch.writeAndFlush(message);
    }


    public static void broadcast(String msg,String fromUser){
        CommonMessage commonMessage = new CommonMessage(fromUser,null,msg);
        Message message = new Message(commonMessage);
        for (UserChannel user : userChannelList){
            Channel ch = user.getChannel();
            if (ch.isActive()){
                ch.writeAndFlush(message);
            }
        }
    }



    static class UserChannel{

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


}
