package com.car.navigation.event;


/**
 * UI事件信息(Toast的同时，请顺带记录日志)
 * Created by fengwenhua on 2016/8/19.
 */
public enum UIEvent {
    /**网络正常*/
    MSG_NETWORK_NORMAL,
    /**网络正在连接中...*/
    MSG_NETWORK_RECONNECT,
    /**网络异常*/
    MSG_NETWORK_FAIL,
    /**自动更新补丁*/
    AUTO_UPDATE_PATCH,
    /**下载APK错误*/
    MSG_DOWNLOAD_APK_ERROR,
    /**TOAST*/
    MSG_TOAST;

    /*指令描述*/
    private String description;
    /**add by 冯文华 reason:新增了抵用券使用信息，对UIEvent类进行了扩展*/
    private Object extraMessage;
    /**add by 冯文华 reason: 携带最后一笔缴费类型, 由于在当普通券与现金(部分缴费)使用顺序上的不同,会导致两种不同的找零结果,
     * 需要将缴费顺序上传到服务器,由服务器判断是否要对当前账单进行找零操作*/
    private int extraCode;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getExtraMessage() {
        return extraMessage;
    }

    public void setExtraMessage(Object extraMessage) {
        this.extraMessage = extraMessage;
    }

    public int getExtraCode() {
        return extraCode;
    }

    public void setExtraCode(int extraCode) {
        this.extraCode = extraCode;
    }
}
