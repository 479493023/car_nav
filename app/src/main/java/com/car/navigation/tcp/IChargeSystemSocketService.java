package com.car.navigation.tcp;


/**
 * 收费系统Socket通讯服务接口
 * 原author zhangyonglin
 * Created by fengwenhua on 2016/8/17.
 */
public interface IChargeSystemSocketService {

    //构建数据封装包
    byte[] buildDataPacket(byte command, byte[] entityData);

    //根据有效的(转义过并完整性检查通过)指令数据内容和指令码,判断数据流向
    int judgeOrderForData(byte[] DataArray, char order);

}
