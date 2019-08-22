/*
 * Copyright (c) 2019. luopeiqin All rights reserved.
 */

package com.stag.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.text.TextUtils;

import com.stag.bluetooth.extend.BleService;
import com.stag.bluetooth.helper.BleHelper;
import com.stag.bluetooth.helper.BluetoothHelper;
import com.stag.bluetooth.helper.TraditionHelper;
import com.stag.bluetooth.protocol.Protocol;
import com.stag.bluetooth.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 蓝牙总控制，提供外部需要调用的接口
 */

public final class BluetoothController {

    private static BluetoothController controller;
    private static BluetoothHelper mHelper;
    private Protocol mProtocol;
    private BluetoothType mBluetoothType;
    private Context mContext;
    private OnBluetoothScanListener mScanListener;                         //蓝牙扫描监听
    private OnBluetoothConnectStateChangeListener mCurrentConnectlistener; //当前controller连接状态监听
    private OnBluetoothConnectStateChangeListener mParamConnectlistener;   //传进来的连接状态监听
    private OnBluetoothStateChangeListener mBluetoothStateListener;     //监听手机蓝牙状态
    private List<BluetoothDevice> mDevices;
    private boolean mScanRemoveRepeat;          //搜索设备进行排重检查
    private boolean isScanning;
    private boolean isStartScanning;

    public static BluetoothController getController(Context context) {
        if (controller == null) {
            synchronized (BluetoothController.class) {
                if (controller == null) {
                    if (context == null)
                        throw new NullPointerException("BluetoothController init fail, context is null!");
                    controller = new BluetoothController(context);
                }
            }
        }
        return controller;
    }

    private BluetoothController(Context context) {
        mContext = context.getApplicationContext();
        mDevices = new ArrayList<>(20);
        mCurrentConnectlistener = new OnBluetoothConnectStateChangeListener() {
            @Override
            public void onBluetoothConnect(BluetoothDevice device, boolean isSuccess) {
                if (isSuccess) {
                    /*设置发送方法*/
                    setSendMethod();
                    /**启动蓝牙传输*/
                    BluetoothTransfer.getInstance().start();
                }
                if (mParamConnectlistener != null)
                    mParamConnectlistener.onBluetoothConnect(device, isSuccess);
            }

            @Override
            public void onBluetoothDisconnect(BluetoothDevice device) {
                BluetoothTransfer.getInstance().stop();
                setProtocol(null);
                if (mParamConnectlistener != null)
                    mParamConnectlistener.onBluetoothDisconnect(device);
            }
        };
        setBluetoothType(BluetoothType.BLE);
    }

    /**
     * 蓝牙连接状态监听
     */
    public OnBluetoothConnectStateChangeListener getConnectStateChangeListener() {
        return mParamConnectlistener;
    }

    public void registerConnectStateChangeListener(final OnBluetoothConnectStateChangeListener listener) {
        mParamConnectlistener = listener;
    }

    public void unregisterConnectStateChangeListener() {
        registerConnectStateChangeListener(null);
    }

    /**
     * 蓝牙开关状态监听
     */
    public OnBluetoothStateChangeListener getBluetoothStateChangeListener() {
        return mBluetoothStateListener;
    }

    public void registerBluetoothStateChangeListener(OnBluetoothStateChangeListener listener) {
        mBluetoothStateListener = listener;
    }

    public void unregisterBluetoothStateChangeListener() {
        registerBluetoothStateChangeListener(null);
    }

    /**
     * 蓝牙数据传输监听
     */
    protected OnBluetoothTransmitListener getTransmitListener() {
        if (mHelper != null)
            return mHelper.getTransmitListener();
        return null;
    }

    public void registerTransmitListener(OnBluetoothTransmitListener transmitListener) {
        BleHelper.getInstance(mContext).setTransmitListener(transmitListener);
        TraditionHelper.getInstance(mContext).setTransmitListener(transmitListener);
    }

    public void unregisterTransmitListener() {
        registerTransmitListener(null);
    }

    /**
     * 设置蓝牙类型
     */
    public void setBluetoothType(BluetoothType bluetoothType) {
        if (mBluetoothType == bluetoothType)
            return;
        mBluetoothType = bluetoothType;
        if (mBluetoothType == BluetoothType.BLE) {
            mHelper = BleHelper.getInstance(mContext);
        } else {
            mHelper = TraditionHelper.getInstance(mContext);
        }
        mHelper.setProtocol(mProtocol);
        mHelper.setConnectStateChangeListener(mCurrentConnectlistener);
        setSendMethod();
    }

    /**
     * 获取蓝牙类型,BLE或者传统
     */
    public BluetoothType getBluetoothType() {
        return mBluetoothType;
    }

    /**
     * 切换蓝牙类型
     */
    public boolean switchBluetoothType(BluetoothType bluetoothType) {
        if (mHelper.isConnected())
            return false;
        setBluetoothType(bluetoothType == BluetoothType.BLE ? BluetoothType.BLE : BluetoothType.TRADITION);
        mHelper.setProtocol(mProtocol);
        return true;
    }

    /**
     * 设置传输协议
     */
    public void setProtocol(Protocol protocol) {
        if (mProtocol != null) {
            mProtocol.destroy();
        }
        mProtocol = protocol;
        if (mProtocol != null)
            mProtocol.initialize();
        if (mHelper != null)
            mHelper.setProtocol(protocol);
        BluetoothTransfer.getInstance().setProtocol(mProtocol);
    }

    public Protocol getProtocol() {
        return mProtocol;
    }

    /**
     * 是否已经连接设备
     */
    public static boolean isConnected() {
        if (mHelper != null)
            return mHelper.isConnected();
        return false;
    }

