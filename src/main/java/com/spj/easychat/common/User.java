package com.spj.easychat.common;

public class User {
    private String name;
    private String pass;
    private long uid;

    public User(String name, String pass, long uid) {
        this.name = name;
        this.pass = pass;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }
}
