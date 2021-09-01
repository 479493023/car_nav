/**
 * <table><tr><td><b>Project Name</b></td><td>KT_CCS_1.0.0</td></tr>
 * <tr><td><b>File Name</b></td><td>ClientS2CEvent.java</td></tr>
 * <tr><td><b>Package Name</b></td><td>com.keyto.ccs.event</td></tr>
 * <tr><td><b>Date</b></td><td>2015年1月16日上午10:51:43</td></tr>
 * Copyright (c) 2015, <b>KEYTOP</b>  All Rights Reserved.
 *
*/

package com.car.navigation.event;
/**
 * <table><tr><td><b>ClassName</b></td><td>ClientS2CEvent</td></tr>
 * <tr><td><b>Function</b></td><td>socket客户端方面:server>>>client数据事件.</td></tr>
 * <tr><td><b>Date</b></td><td>2015年1月16日 上午10:51:43 ;</td></tr>
 * @author   zhangyonglin@rd.keytop.com.cn
 * @version  1.0.0
 */
public class ClientS2CEvent {
private byte[] socketMsg;
	
	public ClientS2CEvent(){
		this.socketMsg=null;
	}
	
	public byte[] getSocketMsg(){
		return this.socketMsg;
	}
	
	public void setSocketMsg(byte[] msg){
		this.socketMsg = msg;
	}
}

