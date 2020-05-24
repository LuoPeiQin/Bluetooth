package com.luo.bluetooth.protocol;

import com.stag.bluetooth.packet.Packet;

public class CustomPacket extends Packet {
    public CustomPacket(int cmd) {
        super(cmd);
    }

    public CustomPacket(int cmd, byte[] data) {
        super(cmd, data);
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
