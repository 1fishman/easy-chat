package com.spj.easychat.common;

import java.awt.*;
import java.io.Serializable;

public class Message {
    long time;
    Object msg;

    public Message(Object msg) {
        this.time = System.currentTimeMillis();
        this.msg = msg;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }
}
