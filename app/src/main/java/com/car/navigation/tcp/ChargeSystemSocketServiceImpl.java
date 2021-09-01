package com.car.navigation.tcp;


import android.util.Log;

/**
 * 收费项目socket interface的实现类
 * Created by fengwenhua on 2016/8/17.
 */
public class ChargeSystemSocketServiceImpl implements IChargeSystemSocketService {
    /** socket数据实体字符串参数分隔符 */
    public static final String socketStringDelimiter = ",";
    private static volatile ChargeSystemSocketServiceImpl instance;

    public IdataPackageHandler mClientSocketUtils = new DataPackageHandleImpl(); //工具类实例

    /** 私有化 */
    private ChargeSystemSocketServiceImpl() {
    }

    public static ChargeSystemSocketServiceImpl getInstance() {
        if (null == instance) {
            synchronized (ChargeSystemSocketServiceImpl.class) {
                if (null == instance) {
                    instance = new ChargeSystemSocketServiceImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public byte[] buildDataPacket(byte command, byte[] entityData) {
        byte[] dataArray;
        if (null != entityData) {
            dataArray = new byte[entityData.length + 15];
        } else {
            dataArray = new byte[15];
        }

        int index = 0;//浮标
        dataArray[index++] = (byte) 0xfb; //协议头

        byte[] dataPacket_timestamp = mClientSocketUtils.time2byteArray(
                System.currentTimeMillis());
        if (null != dataPacket_timestamp) {
            for (int i = 0; i < 4; i++) { //时间戳
                dataArray[index++] = dataPacket_timestamp[i];
            }
        }
        dataArray[index++] = command; //指令码
        dataArray[index++] = 0; //总包数
        dataArray[index++] = 1; //总包数
        dataArray[index++] = 0; //包序号
        dataArray[index++] = 0; //包序号
        if (null == entityData) { //entityData 为null,视为心跳指令
            dataArray[index++] = 0; //数据长度
            dataArray[index++] = 0;
//            dataArray[index++] =0; //数据实体内容
        } else {//其他有实体内容数据
            int dataSize = entityData.length;
            byte[] dataHexByteArray = mClientSocketUtils.hex2byte(Integer.toHexString(dataSize));
            int dataHexByteArraySize = dataHexByteArray.length;
            if (2 >= dataHexByteArraySize && 0 < dataHexByteArraySize) {
                if (dataHexByteArraySize == 1) {//数据长度
                    dataArray[index++] = 0;
                    dataArray[index++] = dataHexByteArray[0];
                } else if (dataHexByteArraySize == 2) {
                    dataArray[index++] = dataHexByteArray[0];
                    dataArray[index++] = dataHexByteArray[1];
                }
            } else {
                Log.e("buildDataPacket",">>>数据长度不合法!<<<");
//                Toasty.warning(TdaApplication.instance,"发送数据长度异常，请检查").show();//TODO 待优化，UI线程操作放置在这边不合适
                return null;
            }

            for (int i = 0; i < dataSize; i++) {
                dataArray[index++] = entityData[i];
            }
        }

        //校验码,从时间戳开始到数据内容最后一个字节在内的所有数据累加和
        int checkSum = 0;//生成校验码
        int checkSumSize = dataArray.length - 1;
        for (int i = 0; i < checkSumSize; i++) {
            checkSum += (dataArray[i + 1] & 0xff);//跳过包头
        }
        byte[] checkSumByteArray = mClientSocketUtils.hex2byte(Integer.toHexString(checkSum));
        if (checkSumByteArray != null && checkSumByteArray.length >= 1) {
            if (checkSumByteArray.length >= 2) {
                dataArray[index++] = checkSumByteArray[checkSumByteArray.length - 2];
                dataArray[index++] = checkSumByteArray[checkSumByteArray.length - 1];
            } else {//如果转换的校验码byte数组长度为1
                dataArray[index++] = 0;
                dataArray[index++] = checkSumByteArray[checkSumByteArray.length - 1];
            }
        } else {
            Log.e("buildDataPacket",">>>校验码转换错误!<<<");
        }
        //协议尾
        dataArray[index] = (byte) 0xfe;

        return mClientSocketUtils.sendDataPacketEscape(dataArray);//转义,并返回
    }

    @Override
    public int judgeOrderForData(byte[] DataArray, char order) {
        //根据指令,取得操作类别和子类别
        /**
         * 服务器与其它程序之间的可扩展指令（‘A’）
         */
        switch (order) {
            case 'A':
                if (DataArray[0] == 0 && DataArray[1] == 2) {//0x0002
                    if (DataArray[2] == 0 && DataArray[3] == 1) {//0x0001
                        return 201;
                    } else if (DataArray[2] == 0 && DataArray[3] == 2) {//0x0002
                        return 202;
                    } else if (DataArray[2] == 0 && DataArray[3] == 4) {//0x0004
                        return 204;
                    } else if (DataArray[2] == 0
                            && DataArray[3] == 6) {//服务器向自助收费程序推送出车信息（A-0002-0006）
                        return 206;
                    } else if (DataArray[2] == 0 && DataArray[3] == 7) {//日志上传指令
                        return 207;
                    }
                } else if (DataArray[0] == 0 && DataArray[1] == 3) {//0x0003
                    if (DataArray[2] == 0 && DataArray[3] == 1) {//0x0001入场预约
                        return 301;
                    } else if (DataArray[2] == 0 && DataArray[3] == 2) {//0x0002出场提前缴费
                        return 302;
                    } else if (DataArray[2] == 0 && DataArray[3] == 3) {//0x0003 锁车/解锁
                        return 303;
                    } else if (DataArray[2] == 0 && DataArray[3] == 4) {//0x0004 月租车续费
                        return 304;
                    }
                }

                break;
            case 'Z':
                if (DataArray[0] == 0 && DataArray[1] == 6) {//0x0006
                    if (DataArray[2] == 1
                            && DataArray[3] == 2) { //0x0102 取卡/票辅助-【(新)出口】通知交卡/票及处理（Z-0006-0102）
                        return 60102;
                    }

                }
                break;
        }
        return -1;
    }
}
