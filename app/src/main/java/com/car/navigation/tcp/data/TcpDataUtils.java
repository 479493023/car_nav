//package com.car.navigation.tcp.data;
//
//import android.content.Context;
//
//import com.keytop.android.apphelper.log.KtLog;
//import com.keytop.commons.collections.ListUtils;
//import com.keytop.tda.R;
//import com.keytop.tda.TdaConstant;
//import com.keytop.tda.TdaParams;
//import com.keytop.tda.event.Client2ServiceWithIdEvent;
//
//import java.io.UnsupportedEncodingException;
//import java.lang.reflect.Array;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.UUID;
//
//import io.realm.RealmList;
//
///**
// * Created by zhangyonglin on 18-2-1.
// * 关于tcp socket方面的数据处理工具
// */
//
//public class TcpDataUtils {
//
//    private TcpDataUtils() {
//    }
//
//    /**
//     * 获取一个long类型的uuid
//     */
//    public static long generateUuid() {
//
//        return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
//    }
//
//    /**
//     * 生成
//     * @param lotId app自定义的lotId，非server端的
//     * @param context
//     */
//    public static ChargeBox generateDefaultLotInfo(long lotId, Context context) {
//
//        ChargeBox chargeBox = new ChargeBox();
//        T_device t_device = new T_device();
//        t_device.setDeviceName(context.getResources().getString(R.string.unattended));
//        t_device.setDeviceIp("0.0.0.0");
//        t_device.setDeviceTypeId(T_device_type.岗亭.getDeviceTypeId());
//        chargeBox.setCustomLotId(lotId);
//
//        RealmList<IoNode> ioNodeList = chargeBox.getIoNodeList();
//
//        IoNode ioNode_o = new IoNode();
//        ioNode_o.setChargeBoxId(chargeBox.getId());
//        T_node t_node_o = new T_node();
//        t_node_o.setId(10002);//TODO 默认的
//        t_node_o.setName("默认出口");
//        t_node_o.setNodeType(new T_node_type(T_node_type.IoType.出口, T_node_type.CardTicketCtlType.无, T_node_type.IoCtlType.不控制).getNodeTypeValue());
//        ioNode_o.setT_node(t_node_o);
//        ioNodeList.add(ioNode_o);
//
//        IoNode ioNode_i = new IoNode();
//        ioNode_i.setChargeBoxId(chargeBox.getId());
//        T_node t_node_i = new T_node();
//        t_node_i.setId(10001);//TODO 默认的
//        t_node_i.setName("默认入口");
//        t_node_i.setNodeType(new T_node_type(T_node_type.IoType.入口, T_node_type.CardTicketCtlType.无, T_node_type.IoCtlType.不控制).getNodeTypeValue());
//        ioNode_i.setT_node(t_node_i);
//        ioNodeList.add(ioNode_i);
//
//        chargeBox.setIoNodeList(ioNodeList);
//        chargeBox.setT_device(t_device);
//
//        return chargeBox;
//    }
//
//    /**
//     * 同步服务器车场数据
//     */
//    public static Client2ServiceWithIdEvent buildQueryLotParkInfoEvent() {
//        Client2ServiceWithIdEvent client2ServiceWithIdEvent = new Client2ServiceWithIdEvent();
//        ProcedureRequest.SqlCommand[] sqlCommands = new ProcedureRequest.SqlCommand[]{
//                new ProcedureRequest.SqlCommand("SELECT id,parkName,totalLots,lotId FROM t_park", ProcedureRequest.SqlType.query),
//                new ProcedureRequest.SqlCommand("SELECT deviceIp,deviceTypeId,deviceName FROM t_device", ProcedureRequest.SqlType.query),
//                new ProcedureRequest.SqlCommand("SELECT id,name,nodeType FROM t_node", ProcedureRequest.SqlType.query),
//                new ProcedureRequest.SqlCommand("SELECT nodeId,deviceIp FROM t_node_device", ProcedureRequest.SqlType.query),
//        };
//        client2ServiceWithIdEvent.setData(TdaParams.BaseCommandType.n_tag, new ProcedureRequest(DatabaseOperation.OperationType.QUERY_LOT, ProcedureRequest.TransactionType.NoTransaction, sqlCommands).format2ByteArray());
//        KtLog.i("同步服务器车场数据>>>operationMethodName "+DatabaseOperation.OperationType.QUERY_LOT
//                    + " sql>>>"+Arrays.toString(sqlCommands));
//        return client2ServiceWithIdEvent;
//    }
//
//
//    /**
//     * 上传插入或替换customlot实例转换成serverlot实例的数据
//     * //TODO 需要优化，暂没有考虑设备信息为空的情况
//     *
//     * @return
//     */
//    public static Client2ServiceWithIdEvent buildUpload2ReplaceDeviceInfoEvent(ServerLot oldServerLot,ServerLot newServerLot) {
//        synchronized (ServerLot.class) {
//            if (newServerLot == null) {
//                return null;
//            } else {
//                Client2ServiceWithIdEvent uploadDeviceInfoEvent = new Client2ServiceWithIdEvent();
//                List<ProcedureRequest.SqlCommand> cmdsList = new LinkedList<>();
//                //t_park
////                final String t_park_sql = "INSERT OR REPLACE INTO t_park (id,parkName) VALUES (%d,'%s')";
//                /**
//                 * 1.t_park表里目前只插入或替换id和parkName
//                 * 2.总车位和已使用车位如果有相同id的记录则取当前记录的值
//                 * 3.总车位和已使用车位如果没有相同id的记录，则填写默认值0
//                 */
//                final String t_park_sql = "INSERT OR REPLACE INTO t_park (id, parkName, totalLots,usedLots) VALUES (%d,'%s',COALESCE((SELECT totalLots FROM t_park WHERE id=%d),0),COALESCE((SELECT usedLots FROM t_park WHERE id=%d),0))";
//                cmdsList.add(new ProcedureRequest.SqlCommand(
//                        String.format(t_park_sql,newServerLot.getT_park().getId(),newServerLot.getT_park().getParkName(),newServerLot.getT_park().getId(),newServerLot.getT_park().getId()),
//                        ProcedureRequest.SqlType.excute));
//
//                //t_device
//                StringBuffer t_device_sql = new StringBuffer("INSERT OR REPLACE INTO t_device (deviceIp,deviceTypeId,deviceName) VALUES ");
//                for (T_device t_device : newServerLot.getT_deviceList()) {
//                    //('172.20.1.110',1,'XXXXXX/'),('172.20.1.111',1,'XXXXXX')
//                    t_device_sql.append("('" + t_device.getDeviceIp() + "'," + t_device.getDeviceTypeId() + ",'" + t_device.getDeviceName() + "'),");
//                }
//                if (!ListUtils.isEmpty(newServerLot.getT_deviceList())) {
//                    t_device_sql.deleteCharAt(t_device_sql.length() - 1);
//                }
//                cmdsList.add(new ProcedureRequest.SqlCommand(t_device_sql.toString(), ProcedureRequest.SqlType.excute));
//
//                //t_node
//                StringBuffer t_node_sql = new StringBuffer("INSERT OR REPLACE INTO t_node (id,name,nodeType) VALUES ");
//                for (T_node t_node : newServerLot.getT_nodeList()) {
//                    //(55,'XXXXXX',100),(56,'XXXXXX','100')
//                    t_node_sql.append("(" + t_node.getId() + ",'" + t_node.getName() + "'," + t_node.getNodeType() + "),");
//                }
//                if (!ListUtils.isEmpty(newServerLot.getT_nodeList())) {
//                    t_node_sql.deleteCharAt(t_node_sql.length() - 1);
//                }
//                cmdsList.add(new ProcedureRequest.SqlCommand(t_node_sql.toString(), ProcedureRequest.SqlType.excute));
//
//                //t_node_device
//                StringBuffer t_node_device_sql = new StringBuffer("INSERT OR REPLACE INTO t_node_device (nodeId,deviceIp) VALUES ");
//                for (T_node_device t_node_device : newServerLot.getT_node_deviceList()) {
//                    //(55,'XXXXXX'),(56,'XXXXXX')
//                    t_node_device_sql.append("(" + t_node_device.getNodeId() + ",'" + t_node_device.getDeviceIp() + "'),");
//                }
//                if (!ListUtils.isEmpty(newServerLot.getT_node_deviceList())) {
//                    t_node_device_sql.deleteCharAt(t_node_device_sql.length() - 1);
//                }
//                cmdsList.add(new ProcedureRequest.SqlCommand(t_node_device_sql.toString(), ProcedureRequest.SqlType.excute));
//
//                //Delete server redundant data
//                if(oldServerLot!=null) {
//
//                    //DELETE FROM t_device WHERE deviceIp in ('172.20.1.110','172.20.1.111');
//                    List<T_device> listT_deviceTemp = new ArrayList(Arrays.asList(new T_device[Math.max(newServerLot.getT_deviceList().size(),oldServerLot.getT_deviceList().size())]));
//                    Collections.copy(listT_deviceTemp, newServerLot.getT_deviceList());
//                    listT_deviceTemp.addAll(oldServerLot.getT_deviceList());
//
//                    HashMap<String,T_device> mapT_deviceTemp = new HashMap<>(listT_deviceTemp.size());
//                    for(T_device t_device : listT_deviceTemp){
//                        if(t_device==null){
//                            continue;
//                        }
//                        mapT_deviceTemp.put(t_device.getDeviceIp(),t_device);
//                    }
//                    for (T_device t_device : newServerLot.getT_deviceList()) {
//                        try {
//                            if(t_device==null){
//                                continue;
//                            }
//                            if (mapT_deviceTemp.get(t_device.getDeviceIp())!=null) {
//                                mapT_deviceTemp.remove(t_device.getDeviceIp());
//                            }
//                        } catch (Exception e) {
//                            KtLog.e("删除t_device冗余数据异常", e);
//                        }
//                    }
//                    listT_deviceTemp = new ArrayList<>(mapT_deviceTemp.values());
//                    if (!ListUtils.isEmpty(listT_deviceTemp)) {
//                        final String del_t_device_str = "DELETE FROM t_device WHERE deviceIp in (%s)";
//                        StringBuffer delT_deviceStrb = new StringBuffer();
//                        for (T_device t_device : listT_deviceTemp) {
//                            if(null!=t_device){
//                                delT_deviceStrb.append("'" + t_device.getDeviceIp() + "',");
//                            }
//                        }
//                        if (delT_deviceStrb.length() > 0) {
//                            delT_deviceStrb.deleteCharAt(delT_deviceStrb.length() - 1);
//                            cmdsList.add(new ProcedureRequest.SqlCommand(String.format(del_t_device_str,delT_deviceStrb.toString()), ProcedureRequest.SqlType.excute));
//                        }
//                    }
//                    //DELETE FROM t_node WHERE id in (55,56);
//                    List<T_node> listT_nodeTemp = new ArrayList(Arrays.asList(new T_node[Math.max(newServerLot.getT_nodeList().size(),oldServerLot.getT_nodeList().size())]));
//                    Collections.copy(listT_nodeTemp, newServerLot.getT_nodeList());
//                    listT_nodeTemp.addAll(oldServerLot.getT_nodeList());
//                    HashMap<Integer,T_node> mapT_nodetemp = new HashMap<>(listT_nodeTemp.size());
//                    for(T_node t_node : listT_nodeTemp){
//                        if(t_node==null){
//                            continue;
//                        }
//                        mapT_nodetemp.put(t_node.getId(),t_node);
//                    }
//
//                    for (T_node t_node : newServerLot.getT_nodeList()) {
//                        try {
//                            if(t_node==null){
//                                continue;
//                            }
//                            if (mapT_nodetemp.get(t_node.getId())!=null) {
//                                mapT_nodetemp.remove(t_node.getId());
//                            }
//                        } catch (Exception e) {
//                            KtLog.e("删除t_node冗余数据异常", e);
//                        }
//                    }
//                    listT_nodeTemp = new ArrayList<>(mapT_nodetemp.values());
//                    if (!ListUtils.isEmpty(listT_nodeTemp)) {
//                        final String del_t_node_str = "DELETE FROM t_node WHERE id in (%s)";
//                        StringBuffer delT_nodeStrb = new StringBuffer();
//                        for (T_node t_node : listT_nodeTemp) {
//                            if(null!=t_node){
//                                delT_nodeStrb.append(t_node.getId() + ",");
//                            }
//                        }
//                        if (delT_nodeStrb.length() > 0) {
//                            delT_nodeStrb.deleteCharAt(delT_nodeStrb.length() - 1);
//                            cmdsList.add(new ProcedureRequest.SqlCommand(String.format(del_t_node_str,delT_nodeStrb.toString()), ProcedureRequest.SqlType.excute));
//                        }
//                    }
//
//                    //DELETE FROM t_node_device WHERE nodeId in(51,56) AND deviceIp in ('XXXXXX','172.19.2.199');
//                    List<T_node_device> listT_nodeDeviceTemp = new ArrayList<>(Arrays.asList(new T_node_device[Math.max(newServerLot.getT_node_deviceList().size(),oldServerLot.getT_node_deviceList().size())]));
//                    Collections.copy(listT_nodeDeviceTemp,oldServerLot.getT_node_deviceList());
//                    listT_nodeDeviceTemp.addAll(newServerLot.getT_node_deviceList());
//                    listT_nodeDeviceTemp.removeAll(newServerLot.getT_node_deviceList());//找到差集(本地数据库里没有，但服务器上有的，需要删除)
//
//                    if(!ListUtils.isEmpty(listT_nodeDeviceTemp)){
//                        final String del_t_node_device_str = "DELETE FROM t_node_device WHERE nodeId in(%s) AND deviceIp in (%s)";
//                        StringBuffer delT_nodeDeviceStrb1 = new StringBuffer();
//                        StringBuffer delT_nodeDeviceStrb2 = new StringBuffer();
//                        for(T_node_device t_node_device :listT_nodeDeviceTemp){
//                            if(null!=t_node_device){
//                                delT_nodeDeviceStrb1.append(t_node_device.getNodeId()+",");
//                                delT_nodeDeviceStrb2.append("'"+t_node_device.getDeviceIp()+"',");
//                            }
//                        }
//                        if(delT_nodeDeviceStrb1.length()>0){
//                            delT_nodeDeviceStrb1.deleteCharAt(delT_nodeDeviceStrb1.length()-1);
//                        }
//                        if(delT_nodeDeviceStrb2.length()>0){
//                            delT_nodeDeviceStrb2.deleteCharAt(delT_nodeDeviceStrb2.length()-1);
//                        }
//                        if(delT_nodeDeviceStrb1.length()>0 && delT_nodeDeviceStrb2.length()>0){
//                            cmdsList.add(new ProcedureRequest.SqlCommand(String.format(del_t_node_device_str,delT_nodeDeviceStrb1.toString(),delT_nodeDeviceStrb2.toString()), ProcedureRequest.SqlType.excute));
//                        }
//                    }
//                }
//
//                uploadDeviceInfoEvent.setData(TdaParams.BaseCommandType.n_tag,
//                        new ProcedureRequest(DatabaseOperation.OperationType.UPDATE_LOT,ProcedureRequest.TransactionType.IsTransaction,
//                                cmdsList.toArray(new ProcedureRequest.SqlCommand[0])).format2ByteArray());
//                KtLog.i("上传当前车场信息至服务器>>>operationMethodName "+DatabaseOperation.OperationType.UPDATE_LOT
//                        +"  sql>>>"
//                        + Arrays.toString((cmdsList.toArray((new ProcedureRequest.SqlCommand[0])))));
//                return uploadDeviceInfoEvent;
//            }
//        }
//    }
//
//    /**
//     * 生成一个查询服务器上lotconfig.db下的t_config的事件
//     * @return
//     */
//    public static Client2ServiceWithIdEvent buildQueryServerConfigEvent() {
//        Client2ServiceWithIdEvent client2ServiceWithIdEvent = new Client2ServiceWithIdEvent();
//        ProcedureRequest.SqlCommand[] sqlCommands = new ProcedureRequest.SqlCommand[]{
//                new ProcedureRequest.SqlCommand("SELECT session,name,value,remark from t_config where session='online' AND name in ('isOn','password')", ProcedureRequest.SqlType.query)
//        };
//        client2ServiceWithIdEvent.setData(TdaParams.BaseCommandType.n_tag, new ProcedureRequest(ProcedureRequest.DbType.config, DatabaseOperation.OperationType.QUERY_CONFIG, ProcedureRequest.TransactionType.NoTransaction, sqlCommands).format2ByteArray());
//        KtLog.i("生成一个查询服务器上lotconfig.db下的t_config的事件>>>operationMethodName "+ProcedureRequest.DbType.config
//                    + "sql>>>"+ Arrays.toString(sqlCommands));
//        return client2ServiceWithIdEvent;
//    }
//
//    /**
//     * 生成一个更新服务器上lotconfig.db下的t_config中关于扫码支付相关配置的事件
//     * @param isOn
//     * @param password
//     * @return
//     */
//    public static Client2ServiceWithIdEvent buildUpdateServerConfigEvnet(String isOn,String password){
//
//        Client2ServiceWithIdEvent client2ServiceWithIdEvent = new Client2ServiceWithIdEvent();
//        ProcedureRequest.SqlCommand[] sqlCommands = new ProcedureRequest.SqlCommand[]{
//                new ProcedureRequest.SqlCommand("UPDATE t_config SET value = CASE name WHEN 'isOn' THEN '"+isOn+"' WHEN 'password' THEN '"+password+"' END WHERE session='online'", ProcedureRequest.SqlType.excute)
//        };
//        client2ServiceWithIdEvent.setData(TdaParams.BaseCommandType.n_tag, new ProcedureRequest(ProcedureRequest.DbType.config, DatabaseOperation.OperationType.UPDATE_CONFIG, ProcedureRequest.TransactionType.NoTransaction, sqlCommands).format2ByteArray());
//        KtLog.i("生成一个更新服务器上lotconfig.db下的t_config中关于扫码支付相关配置的事件>>>operationMethodName "+ProcedureRequest.DbType.config
//                + "sql>>>"+ Arrays.toString(sqlCommands));
//        return client2ServiceWithIdEvent;
//    }
//
//    /**
//     * 生成一个重启视频服务操作的事件
//     * @return
//     */
//    public static Client2ServiceWithIdEvent buildRestartServerEvent(){
//        Client2ServiceWithIdEvent client2ServiceWithIdEvent = new Client2ServiceWithIdEvent();
//        try {
//            client2ServiceWithIdEvent.setData(TdaParams.BaseCommandType.n_tag, TdaConstant.gson.toJson(new RestartServerRequest(DatabaseOperation.OperationType.RESTART_SERVER)).getBytes(TdaConstant.TCP_REQUEST_DATA_CHARSET));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        KtLog.i("生成一个重启视频服务操作的事件>>>"+TdaConstant.gson.toJson(new RestartServerRequest(DatabaseOperation.OperationType.RESTART_SERVER)));
//        return client2ServiceWithIdEvent;
//    }
//}
