/*
 * Copyright (c) 2019. luopeiqin All rights reserved.
 */

package com.stag.bluetooth;

/**
 * 监听手机蓝牙状态
 */
public interface OnBluetoothStateChangeListener {
    /**
     * 手机蓝牙打开
     */
    void onBluetoothOpen();

    /**
     * 手机蓝牙关闭
     */
    void onBluetoothClose();
}
