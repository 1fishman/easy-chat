package com.spj.easychat.common.entity;

public class CommonMessage{
    // 消息类型 0代表命令,1代表发送的消息
    private int type;
    // 属于什么命令
    private CommandEnum command ;
    // 消息内容
    private String msg;
    // 发送人
    private String fromUser;
    // 接受者
    private String toUser;

    private long time;

    public CommonMessage(String fromUser,String toUser, String msg) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.msg = msg;
        this.time = System.currentTimeMillis();
    }

    public CommonMessage(int type,CommandEnum command){
        this.type = type;
        this.command = command;
        this.time = System.currentTimeMillis();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public CommandEnum getCommand() {
        return command;
    }

    public void setCommand(CommandEnum command) {
        this.command = command;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "CommonMessage{" +
                "type=" + type +
                ", command=" + command +
                ", msg='" + msg + '\'' +
                ", fromUser='" + fromUser + '\'' +
                ", toUser='" + toUser + '\'' +
                '}';
    }
}
