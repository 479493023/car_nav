/**
 * <table><tr><td><b>Project Name</b></td><td>KT_CCS_1.0.0</td></tr>
 * <tr><td><b>File Name</b></td><td>ClientSocketMsgEvent.java</td></tr>
 * <tr><td><b>Package Name</b></td><td>com.keytop.ccs.event</td></tr>
 * <tr><td><b>Date</b></td><td>2015年1月16日下午4:03:31</td></tr>
 * Copyright (c) 2015, <b>KEYTOP</b>  All Rights Reserved.
 *
*/

package com.car.navigation.event;
/**
 * <table><tr><td><b>ClassName</b></td><td>ClientSocketMsgEvent</td></tr>
 * <tr><td><b>Function</b></td><td>socket客户端消息事件.</td></tr>
 * <tr><td><b>Date</b></td><td>2015年1月16日 下午4:03:31 ;</td></tr>
 * @author   zhangyonglin@rd.keytop.com.cn
 * @version  1.0.0
 */
public class ClientSocketMsgEvent {
	private String msg;

	private String cmdType;
	
	private boolean isSuccessed;
	
	public ClientSocketMsgEvent(){
		super();
	}
	
	/**
	 * 
	 * setMsg:需要传递的时间数据. <br>
	 * <ul>
	 * <li>在1.0.0版本,由zhangyl创建</li>
	 * </ul>
	 * @param msg 数据内容
	 * @param cmdType socket mdt type
	 * @param isSuccessed 事件是否处理成功
	 */
	public void setMsg(String cmdType,String msg,boolean isSuccessed){
		this.cmdType = cmdType;
		this.msg = msg;
		this.isSuccessed = isSuccessed;
	}
	
	public String getMsg() {
		return msg;
	}
	public String getCmdType() {
		return cmdType;
	}

	public boolean isSuccessed() {
		return isSuccessed;
	}
	
	

}

