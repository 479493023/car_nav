package com.car.navigation.tcp.data;

/**
 * Created by caoJZ on 2018/2/7.
 */

public class CameraViewEntity{

    /**
     * 相机ip
     */
    private String localIP;
    /**
     * 相机NodeName
     */
    private String NodeName;
    /**
     * 相机ID
     */
    private String cameraID;
    /**
     * 相机 netmask
     */
    private String netmask;
    /**
     * 相机 gatway
     */
    private String gatway;
    /**
     * 相机 dns
     */
    private String dns;

    /**
     * 当前相机所在的岗亭
     */
    private String chargeBox;
    /**
     * 当前相机所在的车场下的岗亭的index
     */
    private int chargeBoxIndex;
    /**
     * 当前相机所在的进出口
     */
    private String ioNode;
    /**
     * 当前相机所在的岗亭下的进出口的index
     */
    private int ioNodeIndex;

    /**
     * 当前相机的类型type
     */
    private int  cameraType;

    public String getNetmask() {
        return netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public String getGatway() {
        return gatway;
    }

    public void setGatway(String gatway) {
        this.gatway = gatway;
    }

    public String getDns() {
        return dns;
    }

    public void setDns(String dns) {
        this.dns = dns;
    }

    public String getCameraID() {
        return cameraID;
    }

    public void setCameraID(String cameraID) {
        this.cameraID = cameraID;
    }

    public int getCameraType() {
        return cameraType;
    }

    public void setCameraType(int cameraType) {
        this.cameraType = cameraType;
    }

    public int getChargeBoxIndex() {
        return chargeBoxIndex;
    }

    public void setChargeBoxIndex(int chargeBoxIndex) {
        this.chargeBoxIndex = chargeBoxIndex;
    }

    public int getIoNodeIndex() {
        return ioNodeIndex;
    }

    public void setIoNodeIndex(int ioNodeIndex) {
        this.ioNodeIndex = ioNodeIndex;
    }

    public String getChargeBox() {
        return chargeBox;
    }

    public void setChargeBox(String chargeBox) {
        this.chargeBox = chargeBox;
    }

    public String getIoNode() {
        return ioNode;
    }

    public void setIoNode(String ioNode) {
        this.ioNode = ioNode;
    }

    public String getLocalIP() {
        return localIP;
    }

    public void setLocalIP(String localIP) {
        this.localIP = localIP;
    }

    public String getNodeName() {
        return NodeName;
    }

    public void setNodeName(String nodeName) {
        NodeName = nodeName;
    }

    @Override
    public String toString() {
        return "CameraViewEntity{" +
                "localIP='" + localIP + '\'' +
                ", NodeName='" + NodeName + '\'' +
                ", chargeBox='" + chargeBox + '\'' +
                ", chargeBoxIndex=" + chargeBoxIndex +
                ", ioNode='" + ioNode + '\'' +
                ", ioNodeIndex=" + ioNodeIndex +
                ", cameraType=" + cameraType +
                '}';
    }
}
