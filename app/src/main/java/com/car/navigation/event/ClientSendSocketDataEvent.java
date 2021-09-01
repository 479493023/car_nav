/**
 * <table><tr><td><b>Project Name</b></td><td>KT_CCS_1.0.0</td></tr>
 * <tr><td><b>File Name</b></td><td>ClientSendSocketDataEvent.java</td></tr>
 * <tr><td><b>Package Name</b></td><td>com.keytop.ccs.event</td></tr>
 * <tr><td><b>Date</b></td><td>2015年1月20日下午5:22:07</td></tr>
 * Copyright (c) 2015, <b>KEYTOP</b>  All Rights Reserved.
 *
*/

package com.car.navigation.event;
/**
 * <table><tr><td><b>ClassName</b></td><td>ClientSendSocketDataEvent</td></tr>
 * <tr><td><b>Function</b></td><td>作为socket客户端发送数据到服务端事件(岗亭>云服务器).</td></tr>
 * <tr><td><b>Date</b></td><td>2015年1月20日 下午5:22:07 ;</td></tr>
 * @author   zhangyonglin@rd.keytop.com.cn
 * @version  1.0.0
 */
public class ClientSendSocketDataEvent {
	private byte dataType;
	private String content; 
	private byte[] fileByteArray;
	public ClientSendSocketDataEvent(){
		
	}

	public byte getDataType() {
		return dataType;
	}

	public String getContent() {
		return content;
	}
	
	public byte[] getFileByteArray(){
		return fileByteArray;
	}
	/**
	 * 
	 * setSendSocketData:一般此事件数据内容是别的线程往socket线程中发布需要发送socket数据的事件内容. <br>
	 * <ul>
	 * <li>在1.0.0版本,由zhangyl创建</li>
	 * </ul>
	 * @param dataType socket data type
	 * @param content socket中整个json字符串内容
	 */
	public void setSendSocketData(byte dataType,String content){
		this.dataType = dataType;
		this.content = content;
		
	}
	
	public void setSendSocketData(byte dataType,byte[] content){
		this.dataType = dataType;
		this.fileByteArray = content;
	}
	
	
}

