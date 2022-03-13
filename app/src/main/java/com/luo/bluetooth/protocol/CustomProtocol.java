package com.luo.bluetooth.protocol;


import android.content.Context;
import android.util.Log;

import com.luo.bluetooth.utils.ByteUtils;
import com.stag.bluetooth.protocol.ParseResult;
import com.stag.bluetooth.protocol.Protocol;
import com.stag.bluetooth.protocol.ResultType;
import com.stag.bluetooth.util.LogUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomProtocol extends Protocol<CustomPacket, CustomEventListener> {

    private static final String TAG = "DscpProtocol";

    private final Object recvParseLock = null;

    private final byte FRAME_HEADER = 0x7B;  // 帧头
    private final byte DST_PATH_LEN = 0x01;  // 目标路径长度: 多设备拓展长设置，目前只有0x01
    private final byte DEVICE_ADDRESS = 0x10;  // 地址区域：01->血糖仪；0x20->主机设备
    private final byte PHONE_ADDRESS = 0x20;   // 地址区域：01->血糖仪；0x20->主机设备
    private final byte SRC_PATH_LEN = 0x01;  // 源路径长度：多设备拓展长设置，目前只有0x01
    private final byte EXPAND_CODE = 0x55;   // 扩展码：0x55->读；0x66->写；0xAA->读回应；0x99->写回应
    private final byte FRAME_END = 0x7D;     // 帧尾
    private final int BUFFER_SIZE = 256;

    private ByteBuffer recvBuffer;
    private CustomPacket recvPacket;

    protected CustomProtocol(Context context) {
        super(context);
    }

    public CustomProtocol(Context context, CustomEventListener listener) {
        super(context, listener);
    }

    @Override
    public synchronized byte[] packetToBytes(CustomPacket packet) {
        /**
         * 0x7B：帧头
         * 0x01：目标路径长度
         * 0x10：目标地址区域
         * 0x01：源路径长度
         * 0x20：源地址区域
         * 0x77：获取SN信息功能码
         * 0x55：扩展码 read
         * 0x00 0x00：通讯数据长度
         * 0x00 ...0x00：通讯数据
         * 0x01 0x0B 0x0B 0x04：校验码
         * 0x7D：帧尾
         */
        //实际打包
        int cmd = packet.getCmd();
        byte[] cmdData = packet.getData();
        byte expandCode = packet.getExpandCode();
        ArrayList<Byte> byteList = new ArrayList<Byte>();
        // 1、加入目标路径长度，目标地址区域，源路径长度，源地址区域
        byteList.add(DST_PATH_LEN);
        byteList.add(DEVICE_ADDRESS);
        byteList.add(SRC_PATH_LEN);
        byteList.add(PHONE_ADDRESS);
        LogUtils.d(TAG + "lpq", "1、加入基础头部：" + ByteUtils.toString(listToByte(byteList)));
        // 2、加入功能码
        byteList.add((byte) cmd);
        LogUtils.d(TAG + "lpq", "2、加入功能码：" + ByteUtils.toString(listToByte(byteList)));
        // 3、加入扩展码
        byteList.add(expandCode);
        LogUtils.d(TAG + "lpq", "3、加入扩展码：" + ByteUtils.toString(listToByte(byteList)));
        // 4、加入通讯数据
        if (cmdData != null && cmdData.length != 0) {
            byte[] cmdDataLenBytes = ByteUtils.intToBytes2(cmdData.length);
            byteList.add(cmdDataLenBytes[0]);
            byteList.add(cmdDataLenBytes[1]);
            for (byte temp : cmdData) {
                byteList.add(temp);
            }
        } else {
            byteList.add((byte) 0x0);
            byteList.add((byte) 0x0);
        }
        Log.i("lpq", "packetToBytes: 4、加入通讯数据：" + ByteUtils.toString(listToByte(byteList)));
        // 5、加入校验码
        int crcResult = CRC16Utils.calcCrc16(listToByte(byteList));
        byte[] crcBytes = ByteUtils.intToBytes2(crcResult);
        Log.i("lpq", "packetToBytes: 5、校验码原值：" + ByteUtils.toString(crcBytes));
        byteList.add((byte) ((crcBytes[1] >> 4) & 0x0f));
        byteList.add((byte) (crcBytes[1] & 0x0f));
        byteList.add((byte) ((crcBytes[0] >> 4) & 0x0f));
        byteList.add((byte) (crcBytes[0] & 0x0f));
        LogUtils.d(TAG + "lpq", "5、加入校验码后：" + ByteUtils.toString(listToByte(byteList)));

        // 6、加入头尾帧
        byteList.add(0, FRAME_HEADER);
        byteList.add(FRAME_END);
        LogUtils.d(TAG + "lpq", "6、加入头尾帧：" + ByteUtils.toString(listToByte(byteList)));

        return listToByte(byteList);
    }

    @Override
    public ParseResult parse(byte[] data) {
        LogUtils.d(TAG + "lpq", "蓝牙接收到数据：data = " + ByteUtils.toString(data));
        recvPacket = null;
        final ParseResult result = new ParseResult();
        if (recvBuffer.position() > 0) {
            if (recvBuffer.get(0) != FRAME_HEADER) {
                recvBuffer.clear();
            }
        }
        recvBuffer.put(data);
        LogUtils.d(TAG + "lpq", "parse: " + ByteUtils.toString(recvBuffer.array()));
        LogUtils.d(TAG + "lpq", "parse: position = " + recvBuffer.position());
        if (recvBuffer.get(recvBuffer.position() - 1) != FRAME_END) {
            result.setType(ResultType.INCOMPLETE);
            return result;
        }
        synchronized (recvParseLock) {
            byte[] temp = ByteUtils.subBytes(recvBuffer.array(), 0, recvBuffer.position());
            List<Byte> byteList = byteToList(temp);

            //开始解包
            // 1、去掉包头包尾
            byteList.remove(0);
            byteList.remove(byteList.size() - 1);
            LogUtils.d(TAG + "lpq", "1、去掉包头包尾后：" + ByteUtils.toString(listToByte(byteList)));

            // 2、CRC校验
            // 获取校验码
            byte[] crcOrigin = new byte[4];
            crcOrigin[3] = byteList.remove(byteList.size() - 1);
            crcOrigin[2] = byteList.remove(byteList.size() - 1);
            crcOrigin[1] = byteList.remove(byteList.size() - 1);
            crcOrigin[0] = byteList.remove(byteList.size() - 1);
            // 计算校验码
            int crcResult = CRC16Utils.calcCrc16(listToByte(byteList));
            byte[] crcBytes = ByteUtils.intToBytes2(crcResult);
            Log.i("lpq", "packetToBytes: 2、校验码原值：" + ByteUtils.toString(crcOrigin));
            byte[] crcCount = new byte[4];
            crcCount[0] = ((byte) ((crcBytes[1] >> 4) & 0x0f));
            crcCount[1] = ((byte) (crcBytes[1] & 0x0f));
            crcCount[2] = ((byte) ((crcBytes[0] >> 4) & 0x0f));
            crcCount[3] = ((byte) (crcBytes[0] & 0x0f));
            Log.i("lpq", "packetToBytes: 2、校验码计算值：" + ByteUtils.toString(crcCount));
            if (crcOrigin[0] == crcCount[0] && crcOrigin[1] == crcCount[1]
                    && crcOrigin[2] == crcCount[2] && crcOrigin[3] == crcCount[3]) {
                Log.i("lpq", "parse: 校验成功");
            } else {
                Log.i("lpq", "parse: 校验失败");
            }

            // 3、去除目标路径长度，目标地址区域，源路径长度，源地址区域
            byteList.remove(0);
            byteList.remove(0);
            byteList.remove(0);
            byteList.remove(0);
            LogUtils.d(TAG + "lpq", "3、去除基础头部：" + ByteUtils.toString(listToByte(byteList)));

            // 4、获取功能码
            int cmd = byteList.remove(0);
            byte expandCode = byteList.remove(0);
            LogUtils.d(TAG + "lpq", "4、获取功能码：" + ByteUtils.toString(listToByte(byteList)));

            // 5、获取通讯数据
            byte[] lenBytes = new byte[2];
            lenBytes[0] = byteList.remove(0);
            lenBytes[1] = byteList.remove(0);
            int cmdDataLen = ByteUtils.bytesToInt(lenBytes);
            byte[] cmdData = listToByte(byteList);
            LogUtils.d(TAG + "lpq", "5、获取通讯数据：" + ByteUtils.toString(listToByte(byteList)));

            recvPacket = new CustomPacket(cmd, cmdData, expandCode);
            LogUtils.d(TAG + "lpq", "parse: " + recvPacket.toString());
        }
        result.setPacket(recvPacket);
        switch (recvPacket.getCmd()) {
            // 设备主动上传设备状态
//                if (haveSetEventListener()) {
//                    result.setCallback(new BluetoothDispatch.Callback() {
//                        @Override
//                        public void callback() {
//                            getEventListener().onRecvDeviceStatus(recvPacket.getData());
//                        }
//                    });
//                }
//                result.setType(ACTIVE);
            default://理论上都是待响应事件
                result.setType(ResultType.RESPOND);
                break;
        }

        recvBuffer.clear();
        return result;
    }

    /**
     * list转换成byte
     */
    private byte[] listToByte(List<Byte> byteList) {
        if (null == byteList || byteList.size() == 0) {
            return null;
        }
        byte[] bytes = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); ++i) {
            bytes[i] = byteList.get(i);
        }
        return bytes;
    }

    private List<Byte> byteToList(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        List<Byte> byteList = new ArrayList<Byte>();
        for (int i = 0; i < bytes.length; ++i) {
            byteList.add(bytes[i]);
        }
        return byteList;
    }

    private void addBytes(ArrayList<Byte> byteList, byte[] encodeBytes) {
        for (int i = 0; i < encodeBytes.length; i++) {
            byteList.add(encodeBytes[i]);
        }
    }

    /**
     * 协议类型，用于支持多协议时的区别
     *
     * @return
     */
    @Override
    public int getType() {
        return 0;
    }

    @Override
    public UUID getServiceUUID() {
        return UUID.fromString("0003CDD0-0000-1000-8000-00805F9B0131");
    }

    @Override
    public UUID getSendTunnelUUID() {
        return UUID.fromString("0003CDD2-0000-1000-8000-00805F9B0131");
    }

    @Override
    public UUID getRecvTunnelUUID() {
        return UUID.fromString("0003CDD1-0000-1000-8000-00805F9B0131");
    }

}
