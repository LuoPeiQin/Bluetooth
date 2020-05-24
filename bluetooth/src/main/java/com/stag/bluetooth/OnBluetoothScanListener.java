package com.stag.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * 蓝牙扫描监听
 * Created by Administrator on 2016/11/25.
 */

public interface OnBluetoothScanListener {

    void onBluetoothScanFindDevice(BluetoothDevice device, int rssi, boolean isBle);

    void onBluetoothScanFinish();
}
