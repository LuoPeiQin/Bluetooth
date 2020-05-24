package com.stag.bluetooth.protocol;

import com.stag.bluetooth.BluetoothDispatch;
import com.stag.bluetooth.packet.Packet;

/**
 * 蓝牙收到的数据解析结果
 * Created by Administrator on 2016/11/15.
 */

public final class ParseResult {

    private ResultType type;

    private Packet packet;

    private BluetoothDispatch.Callback callback;    //主动事件才不为null

    public ParseResult(){

    };

    public ParseResult(ResultType type, Packet packet, BluetoothDispatch.Callback callback) {
        this.type = type;
        this.packet = packet;
        this.callback = callback;
    }

    public ResultType getType() {
        return type;
    }

    public void setType(ResultType type) {
        this.type = type;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public BluetoothDispatch.Callback getCallback() {
        return callback;
    }

    public void setCallback(BluetoothDispatch.Callback callback) {
        this.callback = callback;
    }
}
