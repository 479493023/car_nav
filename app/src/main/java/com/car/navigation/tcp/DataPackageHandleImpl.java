package com.car.navigation.tcp;

import android.text.TextUtils;
import android.util.Log;


import com.car.navigation.TdaParams;
import com.car.navigation.event.UIEvent;
import com.car.navigation.tcp.data.BaseResponse;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 收费项目socket客户端工具类
 * 原author zhangyonglin
 * Created by fengwenhua on 2016/8/17.
 */
public class DataPackageHandleImpl implements IdataPackageHandler {
    public static byte dataPacket_head = (byte) 0xfb;// 头
    public static byte dataPacket_tail = (byte) 0xfe;// 尾


    @Override
    public byte[] sendDataPacketEscape(byte[] dataArray) {
        byte[] afterEscapeDataArray = null;
        if (null != dataArray) {
            ArrayList<Byte> list = new ArrayList<Byte>();
//			System.out.println("发送端,转义前的数据为:"+byteToHexStr(dataArray,true));
            int size = dataArray.length;
//			System.out.println("发送端,转义前的数据长度为:"+dataArray.length);
            for (int i = 1; i < size - 1; i++) {// 不计算包头包尾
                switch (dataArray[i]) {
                    case (byte) 0xfb:
                        list.add((byte) 0xff);
                        list.add((byte) 0xbb);
                        break;
                    case (byte) 0xfe:
                        list.add((byte) 0xff);
                        list.add((byte) 0xee);
                        break;
                    case (byte) 0xff:
                        list.add((byte) 0xff);
                        list.add((byte) 0xfc);
                        break;
                    default:
                        list.add(dataArray[i]);
                        break;
                }
            }
            list.add(0, dataArray[0]);//插入包头
            list.add(dataArray[dataArray.length - 1]);//插入包尾

            int listSize = list.size();
            afterEscapeDataArray = new byte[listSize];
            for (int i = 0; i < listSize; i++) {
                afterEscapeDataArray[i] = list.get(i);
            }
//			System.out.println("发送端,转义后的数据长度为:"+afterEscapeDataArray.length);

        }
        return afterEscapeDataArray;
    }

