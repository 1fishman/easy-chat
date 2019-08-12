package com.spj.easychat.chat;

import com.spj.easychat.common.CommonMessage;
import com.spj.easychat.common.HeartMessage;
import com.spj.easychat.common.Message;

import java.util.concurrent.ConcurrentHashMap;

public class DefaultMessageHandler implements MessageHandler {

    private static final ConcurrentHashMap<String,Long> map = new ConcurrentHashMap<>();


    @Override
    public void handleMessage(Message msg) {

    }
}
