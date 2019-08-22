/*
 * Copyright (c) 2019. luopeiqin All rights reserved.
 */

package com.stag.bluetooth.protocol;

/**
 * 蓝牙发送接收数据包
 */
public class Packet<T extends Packet> {

    protected int cmd;  //命令
    protected byte[] data;//数据（包含的主要信息）
//    protected int serialNumber;//流水号，暂时业务不需要

    public Packet(int cmd) {
        this(cmd, new byte[0]);
    }

    public Packet(int cmd, byte[] data) {
        this.cmd = cmd;
        this.data = data == null ? new byte[0] : data;
    }

    /**
     * 用来与接收包进行匹配判断
     *
     * @param recvPacket 接收到的字节数据处理的结果包
     */
    public boolean match(T recvPacket) {
        return cmd == recvPacket.cmd;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