    @Override
    public int checkReceiveDataIsComplete(byte[] receiveData) {
        try {
            int size = receiveData.length;//转义后未解包的长度
            //检查包头尾是否合法
            if (receiveData[0] != (byte) 0xfb || receiveData[size - 1] != (byte) 0xfe) {
                Log.e("checkReceiveComplete", "包头尾不否合法");
                return 2;
            }
            //检查总包数与包序号是否合法
            int sum = (receiveData[6] & 0xff) * 256 + (receiveData[7] & 0xff);//得到总包数
            int number = (receiveData[8] & 0xff) * 256 + (receiveData[9] & 0xff);//得到总包数
            if (number >= sum) {
                Log.e("checkReceiveComplete", "包序号大于总包数");
                return 3;
            }
//            if(sum >= 1000 || number>=1000) {
//                Log.e("总包数或包序号太大");
//                return 3;
//            }
            //检查数据长度是否合法
            int dataSize = (receiveData[10] & 0xff) * 256 + (receiveData[11] & 0xff);
            if (dataSize != (size - 15)) {//15为一个封包协议里除数据实体内容的其他数据的数据长度总和,或数据大于1024个字节 ||dataSize>1024
                Log.e("checkReceiveComplete", "数据长度不正确");
                return 4;
            }
            //检查校验码
            int checkCode = (receiveData[size - 3] & 0xff) * 256 + (receiveData[size - 2] & 0xff);
            int checksum = 0;
            for (int i = 1; i < size - 3; i++) {//不包括包头包尾,元数据的校验码
                checksum += receiveData[i] & 0xff;
            }
            if (checkCode != (checksum & 0xFFFF)) {
                Log.e("校验码不符合,数据包中的校验码:", checkCode + " 计算后的校验码:" + checksum);
                return 5;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e("应答数据完整性检查不通过", e.toString());
            return 1;

        }
        return 0;
    }

    @Override
    public byte[] receiveDataPacketEscape(byte[] receiveDataPacketArray) {
        byte[] receiveData = null;
        if (null != receiveDataPacketArray) {
//			System.out.println("应答  转义前数据为:"+byteToHexStr(receiveDataPacketArray, true));
            ArrayList<Byte> list = new ArrayList<Byte>();
            int receiveDataSize = receiveDataPacketArray.length;
//			System.out.println("应答数据包总长度为:"+receiveDataSize);
            for (int i = 1; i < receiveDataSize - 1; i++) {//取掉包头包尾
                if (receiveDataPacketArray[i] == (byte) 0xff) {
                    if (receiveDataPacketArray[i + 1] == (byte) 0xbb) {
                        list.add((byte) 0xfb);
                        i++;
                    } else if (receiveDataPacketArray[i + 1] == (byte) 0xee) {
                        list.add((byte) 0xfe);
                        i++;
                    } else if (receiveDataPacketArray[i + 1] == (byte) 0xfc) {
                        list.add((byte) 0xff);
                        i++;
                    } else {
                        list.add(receiveDataPacketArray[i]);
                    }
                } else {
                    list.add(receiveDataPacketArray[i]);
                }
            }
            list.add(0, (byte) 0xfb);
            list.add((byte) 0xfe);
            int listSize = list.size();
            receiveData = new byte[listSize];
            if (null != receiveData) {
                for (int i = 0; i < listSize; i++) {
                    receiveData[i] = list.get(i);
                }
//				System.out.println("应答数据包转义后总长度为:"+receiveData.length);
            }
        }

//		System.out.println("应答  转义后数据为:"+byteToHexStr(receiveData, true));
        return receiveData;
    }

    @Override
    public byte[] time2byteArray(long timestamp) {
        if (0 < timestamp) {
            timestamp = timestamp / 1000;//取掉毫秒数
        }
//		System.out.println("时间戳-->>"+time_tag);//测试用
        byte[] byteArray = hex2byte(Long.toHexString(timestamp));
        if (null != byteArray) {
            if (4 == byteArray.length) {
                return byteArray;
            } else {
                Log.e("转换时间戳byte[]", "length不合法!");
                return null;
            }
        } else {
            Log.e("转换时间戳byte[]", "byte[]为空!");
            return null;
        }
    }

    @Override
    public byte[] hex2byte(String hex) {
        if (hex.length() % 2 != 0) {
            hex = 0 + hex;
            // throw new IllegalArgumentException();
        }
        char[] arr = hex.toCharArray();
        byte[] b = new byte[hex.length() / 2];
        for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
            String swap = "" + arr[i++] + arr[i];
            int byteint = Integer.parseInt(swap, 16) & 0xFF;
            b[j] = new Integer(byteint).byteValue();
        }
        return b;
    }

    @Override
    public byte[] changVersionTobyte(String version) {
        ArrayList<Byte> list = new ArrayList<Byte>();
        byte[] androidVersion = null;
        byte fb = 0x00;
        String verNum = version;
        byte[] vb = verNum.getBytes();
        list.add((byte) fb);
        list.add((byte) fb);
        for (int i = 0; i < (verNum.getBytes()).length; i++) {
            list.add(vb[i]);
        }
        int l = list.size();
        if (l != 20) {
            for (int j = 0; j < 20 - l; j++) {
                list.add((byte) fb);
            }
        }
        if (list.size() == 20) {
            androidVersion = new byte[list.size()];
            for (int x = 0; x < list.size(); x++) {
                androidVersion[x] = list.get(x);
            }
        }
        return androidVersion;
    }

