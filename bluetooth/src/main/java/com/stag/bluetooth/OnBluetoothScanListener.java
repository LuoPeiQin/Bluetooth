/*
 * Copyright (c) 2019. luopeiqin All rights reserved.
 */

package com.stag.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * 蓝牙扫描监听
 */
public interface OnBluetoothScanListener {

    void onBluetoothScanFindDevice(BluetoothDevice device, int rssi);

    void onBluetoothScanFinish();
}
