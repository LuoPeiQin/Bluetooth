package com.luo.bluetooth.customview.searchble;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by Administrator on 2016/7/23 0023.
 */
@Data
public class BluetoothDeviceBean implements Serializable {
    private String deviceName;
    private String deviceAddress;
    private int status;
    private boolean isBle;
    private String nickName;
    private int rssi;

}
