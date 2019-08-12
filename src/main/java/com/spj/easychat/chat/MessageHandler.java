package com.spj.easychat.chat;

import com.spj.easychat.common.CommonMessage;
import com.spj.easychat.common.Message;

public interface MessageHandler {
    void handleMessage(Message msg);
}
