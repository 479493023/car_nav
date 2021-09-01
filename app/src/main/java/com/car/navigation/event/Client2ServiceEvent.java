package com.car.navigation.event;

/**
 * 客户端发往服务端的事件
 * Created by fengwenhua on 2016/8/18.
 */
public class Client2ServiceEvent {
    private byte[] sendMsg;

    public byte[] getSendMsg() {
        return sendMsg;
    }

    public void setSendMsg(byte[] sendMsg) {
        this.sendMsg = sendMsg;
    }
}
