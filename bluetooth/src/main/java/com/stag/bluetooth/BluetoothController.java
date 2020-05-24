package com.stag.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.stag.bluetooth.extend.BleService;
import com.stag.bluetooth.helper.BleHelper;
import com.stag.bluetooth.helper.BluetoothHelper;
import com.stag.bluetooth.helper.TraditionHelper;
import com.stag.bluetooth.helper.TraditionServerHelper;
import com.stag.bluetooth.protocol.Protocol;
import com.stag.bluetooth.util.Logs;

import java.util.LinkedList;
import java.util.List;

/**
 * 蓝牙总控制
 * Created by LPQ on 2017/11/14.
 */

public final class BluetoothController {

    //Bluetooth type
    public static final short TYPE_BLE = 1; // 客户端：低功耗蓝牙
    public static final short TYPE_TRADITION = 2; // 客户端：传统蓝牙
    public static final short TYPE_TRADITION_SERVER = 3; // 服务器：传统蓝牙
    //Bluetooth scan filter
//    public static final short SCAN_FILTER_ALL = 0;
//    public static final short SCAN_FILTER_NAME_NON_NULL = 1;
//    public static final short SCAN_FILTER_TYPE_MATCHING = 2;
    private static BluetoothController controller;
    private BluetoothHelper mHelper;
    private Protocol mProtocol;
    private short mBluetoothType;
    private Context mContext;
    private OnBluetoothScanListener mScanListener;                                 //蓝牙扫描监听
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
                if (controller == null)
                    if (context == null)
                        throw new NullPointerException("BluetoothController init fail, context is null!");
                controller = new BluetoothController(context);
            }
        }
        return controller;
    }

    private BluetoothController(Context context) {
        mContext = context.getApplicationContext();
        mDevices = new LinkedList<BluetoothDevice>();
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
        setBluetoothType(TYPE_BLE);
    }

    public OnBluetoothConnectStateChangeListener getConnectStateChangeListener() {
        return mParamConnectlistener;
    }

    public void registerConnectStateChangeListener(final OnBluetoothConnectStateChangeListener listener) {
        mParamConnectlistener = listener;
    }

    public void unregisterConnectStateChangeListener() {
        registerConnectStateChangeListener(null);
    }

    public OnBluetoothStateChangeListener getBluetoothStateChangeListener() {
        return mBluetoothStateListener;
    }

    public void registerBluetoothStateChangeListener(OnBluetoothStateChangeListener listener) {
        mBluetoothStateListener = listener;
    }

    public void unregisterBluetoothStateChangeListener(){
        registerBluetoothStateChangeListener(null);
    }

    public OnBluetoothTransmitListener getTransmitListener() {
        if (mHelper != null)
            return mHelper.getTransmitListener();
        return null;
    }

    public void registerTransmitListener(OnBluetoothTransmitListener transmitListener) {
//        BleHelper.getInstance(mContext).setTransmitListener(transmitListener);
//        TraditionHelper.getInstance(mContext).setTransmitListener(transmitListener);
        mHelper.setTransmitListener(transmitListener);
    }

    public void unregisterTransmitListener(){
        registerTransmitListener(null);
    }

    public void setBluetoothType(short bluetoothType) {
        if (mBluetoothType == bluetoothType)
            return;
        mBluetoothType = bluetoothType;
        if (mBluetoothType == TYPE_BLE) {
            mHelper = BleHelper.getInstance(mContext);
        } else if (mBluetoothType == TYPE_TRADITION){
            mHelper = TraditionHelper.getInstance(mContext);
        } else {
            mHelper = TraditionServerHelper.getInstance(mContext);
        }
        mHelper.setProtocol(mProtocol);
        mHelper.setConnectStateChangeListener(mCurrentConnectlistener);
        setSendMethod();
    }

    public void setProtocol(Protocol protocol) {
        if (mProtocol != null) {
            mProtocol.destroy();
        }
        mProtocol = protocol;
        if (mProtocol!=null)
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
    public boolean isConnected() {
        if (mHelper != null)
            return mHelper.isConnected();
        return false;
    }

    public boolean isConnecting(){
        if (mHelper != null)
            return mHelper.isConnecting();
        return false;
    }

    public boolean isScanning(){
        return isScanning;
    }

    /**
     * 获取蓝牙类型,BLE或者传统
     */
    public short getBluetoothType() {
        return mBluetoothType;
    }

    public boolean setBleHighConnectionPriority(boolean flag){
        boolean res = false;
        if (mHelper!=null && mBluetoothType==TYPE_BLE)
            res = ((BleHelper) mHelper).setHighConnectionPriority(flag);
        return res;
    }

    public boolean setBleHighSpeedMode(boolean flag){
        boolean res = false;
        if (mHelper!=null && mBluetoothType==TYPE_BLE)
            res = ((BleHelper) mHelper).setHighSpeedMode(flag);
        return res;
    }

    public boolean isBleHighSpeedMode(){
        boolean res = false;
        if (mHelper!=null && mBluetoothType==TYPE_BLE)
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

    public void connect(String address, Protocol protocol){
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
        if (mScanListener!=listener)
            mScanListener = listener;
        registerBluetoothBroadcast();
        isScanning = true;
        mHelper.startScan();
//        BleHelper.getInstance(mContext).startScan();
//        TraditionHelper.getInstance(mContext).startScan();
    }

    /**
     * 停止扫描
     */
    public void stopScan() {
        if (isScanning) {
            mScanListener = null;//防止DeviceListActivity内存泄漏
            mHelper.stopScan();
//            BleHelper.getInstance(mContext).stopScan();
//            TraditionHelper.getInstance(mContext).stopScan();
            if (isScanRemoveRepeat())
                mDevices.clear();
            isScanning = false;
        }
    }

    /*private void stopScan(boolean isSelf){
        if (isSelf){
            stopScan();
        }
    }*/

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
            switch (intent.getAction()){
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
                    Logs.d("LPQ", "discontect from broadcast");
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
                    if (isStartScanning){
                        isStartScanning = false;
                    }else {
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
                default:break;
            }

        }
    };

    /**
     * 处理扫描发现结果
     */
    private void handleScanFindResult(BluetoothDevice device, int rssi, boolean isBle) {
        if (device == null) {
            return;
        }
        if (TextUtils.isEmpty(device.getName())) {
            return;
        }

        if (mScanRemoveRepeat) {
            for (BluetoothDevice m : mDevices) {
                if (m.getAddress().equals(device.getAddress())) {
                    return;
                }
            }
            mDevices.add(device);
        }

        if (mScanListener != null) {
            mScanListener.onBluetoothScanFindDevice(device, rssi, isBle);
        }
    }

    public void setScanRemoveRepeat(boolean b) {
        mScanRemoveRepeat = b;
    }

    public boolean isScanRemoveRepeat() {
        return mScanRemoveRepeat;
    }
}
