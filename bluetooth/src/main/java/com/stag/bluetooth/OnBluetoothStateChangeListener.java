package com.stag.bluetooth;

/**
 * 监听手机蓝牙状态
 * Created by Administrator on 2016/11/18.
 */

public interface OnBluetoothStateChangeListener {
    /**手机蓝牙打开*/
    void onBluetoothOpen();
    /**手机蓝牙关闭*/
    void onBluetoothClose();
}
