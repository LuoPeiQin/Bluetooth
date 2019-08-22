/*
 * Copyright (c) 2019. luopeiqin All rights reserved.
 */

package com.stag.bluetooth.helper;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import com.stag.bluetooth.extend.BleService;
import com.stag.bluetooth.extend.IBle;
import com.stag.bluetooth.protocol.Protocol;
import com.stag.bluetooth.util.ByteUtils;
import com.stag.bluetooth.util.LogUtils;

import java.util.UUID;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public final class BleHelper extends BluetoothHelper {

    private static final String TAG = "lpq";
    public final static UUID DEFAULT_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public final static String BLE_SCAN_FINISH = "BLE_SCAN_FINISH";
    private final static int SCAN_DURATION = 10000;
    private final static short MAX_WRITE_DESCRIPTOR_TIME = 3000;
    private final static int MAX_SINGLE_SZIE = 512;
    private final static int DEFAULT_SINGLE_SZIE = 20;
    private static BleHelper instance;
    private BluetoothGatt mGatt;
    private BluetoothGattCharacteristic mCRead;
    private BluetoothGattCharacteristic mCWrite;
    private Thread timeoutCountThread;  //超时计数
    private boolean isResumeTimeoutCount, isResumeScanCount;
    private ServiceConnection connBle;
    private BleService mBleService;
    private IBle mBle;
    private Thread scanCountThread;     //扫描计时
    private int mSingleSize = DEFAULT_SINGLE_SZIE;
    private boolean mSetHeightSpeedModeResult;
    private boolean mWriteDescriptorResult;
    private boolean sendResult;//是否发生成功

    public static BleHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (BleHelper.class) {
                instance = new BleHelper(context);
            }
        }
        return instance;
    }

    private BleHelper(Context context) {
        super(context);
    }

    @Override
    public void startScan() {
        if (mBle == null) {
            bindBleService();
        } else {
            mBle.startScan();
        }
    }

    @Override
    public void stopScan() {
        if (mBle != null) {
            mBle.stopScan();
        }
        stopScanCount();
    }

    @Override
    public void connect(final String address) {
        mMaxTime = 0;
        configHandler();
//        BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
//        BluetoothAdapter adapter = bluetoothManager.getAdapter();
        setConnecting(true);
        mDevice = mBluetoothAdapter.getRemoteDevice(address);
        mCWrite = null;
        mCRead = null;
        mSingleSize = DEFAULT_SINGLE_SZIE;
        mGatt = mDevice.connectGatt(mContext, false, mCallBack);
        if (mGatt == null) {
            LogUtils.d(TAG, "GATT NULL，FAIL");
            handleConnectEvent(isConnecting(), false);
        } else {
            LogUtils.d(TAG, "start connect... " + address);
            startTimeoutCount();
        }
    }

    @Override
    public void disconnect() {
        handleConnectEvent(false, false);
    }

    private void bindBleService() {
        connBle = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mBleService = ((BleService.LocalBinder) service).getService();
                mBle = mBleService.getBle();
                if (mBle != null && mBle.adapterEnabled()) {
                    mBle.startScan();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mBleService = null;
                mBle = null;
            }
        };
        Intent intent = new Intent(mContext, BleService.class);
        mContext.bindService(intent, connBle, Context.BIND_AUTO_CREATE);
    }

    /**
     * 开始扫描计时
     */
    private void startScanCount() {
        isResumeScanCount = true;
        scanCountThread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(SCAN_DURATION);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isResumeScanCount)
                    stopScan();
            }
        };
        scanCountThread.start();
    }

    /**
     * 停止扫描计时
     */
    private void stopScanCount() {
        if (scanCountThread != null) {
            isResumeScanCount = false;
            scanCountThread.interrupt();
            scanCountThread = null;
        }
        mContext.sendBroadcast(new Intent(BLE_SCAN_FINISH));
    }

    /**
     * 开始连接超时计数
     */
    private void startTimeoutCount() {
        isResumeTimeoutCount = true;
        timeoutCountThread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(CONNECT_TIMEOUT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LogUtils.d(TAG, "timeout:" + !isConnected + " isResumeTimeoutCount:" + isResumeTimeoutCount);
                if (!isConnected && isResumeTimeoutCount)
                    handleConnectEvent(isConnecting(), false);
            }
        };
        timeoutCountThread.start();
    }

    /**
     * 停止连接超时计数
     */
    private void stopTimeoutCount() {
        if (timeoutCountThread != null) {
            isResumeTimeoutCount = false;
            timeoutCountThread.interrupt();
            timeoutCountThread = null;
        }
    }

    @Override
    public void send(byte[] data) {
        super.send(data);
        if (mGatt == null || mCWrite == null || data == null || data.length == 0)
            return;
        /*int c = data.length/20;
        int remain = data.length%20;
        int count = remain==0?c:c+1;
        for (int i=0;i<count;i++){
            mCWrite.setValue(ByteUtils.subBytes(data, i*20, i==count-1?remain:20));
            for (int k=0;k<4;k++){
                if (mGatt.writeCharacteristic(mCWrite)){
                    LogUtils.d("lpq2", "成功 "+k);
                    try {
                        Thread.sleep(SEND_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                }else if (k==3){
                    LogUtils.d("lpq2", "失败 "+k);
                    return;
                }else {
                    LogUtils.d("lpq2", "失败 "+k);
                    try {
                        Thread.sleep(SEND_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }*/
        int sendInterval = mProtocol == null ? Protocol.BLE_MAX_SEND_INTERVAL : mProtocol.getMaxBleSendInterval();
        if (data.length <= mSingleSize) {
            mCWrite.setValue(data);
            LogUtils.d("lpq", "prepare small send");
            sendResult = false;
            boolean res = mGatt.writeCharacteristic(mCWrite);
            LogUtils.d("lpq", "after small writeCharacteristic:" + res + " " + ByteUtils.toString(data));

            if (sendResult) {
                LogUtils.d("lpq", "small abnormal");
                return;
            }
            ;

            synchronized (BleHelper.this) {
                try {
                    long i = System.currentTimeMillis();
                    wait(sendInterval);
                    long t = System.currentTimeMillis() - i;
                    if (t > mMaxTime) {
                        mMaxTime = t;
                        LogUtils.d("lpq", "max interval：" + mMaxTime);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            byte[] sendData = null;
            for (int i = 0; i <= data.length / mSingleSize; i++) {
                if (i == data.length / mSingleSize) {
                    sendData = new byte[data.length % mSingleSize];
                    if (sendData.length == 0)
                        return;
                    for (int k = 0; k < data.length % mSingleSize; k++) {
                        sendData[k] = data[i * mSingleSize + k];
                    }
                } else {
                    sendData = new byte[mSingleSize];
                    for (int k = 0; k < mSingleSize; k++) {
                        sendData[k] = data[i * mSingleSize + k];
                    }
                }
                if (!isConnected())
                    return;
                mCWrite.setValue(sendData);
                LogUtils.d("lpq", "prepare big send");
                sendResult = false;
                boolean res = mGatt.writeCharacteristic(mCWrite);
                LogUtils.d("lpq", "after big writeCharacteristic:" + sendData.length + " result:" + res + " " + ByteUtils.toString(sendData));

                if (sendResult) {
                    LogUtils.d("lpq", "big success");
                    continue;
                }
                ;

                synchronized (BleHelper.this) {
                    try {
                        long i2 = System.currentTimeMillis();
                        wait(sendInterval);
                        long t = System.currentTimeMillis() - i2;
                        if (t > mMaxTime) {
                            mMaxTime = t;
                            LogUtils.d("lpq", "max interval：" + mMaxTime);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    long mMaxTime;

    private BluetoothGattCallback mCallBack = (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) ? null : new BluetoothGattCallback() {
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            recv(characteristic.getValue());
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                LogUtils.d(TAG, "success，discoverServices");
                mGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                LogUtils.d(TAG, "fail，stop");
                stopTimeoutCount();
                handleConnectEvent(isConnecting, false);
            }
            LogUtils.d(TAG, "connect state---" + newState);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            stopTimeoutCount();
            if (status == BluetoothGatt.GATT_SUCCESS && mProtocol != null) {
                if (mProtocol.getServiceUUID() != null) {
                    BluetoothGattService service = mGatt.getService(mProtocol.getServiceUUID());
                    if (service != null) {
                        mCWrite = service.getCharacteristic(mProtocol.getSendTunnelUUID());
                        mCRead = service.getCharacteristic(mProtocol.getRecvTunnelUUID());
                    } else {
                        LogUtils.d("lpq", "service is null");
                    }
                }
                if (mCRead == null || mCWrite == null) {
                    for (BluetoothGattService s : mGatt.getServices()) {
                        if (mCWrite == null) {
                            mCWrite = s.getCharacteristic(mProtocol.getSendTunnelUUID());
                            if (mCWrite != null)
                                LogUtils.d("lpq", "write service:" + s.getUuid().toString());
                        }
                        if (mCRead == null) {
                            mCRead = s.getCharacteristic(mProtocol.getRecvTunnelUUID());
                            if (mCRead != null)
                                LogUtils.d("lpq", "read service:" + s.getUuid().toString());
                        }
                        if (mCWrite != null && mCRead != null) {
                            break;
                        }
                    }
                }
                if (mCRead != null && mCWrite != null) {
//                    if (mGatt.setCharacteristicNotification(mCRead, true)) {
                        /*BluetoothGattDescriptor descriptor = mCRead.getDescriptor(mProtocol.getDescriptorUUID() == null ? DEFAULT_DESCRIPTOR_UUID : mProtocol.getDescriptorUUID());
                        if (descriptor != null) {
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            mGatt.writeDescriptor(descriptor);
                        }*/

                        /*List<BluetoothGattDescriptor> descriptors = mCRead.getDescriptors();
                        for(BluetoothGattDescriptor dp:descriptors){
                            dp.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            mGatt.writeDescriptor(dp);
                        }*/
//                    }
                    boolean isSet = false;
                    byte[] value = null;
                    if (0 != (mCRead.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY)) { // 查看是否带有可通知属性notify
                        value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
                        isSet = true;
                    } else if (0 != (mCRead.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE)) {  // 查看是否带有indecation属性
                        value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE;
                        isSet = true;
                    }
                    if (isSet) {
                        UUID descriptorUUID = mProtocol.getDescriptorUUID() == null ? DEFAULT_DESCRIPTOR_UUID : mProtocol.getDescriptorUUID();
                        mGatt.setCharacteristicNotification(mCRead, true);
                        BluetoothGattDescriptor descriptor = mCRead.getDescriptor(descriptorUUID);
                        descriptor.setValue(value);
                        final boolean b = mGatt.writeDescriptor(descriptor);
                        //此处必须开线程，因为onDescriptorWrite是顺序回调执行的
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                LogUtils.d(TAG, "writeDescriptor " + b);
                                synchronized (mGatt) {
                                    try {
                                        mWriteDescriptorResult = false;
                                        mGatt.wait(MAX_WRITE_DESCRIPTOR_TIME);
                                        LogUtils.d(TAG, "write --- connect success" + isConnecting());
                                        handleConnectEvent(isConnecting(), true);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).start();
                    } else {
                        LogUtils.d(TAG, "no write --- connect success " + isConnecting());
                        handleConnectEvent(isConnecting(), true);
                    }
                    return;
                } else {
                    LogUtils.d(TAG, "read-write null");
                }
            } else {
                LogUtils.d(TAG, "onServicesDiscovered fail " + status);
            }
            handleConnectEvent(isConnecting(), false);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            //TODO:这个逻辑有点小问题
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mSingleSize = mtu - 3;
                mSetHeightSpeedModeResult = true;
                LogUtils.d("lpq", "MTU change success " + mtu);
            } else {
                mSetHeightSpeedModeResult = false;
                LogUtils.d("lpq", "MTU change fail " + mtu);
            }
            synchronized (mGatt) {
                mGatt.notify();
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            synchronized (BleHelper.this) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    sendResult = true;
                    LogUtils.d("lpq", "send success " + ByteUtils.toString(characteristic.getValue()));
                } else {
                    sendResult = false;
                    LogUtils.d("lpq", "sen fail，status:" + status + "   " + ByteUtils.toString(characteristic.getValue()));
                }
                BleHelper.this.notify();
                LogUtils.d("lpq", "send complete, notify end");
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            mWriteDescriptorResult = status == BluetoothGatt.GATT_SUCCESS;
            LogUtils.d("lpq", "Write result: " + mWriteDescriptorResult);
            synchronized (mGatt) {
                mGatt.notify();
            }
        }
    };

    public boolean isHighSpeedMode() {
        return mSingleSize > DEFAULT_SINGLE_SZIE;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean setHighConnectionPriority(boolean flag) {
        if (mGatt == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return false;
        return mGatt.requestConnectionPriority(flag ? BluetoothGatt.CONNECTION_PRIORITY_HIGH : BluetoothGatt.CONNECTION_PRIORITY_BALANCED);
    }

    public boolean setHighSpeedMode(boolean flag) {
        mSetHeightSpeedModeResult = false;
        if (mGatt == null)
            return false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return false;
        int mtuSize = 3;
        if (flag) {
            mtuSize += MAX_SINGLE_SZIE;
        } else {
            mtuSize += DEFAULT_SINGLE_SZIE;
        }

        synchronized (mGatt) {
            boolean res = mGatt.requestMtu(mtuSize);
            LogUtils.d("lpq", "request Max Mtu " + (mtuSize) + " result:" + res);
            if (!res)
                return false;

            try {
                mGatt.wait(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return mSetHeightSpeedModeResult;
    }

    /**
     * 处理设备连接事件，所有BLE连接状态事件最终处理的地方
     *
     * @param tryConnect   是否为尝试连接事件
     * @param connectState 连接状态，连接或断开
     */
    private synchronized void handleConnectEvent(boolean tryConnect, boolean connectState) {
        if (tryConnect) {
            setConnecting(false);
            if (connectState) {
                setConnected(true);
            } else {
                //连接失败
                setConnected(false);
                if (mGatt != null) {
                    mGatt.disconnect();
                    mGatt.close();
                    mGatt = null;
                }
            }
            sendConnectResultMessage(connectState);
        } else {
            //这种情况一般connectState只可能为false,即蓝牙已经成功连接后断开的情况
            if (mGatt != null) {
//                    mGatt.disconnect();
                mGatt.close();
                mGatt = null;
            }
            if (isConnected()) {
                setConnected(false);
                sendDisconnectResultMessage();
            }
        }
    }
}
