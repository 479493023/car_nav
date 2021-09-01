package com.car.navigation.event;

/**
 * 客户端发送指令到服务端、附带cmdId
 * Created by fengwenhua on 2016/8/19.
 */
public class Client2ServiceWithIdEvent {
    private int code;//命令

    private byte[] content;//发送的内容

    public void setData(int cmdType,byte[] content){
        this.code = cmdType;
        this.content = content;
    }

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public byte[] getContent() {
        return content;
    }
    public void setContent(byte[] content) {
        this.content = content;
    }
}
