package com.stag.bluetooth;

/**
 * 蓝牙数据收发监听
 * Created by Administrator on 2016/12/21.
 */

public interface OnBluetoothTransmitListener {

    void onBluetoothSendData(byte[] data);
    void onBluetoothRecvData(byte[] data);
}
