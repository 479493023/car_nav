package com.car.navigation.event;

/**
 * 服务端发往客户端的消息事件
 * Created by fengwenhua on 2016/8/18.
 */
public class Service2ClientEvent {
    private byte[] receiveMsg;

    public byte[] getReceiveMsg() {
        return receiveMsg;
    }

    public void setReceiveMsg(byte[] receiveMsg) {
        this.receiveMsg = receiveMsg;
    }
}
