package com.spj.easychat.chat;

import com.spj.easychat.common.entity.Message;

public interface MessageHandler {
    void handleMessage(Message msg);
}
