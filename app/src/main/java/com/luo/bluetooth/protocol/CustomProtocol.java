package com.luo.bluetooth.protocol;


import android.content.Context;

import com.stag.bluetooth.protocol.ParseResult;
import com.stag.bluetooth.protocol.Protocol;

public class CustomProtocol extends Protocol<CustomPacket, CustomEventListener> {

    protected CustomProtocol(Context context) {
        super(context);
    }

    public CustomProtocol(Context context, CustomEventListener listener) {
        super(context, listener);
    }

    /**
     * todo 根据自己的蓝牙协议来将要发送的对象转换最终要发送的 byte[]
     * @param packet 要发送的对象
     * @return
     */
    @Override
    public byte[] packetToBytes(CustomPacket packet) {
        return new byte[0];
    }

    /**
     * todo 接收到蓝牙数据时的,根据自己的协议转换成类
     * @param data 从蓝牙接收到的原始数据
     * @return
     */
    @Override
    public ParseResult parse(byte[] data) {
        return null;
    }

    /**
     * 协议类型，用于支持多协议时的区别
     * @return
     */
    @Override
    public int getType() {
        return 0;
    }
}
