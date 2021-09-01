package com.car.navigation.tcp.data;

/**
 * socket返回值基类
 * Created by fengwenhua on 2018/1/17.
 */

public class BaseResponse {

    private String cmd;

    private String cmdId;

    private String code;

    private Object result;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getCmdId() {
        return cmdId;
    }

    public void setCmdId(String cmdId) {
        this.cmdId = cmdId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
