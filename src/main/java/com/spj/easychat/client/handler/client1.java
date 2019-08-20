package com.spj.easychat.client.handler;

import com.spj.easychat.client.Client;
import com.spj.easychat.client.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class client1 {

    private static final Logger log = LoggerFactory.getLogger("client>>>:");

    public static void main(String[] args) throws InterruptedException {
        if (args.length < 3){
            log.info("参数个数错误,正确格式为,地址:端口号 用户名 密码");
            System.exit(1);
        }
        Client client = new Client();
        client.run(client);
        client.setLatch(new CountDownLatch(1));
        if (args[0].equals("register")){
            client.register(args[1],args[2],args[3]);
            client.setUserName(args[2]);
            client.setPass(args[3]);
        }else{
            client.setUserName(args[1]);
            client.setPass(args[2]);
        }
        try {
            client.login(client.getRemoteAddr()+":"+client.getPort(),client.getUserName(),client.getPass());
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
