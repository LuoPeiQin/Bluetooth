/*
 * Copyright (c) 2019. luopeiqin All rights reserved.
 */

package com.stag.bluetooth;

/**
 * 蓝牙数据收发监听
 */
public interface OnBluetoothTransmitListener {

    void onBluetoothSendData(byte[] data);

    void onBluetoothRecvData(byte[] data);
}
