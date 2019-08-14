package com.spj.easychat.common.entity;

public enum CommandEnum {
    LOGIN("login"),
    REGISTER("register"),
    LOGOUT("logout"),
    JOINTO("joinTo"),
    EXITTO("exitTO"),
    SENDTO("sendTO"),
    LIST("list"),
    CD("cd");

    CommandEnum(String command){
        this.command = command;
    }

    String command;

    @Override
    public String toString() {
        return "CommandEnum{" +
                "command='" + command + '\'' +
                '}';
    }
}
