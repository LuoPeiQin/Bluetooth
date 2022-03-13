/*
 * Copyright (c) 2019. stag All rights reserved.
 */

package com.luo.bluetooth.protocol;

/**
 * 蓝牙任务回调
 */
public interface OnTimeoutResult<T> {

    void onResult(boolean isTimeout, T result);
}
