package com.car.navigation.tcp;


import android.util.Log;

import com.car.navigation.ComApplication;
import com.car.navigation.TdaParams;
import com.car.navigation.event.Client2ServiceEvent;
import com.car.navigation.event.Client2ServiceWithIdEvent;
import com.car.navigation.event.Service2ClientEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 长连接服务处理
 * Created by fengwenhua on 2018/1/17.
 */

public class ChargeSocketHandler {

    private static volatile ChargeSocketHandler mChargeSocketHolder;

    private ChargeSocketHandler() {
        EventBus.getDefault().register(this);
    }

    public static ChargeSocketHandler getInstance() {
        if (null == mChargeSocketHolder) {
            synchronized (ChargeSocketHandler.class) {
                if (null == mChargeSocketHolder) {
                    mChargeSocketHolder = new ChargeSocketHandler();
                }
            }
        }
        return mChargeSocketHolder;
    }

    /**
     * 发送给服务器的业务逻辑处理
     *
     * @param mClient2ServiceEvent 待发送事件
     */
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sendSocketData(Client2ServiceEvent mClient2ServiceEvent) {
        String logStr = ChargeSystemSocketServiceImpl.getInstance().mClientSocketUtils.byteToHexStr(mClient2ServiceEvent.getSendMsg(), true);//byte转16进制,日志用
        byte[] request = ChargeSystemSocketServiceImpl.getInstance().mClientSocketUtils.receiveDataPacketEscape(mClient2ServiceEvent.getSendMsg());//转义请求码
        if (null != request) {
            switch (request[5]) {
                case TdaParams.BaseCommandType.regist_tag:// 注册
                    Log.i(">>>注册指令'", (char) TdaParams.BaseCommandType.regist_tag + "'发送:" + logStr);
                    break;
                case TdaParams.BaseCommandType.heartBeat_tag:// 心跳
                    Log.i(">>>>>>>>指令'", (char) TdaParams.BaseCommandType.heartBeat_tag + "'" + "发送:" + logStr);
                    break;
                case TdaParams.BaseCommandType.outCar_tag:// 出入车
                    Log.i(">>>>>>>>指令'", (char) TdaParams.BaseCommandType.outCar_tag + "'发送:" + logStr);
                    break;
                case TdaParams.BaseCommandType.v_tag:// 版本
                    Log.i(">>>>>>>>版本指令'", (char) TdaParams.BaseCommandType.v_tag + "'发送:" + logStr);
                    break;
                case TdaParams.BaseCommandType.n_tag:
                    Log.i(">>>>>>>>指令'", (char) TdaParams.BaseCommandType.n_tag + "'发送:" + logStr);
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * @param event 事件内容
     */
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveSocketData(Service2ClientEvent event) {
        String logStr = ChargeSystemSocketServiceImpl.getInstance().mClientSocketUtils.byteToHexStr(event.getReceiveMsg(), true);//byte转16进制,日志用
        byte[] responseByteArray = ChargeSystemSocketServiceImpl.getInstance().mClientSocketUtils.receiveDataPacketEscape(event.getReceiveMsg());//转义请求码
        int flag = ChargeSystemSocketServiceImpl.getInstance().mClientSocketUtils.checkReceiveDataIsComplete(responseByteArray);//完整性检查
        if (flag == TdaParams.data_state_success) {// 完整性检查通过,数据可以使用
            switch (responseByteArray[5]) {
//                case TdaParams.BaseCommandType.v_tag:// V 版本号指令
//                    Log.i(">>>>>>>>指令'" + (char) TdaParams.BaseCommandType.v_tag + "'接收:"+ logStr);// 接收客户端线程发来的Message对象，显示用
//                    String versionName = AppUtils.getVersionName(ComApplication.instance);
//                    Client2ServiceWithIdEvent versionResponseEvent = new Client2ServiceWithIdEvent();
//                    versionResponseEvent.setData(TdaParams.BaseCommandType.v_tag,ChargeSystemSocketServiceImpl.getInstance().mClientSocketUtils.changVersionTobyte(versionName));
//                    EventBus.getDefault().post(versionResponseEvent);
//                    break;
                case TdaParams.BaseCommandType.regist_tag:// 注册
                    Log.i(">>>指令'", (char) TdaParams.BaseCommandType.regist_tag + "'接收:" + logStr);
                    break;
                case TdaParams.BaseCommandType.heartBeat_tag:// 心跳
                    // TEST
                    Log.i(">>>指令'", (char) TdaParams.BaseCommandType.heartBeat_tag + "'" + "' 心跳中...");
//                    HeartBeatRecordUtil.socketHeartBeatNormal();
                    break;
                case TdaParams.BaseCommandType.outCar_tag:// 出车
                    Log.i(">>>指令'", (char) TdaParams.BaseCommandType.outCar_tag + "接收:" + logStr);
                    break;
//                case TdaParams.BaseCommandType.n_tag://
//                    Log.i(">>>指令'", (char) TdaParams.BaseCommandType.n_tag + "接收:" + logStr);
//                    //cmdId
//                    ChargeSystemSocketServiceImpl.getInstance().mClientSocketUtils.handleNTagResponseByteArray(responseByteArray);
//                    break;
            }
        }
    }
}
