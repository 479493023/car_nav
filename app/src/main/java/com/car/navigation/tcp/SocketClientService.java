package com.car.navigation.tcp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;

import com.car.navigation.ComApplication;
import com.car.navigation.Constants;
import com.car.navigation.TdaParams;
import com.car.navigation.event.Client2ServiceEvent;
import com.car.navigation.event.Client2ServiceWithIdEvent;
import com.car.navigation.event.Service2ClientEvent;
import com.car.navigation.event.UIEvent;
import com.car.navigation.tcp.data.BaseResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;

/**
 * Created by caoJZ on 2018/1/19.
 */

public class SocketClientService extends Service {

    private ClientSocketConnect socketConnect;
    private ReceiveThread mReceiveThread;
    private Socket clientSocket;
    private boolean isCharging;//服务开关
    private volatile boolean socketIsRun;//禁止编译器优化此代码
    private volatile LinkedList<Byte> receiveDataBuffer = new LinkedList<Byte>();// 接收用的byte数组
    private byte[] responseByteArray;// 应答用的byte数组
    private volatile byte[] requestByteArray;// 请求用的byte数组
    /**
     * 上一次接收到信息的时间
     * 1.当接收到服务器信息时，需要同步最后一次接收的时间
     * 2.如果长时间没接收到服务器的信息,则认为服务器接收信息失败 10秒左右，关闭重连
     */
    private volatile long lastReceiveDataTime;

