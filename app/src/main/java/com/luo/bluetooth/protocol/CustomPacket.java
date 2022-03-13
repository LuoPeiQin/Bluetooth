package com.luo.bluetooth.protocol;

import com.stag.bluetooth.packet.Packet;

public class CustomPacket extends Packet {
    private byte expandCode; // 扩展码：0x55->读；0x66->写；0xAA->读回应；0x99->写回应

    public CustomPacket(int cmd, byte expandCode) {
        super(cmd);
        this.expandCode = expandCode;
    }

    public CustomPacket(int cmd, byte[] data, byte expandCode) {
        super(cmd, data);
        this.expandCode = expandCode;
    }

    public byte getExpandCode() {
        return expandCode;
    }

    /**
     * 用于判断发送的命令是否已经收到回应，没有收到回应底层会重发一次数据
     * @param recvPacket 接收到的字节数据处理的结果包
     * @return
     */
    @Override
    public boolean match(Packet recvPacket) {
        return super.match(recvPacket);
    }
}