    @Override
    public byte[] unpackingReceiveData(LinkedList<Byte> receiveDataBuffer) {
        byte[] data = null;
        try {
            if (!receiveDataBuffer.isEmpty()) {
                int begin = -1;
                int finish = -1;
                begin = receiveDataBuffer.indexOf(dataPacket_head);// 找到头的索引
                finish = receiveDataBuffer.indexOf(dataPacket_tail);// 找到尾的索引
                if (begin != -1 && finish != -1 && begin < finish) {// 缓冲区有整包数据
                    data = new byte[finish - begin + 1];
                    for (int i = begin, j = 0; i <= finish; i++, j++) {
                        data[j] = receiveDataBuffer.get(i);
                    }
                } else if (begin != -1 && finish != -1 && begin > finish) {//如果存在丢失包头 错误的整包数据
                    finish = begin - 1;
                    begin = 0;//删除end之前的数据
                } else {
                    begin = 0;
                    finish = -1;//这样才能保证不会误删除第一个byte
                }
                for (int i = begin; i <= finish; i++) {
                    if (begin < receiveDataBuffer.size()) {
                        receiveDataBuffer.remove(begin);// 移除拆好的数据
                    }
                }
            }

        } catch (Exception e) {
            Log.e("拆解数据包异常", e.toString());
            data = null;
        }
        return data;
    }

    @Override
    public String byteToHexStr(byte[] bArray, boolean format) {
        if (null == bArray) {
            return null;
        }
        StringBuffer strb = new StringBuffer(bArray.length);
        String str;
        for (int i = 0; i < bArray.length; i++) {
            str = Integer.toHexString(0xFF & bArray[i]).trim();
            if (str.length() < 2) {
                str = "0" + str;
            }
            if (format) {
                str += " ";
            }
            strb.append(str);
        }
        str = strb.toString().trim();
        return str;
    }

    @Override
    public byte[] realSocketDate(byte[] responseByteArray) {
        byte[] realResponseByteArray = null;
        if (responseByteArray == null || responseByteArray.length < 15) {
            return realResponseByteArray;
        }
        String logStr = ChargeSystemSocketServiceImpl.getInstance().mClientSocketUtils.byteToHexStr(responseByteArray, true);
        int size = responseByteArray.length - 15;// 转义数据包后长度减去15就是数据实体内容的长度
        realResponseByteArray = new byte[size];// 为数据实体内容
        try {
            System.arraycopy(responseByteArray, 12, realResponseByteArray, 0, size);
            /**
             * for (int i = 0; i < size; i++) {
             *  realResponseByteArray[i] = responseByteArray[i + 12];
             *}
             */
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e("出错啦!", ">>>数组下标越界ArrayIndexOutOfBoundsException>>>" + logStr + e.toString());
        }
        return realResponseByteArray;
    }


    @Override
    public byte[] realSocketDataWithoutCategory(byte[] responseByteArray) {
        if (responseByteArray == null || responseByteArray.length < 4) {
            return null;
        }
        int size = responseByteArray.length;
        byte[] responseByteArrayWithoutCategory = new byte[size - 4];
        System.arraycopy(responseByteArray, 4, responseByteArrayWithoutCategory, 0, size - 4);
        /**
         * for (int i = 0; i < size - 4; i++) {
         *  responseByteArrayWithoutCategory[i] = responseByteArray[i + 4];
         *}
         */
        return responseByteArrayWithoutCategory;
    }


