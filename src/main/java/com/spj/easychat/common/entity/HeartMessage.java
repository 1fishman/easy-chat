package com.spj.easychat.common.entity;

import java.io.Serializable;

public class HeartMessage{
    // 心跳请求包或者答复包,答复包则不回复. 1 是心跳请求,2是答复
    private int type;
    /**
     * 发送消息的时间戳
     */
    public HeartMessage(int type){
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