    /**
     * 用于记录socket断开连接是否第一次发生。如果不是，不再记录错误信息，取而代之的是使用....
     */
    private boolean isFirstError = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("", "socket client service onCreate...");
        EventBus.getDefault().register(this);
        startSocketThread();
    }

    private void startSocketThread() {
        isCharging = true;//开始服务
        socketConnect = new ClientSocketConnect();
        socketConnect.setName("ClientSocketConnect");
        socketConnect.start();
    }

    class ClientSocketConnect extends Thread {

        @Override
        public void run() {
            super.run();
            while (isCharging) {
                if (clientSocket == null) {
                    Log.i("connecttoserviceip>>>", Constants.currentSocketIp + " PORT>>>" + Constants.TCPPORT);
                    try {
                        clientSocket = new Socket(Constants.currentSocketIp, Constants.TCPPORT);
                        clientSocket.setSoTimeout(TdaParams.SOCKET_HEARTBEAT_CYCLE);
                        socketIsRun = true;//socket成功执行了
                        lastReceiveDataTime = System.currentTimeMillis();//更新
                        //发送注册包给服务器
                        sendSocketData2Server(TdaParams.BaseCommandType.regist_tag, TdaParams.SocketResponse.registData);
                        SystemClock.sleep(500);
                        ReceiveServerSocketData();
                        //如果注册没抛出异常
                        Log.i("clientSocket", "与视频服务器连接正常!");
                        EventBus.getDefault().post(UIEvent.MSG_NETWORK_NORMAL);
                        //-------开启新的线程,持续接收socket消息----------
                        mReceiveThread = new ReceiveThread();
                        mReceiveThread.setName("mReceiveThread");
                        mReceiveThread.start();
                        ComApplication.isConnectionTcp = true;
                        //-------持续发送心跳包-------------
                        while (socketIsRun) {
                            try {
                                sendSocketData2Server(TdaParams.BaseCommandType.heartBeat_tag, TdaParams.SocketResponse.registData);
                                Thread.sleep(TdaParams.heartBeatSpacing_5);
                                EventBus.getDefault().post(UIEvent.MSG_NETWORK_NORMAL);
                            } catch (Exception e) {
                                Log.e("发送心跳包异常，导致socket断开", ",错误信息:(" + e.getMessage() + ")");
                                socketIsRun = false;//socket断开
                                ComApplication.isConnectionTcp = false;
                                EventBus.getDefault().post(UIEvent.MSG_NETWORK_FAIL);
                            }
                        }
                        //------如果执行到这一步,说明连接异常了--
                    } catch (IOException e) {
                        ComApplication.isConnectionTcp = false;
                        if (isFirstError) {
                            Log.e("socket连接异常", "(" + Constants.currentSocketIp + ":" + Constants.TCPPORT + ") error>>>" + e.toString());
                        } else {
                            Log.e("......", "......");
                        }
                    } finally {//跑到这里说明连接出现问题了,必须关闭才能正常重新连接
                        if (null != clientSocket) {
                            try {
                                clientSocket.close();
                            } catch (IOException e) {
                                //
                            }
                        }
                        ComApplication.isConnectionTcp = false;
                        clientSocket = null;
                        receiveDataBuffer.clear();
                        responseByteArray = null;
//                        HeartBeatRecordUtil.socketHeartBeatFail();//日志记录
                        Log.e("网络连接失败", "等待发起重连请求...");
                        EventBus.getDefault().post(UIEvent.MSG_NETWORK_FAIL);
                    }
                }
//                SystemClock.sleep(TdaParams.SOCKET_HEARTBEAT_CYCLE);
            }
        }
    }


    /**
     * 外界通过该方法通知网络情况
     */
    private void setSocketIsRun(boolean isSocketIsRun) {
        socketIsRun = isSocketIsRun;
    }

    public class ReceiveThread extends Thread {
        @Override
        public void run() {
            while (socketIsRun) {
                try {
                    ReceiveServerSocketData();
                } catch (IOException e) {
                    socketIsRun = false;
                    Log.e("接收socket信息流失败>>>", e.toString());
                }
            }
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void sendSocketDataWithId(Client2ServiceWithIdEvent event) {
        switch (event.getCode()) {
            case TdaParams.BaseCommandType.outCar_tag:// 出车
            case TdaParams.BaseCommandType.v_tag:// 发送版本号
            case TdaParams.BaseCommandType.z_tag://取卡/票辅助-【(新)出口】通知交卡/票及处理（Z-0006-0102）
            case TdaParams.BaseCommandType.n_tag://相机绑定
                try {
                    sendSocketData2Server((byte) event.getCode(), event.getContent());
                } catch (IOException e) {
                    Log.e("[C2S]发送" + (byte) event.getCode() + "应答指令失败>>>", e.toString());
                }
                break;
            case TdaParams.BaseCommandType.custom_stop_socket_thread_tag://off
                stopSocketThread();
                break;
            case TdaParams.BaseCommandType.custom_start_socket_thread_tag://on
                startSocketThread();
                break;
            default:
                break;
        }
    }

    /**
     * 发送socket数据
     */
    public void sendSocketData2Server(byte command, byte[] dataArray) throws IOException {
        // 如果socket在运行且物理网络连接正常
        if (socketIsRun) {
            OutputStream outputStream = clientSocket.getOutputStream();
            requestByteArray = ChargeSystemSocketServiceImpl.getInstance().buildDataPacket(command, dataArray);
            if (null != requestByteArray) {
                outputStream.write(requestByteArray);
                outputStream.flush();
                Client2ServiceEvent client2ServiceEvent = new Client2ServiceEvent();
                client2ServiceEvent.setSendMsg(requestByteArray);
                EventBus.getDefault().post(client2ServiceEvent);
            }
        } else {
            Log.i("", "Socket连接断开 socketIsRun=false");
            throw new IOException("Socket连接断开,无法发送数据");
        }
    }


    /**
     * 接收socket数据
     *
     * @throws IOException
     * @throws Exception
     */
    public void ReceiveServerSocketData() throws IOException {
        InputStream dis = null;
        dis = clientSocket.getInputStream();
        //解析数据流之前判断socket有多长时间没接收到信息了
        long timeDifference = System.currentTimeMillis() - lastReceiveDataTime;
        /*三个心跳包间隔都没接收到*/
//        if (timeDifference >= (TdaParams.heartBeatSpacing_5 * 3) && timeDifference < TdaParams.DAY_2_MILLISECOND) {
//            socketIsRun = false;
//            Log.e("三个心跳间隔未接收到消息", "socket异常.");
//        }
        if (0 != dis.available()) {
            int i = -1;
            byte[] b = new byte[1024];// 数据最大不可超过1024个字节
            i = dis.read(b);
            for (int j = 0; j < i; j++) {
                receiveDataBuffer.add(b[j]);
            }
            byte[] buffer;
            while ((buffer = ChargeSystemSocketServiceImpl.getInstance().mClientSocketUtils.unpackingReceiveData(receiveDataBuffer)) != null && !buffer.equals("")) {// 从缓冲区取包并判断是否有数据
                lastReceiveDataTime = System.currentTimeMillis();//获取当前服务器的时间
                responseByteArray = buffer;
                //分发事件
                Service2ClientEvent service2ClientEvent = new Service2ClientEvent();
                service2ClientEvent.setReceiveMsg(responseByteArray);
                EventBus.getDefault().post(service2ClientEvent);
            }
        }
    }

    /**
     * 关闭socket的连接
     */
    private void stopSocketThread() {
        socketIsRun = false;
        isCharging = false;
        if (clientSocket != null) {
            try {
                clientSocket.close();
            } catch (IOException e) {
                Log.e("", "clientSocket 关闭异常>>>" + e.toString());
            }
        }
        clientSocket = null;
        if (socketConnect != null) {
            socketConnect.interrupt();
            socketConnect = null;
        }
        if (mReceiveThread != null) {
            mReceiveThread.interrupt();
            mReceiveThread = null;
        }
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        stopSocketThread();
    }
}
