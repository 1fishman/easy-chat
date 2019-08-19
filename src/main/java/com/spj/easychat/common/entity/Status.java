package com.spj.easychat.common.entity;

public enum    Status {
    AUTHENTICATIONERROT("用户名或密码不对",101),    // 身份验证错误 101
    LOGINSUCCESS("成功",200), // 请求成功
    NOT_LOGIN_ERROR("请先登录",102), // 中间可能掉线了,被服务器暂定为下线了,要重新登录
    USERNAMEEXIST("用户名已存在",103),
    REGISTERSUCCESS("注册成功",200);



    Status(String msg,int statusNo){
        this.statusNo = statusNo;
        this.msg = msg;
    }
    private String msg;
    private int statusNo;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatusNo() {
        return statusNo;
    }

    public void setStatusNo(int statusNo) {
        this.statusNo = statusNo;
    }
}