    /**
     * N指令日志处理
     *
     * @param response 已经转义过的socket内容
     */
//    @Override
//    public void handleNTagResponseByteArray(byte[] response) {
//        String responseStr = TDAUtils.decoding(decodeResponseArrayToStr(response));
//        Log.w("received server data >>>"+responseStr);
//        if(TextUtils.isEmpty(responseStr)){
//            UIEvent event = UIEvent.MSG_TOAST;
//            event.setDescription("服务器返回空数据");
//            EventBus.getDefault().post(event);
//            return;
//        }
//        responseStr = responseStr.replaceAll(JsonErrorCorrection.JSON_MUTI_LIST_FLITER, JsonErrorCorrection.JSON_MUTI_LIST_REPLACE_STR);//json 不符合规范,老陈说他那边更改困难,需要前端自行过滤
//        BaseResponse baseResponse = TdaConstant.gson.fromJson(responseStr, BaseResponse.class);
//        String cmdName = getlegalCmdName(baseResponse);
//        Log.i("handleNTagResponseByteArray  cmdName>>>" + responseStr);
//        switch (cmdName) {
//            case DatabaseOperation.OperationType.QUERY_LOT:
//                try {
//                    Server2ClientCustomLotEntity server2ClientCustomLotEntity = new Server2ClientCustomLotEntity();
//                    server2ClientCustomLotEntity.setMsg(new JSONObject(responseStr).optString("result", ""));
//                    server2ClientCustomLotEntity.setOperationType(DatabaseOperation.OperationType.QUERY_LOT);
//                    EventBus.getDefault().post(server2ClientCustomLotEntity);
//                } catch (JSONException e) {
//                    Log.e("new JSONObject error(QUERY_LOT)>>>",e);
//                }
//                break;
//            case DatabaseOperation.OperationType.UPDATE_LOT:
//                Server2ClientCustomLotEntity server2ClientCustomLotEntity = new Server2ClientCustomLotEntity();
//                server2ClientCustomLotEntity.setMsg(responseStr);
//                server2ClientCustomLotEntity.setOperationType(DatabaseOperation.OperationType.UPDATE_LOT);
//                EventBus.getDefault().post(server2ClientCustomLotEntity);
//                break;
//            case DatabaseOperation.OperationType.QUERY_CONFIG:
//                try {
//                    String result = new JSONObject(responseStr).optString("result", "");
//                    if (!TextUtils.isEmpty(result)) {
//                        JSONArray array = new JSONArray(result).optJSONArray(0);
//                        if(array!=null && array.length()>0){
//                            List<T_config> configList = new ArrayList<>();
//                            for(int index=0; index<array.length();index++){
//                                configList.add(TdaConstant.gson.fromJson(array.get(index).toString(), T_config.class));
//                            }
//                            Service2ClientWxEvent service2ClientWxEvent = new Service2ClientWxEvent();
//                            service2ClientWxEvent.setConfigList(configList);
//                            EventBus.getDefault().post(service2ClientWxEvent);
//                        }
//                    }
//                } catch (JSONException e) {
//                    Log.e("new JSONObject error(QUERY_CONFIG)>>>",e);
//                }
//                break;
//            case DatabaseOperation.OperationType.UPDATE_CONFIG:
//                Server2ClientCustomLotEntity server2ClientWxEntity = new Server2ClientCustomLotEntity();
//                server2ClientWxEntity.setMsg(responseStr);
//                server2ClientWxEntity.setOperationType(DatabaseOperation.OperationType.UPDATE_CONFIG);
//                EventBus.getDefault().post(server2ClientWxEntity);
//                break;
//        }
//
//    }

    /**
     * 获取有效的cmd name，如果无则返回null
     * @param baseResponse
     * @return
     */
//    private String getlegalCmdName(BaseResponse baseResponse){
//        Iterator<DatabaseOperation> databaseOperationIterator = TdaConstant.CMD_QUEUE.getQueue().iterator();
//        while(databaseOperationIterator.hasNext()){
//            DatabaseOperation databaseOperation = databaseOperationIterator.next();
//            if (databaseOperation.getUuid().equalsIgnoreCase(baseResponse.getCmdId())) {
//                return databaseOperation.getCmdName();
//            }
//        }
//        return null;
//    }

    /**
     * 解析转义过的byte数组为 字符串内容
     *
     * @param response 已经转义过的socket内容
     * @return 解析过的字符串
     */
    public String decodeResponseArrayToStr(byte[] response) {
        String responseStr = null;
        byte[] responseByteArrayWithoutCategory = realSocketDate(response);
//        byte[] responseByteArrayWithoutCategory = realSocketDataWithoutCategory(responseByteArray);
        try {
            responseStr = new String(responseByteArrayWithoutCategory, TdaParams.SocketParams.SOCKET_DATA_PARSE_FORMAT).trim();
        } catch (UnsupportedEncodingException e) {
            Log.e("Socket字节流转字符串出错", " UnsupportedEncodingException>>>", e);
        }
        return responseStr;
    }


}
