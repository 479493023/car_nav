/**
 * <table><tr><td><b>Project Name</b></td><td>KT_CCS_1.0.0</td></tr>
 * <tr><td><b>File Name</b></td><td>SocketMessageEvent.java</td></tr>
 * <tr><td><b>Package Name</b></td><td>com.keyto.ccs.event</td></tr>
 * <tr><td><b>Date</b></td><td>2015年1月16日上午10:07:50</td></tr>
 * Copyright (c) 2015, <b>KEYTOP</b>  All Rights Reserved.
 *
*/

package com.car.navigation.event;
/**
 * <table><tr><td><b>ClassName</b></td><td>ClientC2SEvent</td></tr>
 * <tr><td><b>Function</b></td><td>socket数据传输事件.</td></tr>
 * <tr><td><b>Date</b></td><td>2015年1月16日 上午10:07:50 ;</td></tr>
 * @author   zhangyonglin@rd.keytop.com.cn
 * @version  1.0.0
 */
public class ClientC2SEvent {
	private byte[] socketMsg;
	
	public ClientC2SEvent(){
		this.socketMsg=null;
	}
	
	public byte[] getScoketMsg(){
		return this.socketMsg;
	}
	
	public void setSocketMsg(byte[] msg){
		this.socketMsg = msg;
	}
}

