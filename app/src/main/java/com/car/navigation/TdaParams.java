package com.car.navigation;

/**
 *
 * <table><tr><td><b>ClassName</b></td><td>CCSParams</td></tr>
 * <tr><td><b>Function</b></td><td>各种使用于全局的参数常量,变量.</td></tr>
 * <tr><td><b>Date</b></td><td>2015年1月7日 上午10:08:23 </td></tr></table>
 *
 * @author zhangyonglin@rd.keytop.com.cn
 * @version 1.0.0
 */
public class TdaParams {

	public static final int heartBeatSpacing_5=5*1000;
	public static final long DAY_2_MILLISECOND = 24*60*60*1000;
	//socket心跳周期，单位毫秒
	public static final int SOCKET_HEARTBEAT_CYCLE = 5 * 1000;

	/**标准版协议心跳间隔*/
	public static byte dataPacket_head = (byte) 0xfb;// 头
	public static byte dataPacket_tail = (byte) 0xfe;// 尾

	public static class BaseCommandType{
		public static final byte regist_tag = 'C';// 注册包指令
		public static final byte heartBeat_tag = 'F';// 心跳指令
		public static final byte outCar_tag = 'A';// 是否出车指令
		public static final byte custom_stop_socket_thread_tag = '~';// 自定义停止
		public static final byte custom_start_socket_thread_tag = '!';// 自定义开始
		public static final byte v_tag = 'V';// 版本号指令
		public static final byte extend_tag = 'Y';//服务器与DSP间可扩展指令（‘Y’）
		public static final byte pic_tag = 'J';//图像采集
		public static final byte manual_tag = 'G';//人工要求上传照片
		public static final byte led_tag = 'P';//屏显指令
		public static final byte z_tag = 'Z';//服务器与控制器间可扩展指令
		public static final byte r_tag = 'R';//车辆放行指令
		public static final byte n_tag = 'n';// 数据包通信包为小写的n指令
	}

	/** 检查数据包的完整性 0：成功 */
	public static final int data_state_success = 0;
	/** 检查数据包的完整性 1：失败 */
	public static final int data_state_fail = 1;
	/** 检查数据包的完整性 2：包头尾不符合 */
	public static final int data_state_error_2 = 2;
	/** 检查数据包的完整性 3：总包数与包序号不符合 */
	public static final int data_state_error_3 = 3;
	/** 检查数据包的完整性 4：数据长度与内容不符合 */
	public static final int data_state_error_4 = 4;
	/** 检查数据包的完整性 5：校验码不符合 */
	public static final int data_state_error_5 = 5;
	/** 检查数据包的完整性 6：数据内容错误 */
	public static final int data_state_error_6 = 6;
	/**
	 * 2字节 0x000A: DSP向服务器发送附属设备状态指令
	 * 2字节 0x0001
	 * 1字节 0：成功 1：失败；2：包头尾不符合；3：数据长度与内容不符合；4：校验码不符合；5：数据内容错误
	 */
	public static byte[] resp_query_statue=new byte[]{(byte)0x00,(byte)0x0A,(byte)0x00,(byte)0x01,(byte)0x09};


	public static class SocketParams {
		/** 服务器byte流转字符串格式 */
		public static final String SOCKET_DATA_PARSE_FORMAT = "GBK";
	}


	public static class SocketDataType{
		/**工具与相机间的数据交互数据包类型*/
		public static final byte udp = 0x04;
		/**登录数据包类型*/
		public static final byte login = 0x00;
		/**心跳包类型*/
		public static final byte heart = 0x01;
		/**IPCam通讯数据包类型*/
		public static final byte IPCam = 0x02;
		/**二进制数据流包类型*/
		public static final byte byteData = 0x03;
		/**IPCam参数设置指令包类型*/
		public static final byte IPCam_param = 0x04;
		/**IPCam传视频流包类型*/
		public static final byte IPCam_vedio = 0x05;//add 20150424 by zhangyl
		/**提前缴费接口:暂未与IPcam客户端协商,自助缴费机提前缴费用*/
		public static final byte advance_pay=0x10;
		/**调试数据包类型*/
		public static final byte Debug = (byte)0xff;
		
		/**
		 * 
		 * isLegal:检查socket数据类型是否合法. <br>
		 * <ul>
		 * <li>在1.0.0版本,由zhangyl创建</li>
		 * </ul>
		 * @param type socket数据类型
		 * @return 是否合法
		 */
		public static boolean isLegal(byte type) {
			switch (type) {
			case login:
			case heart:
			case IPCam:
			case byteData:
			case IPCam_param:
			case IPCam_vedio:
			case advance_pay:
			case Debug:
				return true;
			default:
				return false;
			}
		}

	}

	public static class SocketResponse{
		/** 是否出车指令 0x0002 0x0001应答成功数据 */
		public static final byte[] outCarTagReciveData_1 = new byte[] { (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x01, (byte) 0x00 };
		/** 是否出车指令 0x0002 0x0002应答成功数据 */
		public static final byte[] outCarTagReciveData_2 = new byte[] { (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x02, (byte) 0x00 };
		/**0x0002 0x0004 应答指令*/
		public static final byte[] outCarTagReciveData_4 = new byte[] { (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x04, (byte) 0x00 };
		/**0x0002 0x0006 应答指令*/
		public static final byte[] outCarTagReciveData_6 = new byte[] { (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x06, (byte) 0x00 };
		/**0x0002 0x0007 应答指令*/
		public static final byte[] uploadLogTagReciveData_207_OK = new byte[] { (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x07, (byte) 0x00 };//成功
		/**0x0002 0x0007 应答指令*/
		public static final byte[] uploadLogTagReciveData_207_FAIL = new byte[] { (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x07, (byte) 0x01 };//失败
		/**注册包实体数据内容*/
		public static final byte[] registData = new byte[] { (byte) 0x08, (byte) 0x04, (byte) 0x00 }; // 注册包实体数据内容
		/**微信入车预约*/
		public static final byte[] wxIncarData = new byte[] { (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x01, (byte) 0x00 };//微信入车预约
		/**微信出车提前缴费*/
		public static final byte[] wxOutcarData = new byte[] { (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x02, (byte) 0x00 };//微信出车提前缴费
		/**微信出车提前缴费*/
		public static final byte[] wx0304Response = new byte[] { (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x04, (byte) 0x00 };//微信出车提前缴费
		/**微信锁车指令回复*/
		public static final byte[] wxlockData = new byte[] { (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x03, (byte) 0x00 };//微信锁车指令回复
		/**微信月租车续费指令回复*/
		public static final byte[] wxRenewalsData = new byte[] { (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x04, (byte) 0x00 };//微信月租车续费指令回复
	}

	public static class SocketCmdType {

		public static final String udpSearch = "ipcamSearchBC";
		public static final String udpSettingIp = "networkConfigBC";

		public static final String addDevice = "procedure";

	}

	public static class SocketSqlsType{

		public static final String addDeviceQueryType = "query";
		public static final String addDeviceExcuteType = "excute";
	}

	public static class SocketSqlsTransaction{
		public static final String transaction = "1";
	}

	public static class SocketSqlsStatement{
//		public static final String ExcuteStatement = "insert into t_device(deviceIp,deviceTypeId,deviceName)values('"+deviceIp+"',"+deviceType+",'"+Ashebei+"')";
	}

}

