package com.car.navigation.tcp;

import java.util.LinkedList;
import java.util.List;

/**
 * 根据协议的封装和拆箱等工具方法集
 */
public interface IdataPackageHandler {
    /**
     * 转义待发送数据
     * @param dataArray 待发送数据
     * 转义规则:
     *        0xfb 转化 0xff 0xbb
     *        0xff 转化 0xff 0xfc
     *  	  0xfe 转化 0xff 0xee
     * @return the array of byte
     */
    byte[] sendDataPacketEscape(byte[] dataArray);

    /**
     * /**
     * 确认应答的数据包是否完整,receiveData为转义后的封包数据
     * 返回值：
     *		0：成功
     *		1：失败
     *		2：包头尾不符合
     *		3：总包数与包序号不符合
     *		4：数据长度与内容不符合
     *		5：校验码不符合
     *		6：数据内容错误
     * @param receiveData 应答数据
     * @return 应答结果
     */
    int checkReceiveDataIsComplete(byte[] receiveData);

    /**
     * 转义服务器应答数据包
     * 转义规则:
     *        0xff 0xbb-->0xfb
     *        0xff 0xfc-->0xff
     *  	  0xff 0xee-->0xfe
     * @param receiveDataPacketArray 服务器应答数据
     * @return the array of byte
     */
    byte[] receiveDataPacketEscape(byte[] receiveDataPacketArray);

    /**
     * 时间戳转换成项目所用的格式 byte[4](十三位毫秒数去毫秒再转换成四位十六进制)
     * @param timestamp 13位时间戳
     * @return the array of byte
     */
    byte[] time2byteArray(long timestamp);

    /**
     * 十六进制串转化为byte数组
     * @param hex 十六进制转byte数组
     * @return the array of byte
     */
    byte[] hex2byte(String hex);

    /**
     * 封装Version数据成byte数组
     * @param version
     * @return
     */
    byte[]  changVersionTobyte(String version);

    /**
     * 从应答数据缓冲区中拆得包数据
     * @param receiveDataBuffer
     * @return
     */
    byte[] unpackingReceiveData(LinkedList<Byte> receiveDataBuffer);

    /**
     * 将byte数组转化成16进制的字符串内容
     * @param bArray 待转化数组
     * @param format 是否格式化加入空格
     * @return
     */
    String byteToHexStr(byte[] bArray, boolean format);
    /**
     * @param responseByteArray 转义后的数据
     * 转义数据包后长度减去15就是数据实体内容的长度
     * 返回的内容
     * 名称	    字节数
     * 操作类别	2byte
     * 子类别	2byte
     * 来车信息	Nbyte
     * */
    byte[] realSocketDate(byte[] responseByteArray);

    /**
     * 剥离数据内容中的主类别及子类别后的信息
     * @param responseByteArray 转义后&去除了协议头、校验码等后的 数据内容
     * @return 返回 剥离数据内容中的主类别及子类别后的信息
     */
    byte[] realSocketDataWithoutCategory(byte[] responseByteArray);

}