    public static boolean isConnecting() {
        if (mHelper != null)
            return mHelper.isConnecting();
        return false;
    }

    /**
     * 是否正在扫描蓝牙设备
     */
    public boolean isScanning() {
        return isScanning;
    }

    protected boolean setBleHighConnectionPriority(boolean flag) {
        boolean res = false;
        if (mHelper != null && mBluetoothType == BluetoothType.BLE)
            res = ((BleHelper) mHelper).setHighConnectionPriority(flag);
        return res;
    }

    /**
     * 设置低功耗蓝牙的发送速率
     */
    public boolean setBleHighSpeedMode(boolean flag) {
        boolean res = false;
        if (mHelper != null && mBluetoothType == BluetoothType.BLE)
            res = ((BleHelper) mHelper).setHighSpeedMode(flag);
        return res;
    }

    public boolean isBleHighSpeedMode() {
        boolean res = false;
        if (mHelper != null && mBluetoothType == BluetoothType.BLE)
            res = ((BleHelper) mHelper).isHighSpeedMode();
        return res;
    }

    /**
     * 设置发送方法
     */
    private void setSendMethod() {
        BluetoothTransfer.getInstance().setSend(new BluetoothTransfer.SendMethod() {
            @Override
            public void send(byte[] data) {
                mHelper.send(data);
            }
        });
    }

    public void sendData(byte[] data) {
        if (mHelper != null) {
            mHelper.send(data);
        }
    }

    /**
     * 连接设备，必须先设置Protocol或者使用connect(String address, Protocol protocol)
     */
    public void connect(String address) {
        connect(address, mProtocol);
    }

    public void connect(String address, Protocol protocol) {
        if (isConnecting() || isConnected()) {
            LogUtils.e("lpq", "connect: " + "蓝牙已连接，无法同时连接多个蓝牙");
            return;
        }
        setProtocol(protocol);
        mHelper.connect(address);
    }

    /**
     * 断开设备
     */
    public void disconnect() {
        mHelper.disconnect();
        unregisterBluetoothBroadcast();
        BluetoothTransfer.getInstance().stop();
    }

    /**
     * 扫描设备
     */
    public void startScan(OnBluetoothScanListener listener) {
        if (isScanning) {
            isStartScanning = true;
            stopScan();
        }
        if (mScanListener != listener)
            mScanListener = listener;
        registerBluetoothBroadcast();
        isScanning = true;
        mHelper.startScan();
    }

    /**
     * 停止扫描
     */
    public void stopScan() {
        if (isScanning) {
            mScanListener = null;//防止DeviceListActivity内存泄漏
            mHelper.stopScan();
            if (isScanRemoveRepeat())
                mDevices.clear();
            isScanning = false;
        }
    }

    private void registerBluetoothBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BleService.BLE_DEVICE_FOUND);
        filter.addAction(BleHelper.BLE_SCAN_FINISH);
        mContext.registerReceiver(mReceiver, filter);
    }

    private void unregisterBluetoothBroadcast() {
        if (mReceiver != null) {
            try {
                mContext.unregisterReceiver(mReceiver);
            } catch (Exception e) {

            }
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device = null;
            int rssi;
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
//                    case BluetoothAdapter.STATE_TURNING_ON:
                        case BluetoothAdapter.STATE_ON:
                            if (mBluetoothStateListener != null)
                                mBluetoothStateListener.onBluetoothOpen();
                            break;
//                    case BluetoothAdapter.STATE_TURNING_OFF:
                        case BluetoothAdapter.STATE_OFF:
                            if (mBluetoothStateListener != null)
                                mBluetoothStateListener.onBluetoothClose();
                            break;
                        default:
                            break;
                    }
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    LogUtils.i("lpq", "discontect from broadcast");
                    if (mHelper != null)
                        mHelper.disconnect();
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    rssi = (int) intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, (short) 0);
                    handleScanFindResult(device, rssi, false);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    if (mScanListener != null)
                        mScanListener.onBluetoothScanFinish();
                    if (isStartScanning) {
                        isStartScanning = false;
                    } else {
                        stopScan();
                    }
                    break;
                case BleService.BLE_DEVICE_FOUND:
                    device = intent.getParcelableExtra(BleService.EXTRA_DEVICE);
                    rssi = intent.getIntExtra(BleService.EXTRA_RSSI, 0);
                    handleScanFindResult(device, rssi, true);
                    break;
                case BleHelper.BLE_SCAN_FINISH:
                    //因为BLE的扩展SDK没有这个，这个是另外加的，由BleHelper发送
                    if (mScanListener != null)
                        mScanListener.onBluetoothScanFinish();
                    break;
                default:
                    break;
            }

        }
    };

    /**
     * 处理扫描发现结果
     */
    private void handleScanFindResult(BluetoothDevice device, int rssi, boolean isBle) {
        if (device == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            LogUtils.i("lpq", "Bluetooth name:" + device.getName() + "  address:" + device.getAddress() + "  type:" + device.getType());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && (isBle ^ device.getType() == BluetoothDevice.DEVICE_TYPE_LE)) {
            return;
        }
        if (TextUtils.isEmpty(device.getName())) {
            return;
        }

        if (isScanRemoveRepeat()) {
            for (BluetoothDevice m : mDevices) {
                if (m.getAddress().equals(device.getAddress())) {
                    return;
                }
            }
            mDevices.add(device);
        }

        if (mScanListener != null)
            mScanListener.onBluetoothScanFindDevice(device, rssi);
    }

    public void setScanRemoveRepeat(boolean b) {
        mScanRemoveRepeat = b;
    }

    public boolean isScanRemoveRepeat() {
        return mScanRemoveRepeat;
    }
}
