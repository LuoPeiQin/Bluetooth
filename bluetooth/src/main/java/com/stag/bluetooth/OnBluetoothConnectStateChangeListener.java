/*
 * Copyright (c) 2019. luopeiqin All rights reserved.
 */

package com.stag.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * 连接状态监听
 */

public interface OnBluetoothConnectStateChangeListener {
    /**
     * 蓝牙设备连接回调
     *
     * @param device    蓝牙设备
     * @param isSuccess 是否成功
     */
    void onBluetoothConnect(BluetoothDevice device, boolean isSuccess);

    /**
     * 蓝牙设备断开回调
     *
     * @param device 蓝牙设备
     */
    void onBluetoothDisconnect(BluetoothDevice device);
}
