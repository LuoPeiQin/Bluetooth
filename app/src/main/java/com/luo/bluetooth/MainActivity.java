package com.luo.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.luo.bluetooth.customview.searchble.BluetoothDeviceBean;
import com.luo.bluetooth.customview.searchble.DeviceListActivity;
import com.luo.bluetooth.protocol.CustomPacket;
import com.luo.bluetooth.protocol.CustomProtocol;
import com.luo.bluetooth.utils.ByteUtils;
import com.luo.bluetooth.utils.DialogUtils;
import com.luo.bluetooth.utils.GpsUtils;
import com.luo.bluetooth.utils.LogUtils;
import com.luo.bluetooth.utils.ToastUtil;
import com.stag.bluetooth.BluetoothController;
import com.stag.bluetooth.OnBluetoothConnectStateChangeListener;
import com.stag.bluetooth.OnBluetoothStateChangeListener;

import java.nio.charset.StandardCharsets;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity implements OnBluetoothStateChangeListener, OnBluetoothConnectStateChangeListener {
    private static final String TAG = "MainActivity";
    private static final int QUEST_BLUETOOTH_DEVICE = 1;

    private TextView appVersion;
    private TextView bleStatus;
    private TextView bleConnectStatus;
    // 蓝牙相关
    public static BluetoothDeviceBean mDeviceModel;
    public BluetoothController mBluetoothController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initBluetooth();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (mBluetoothController != null) {
            mBluetoothController.setProtocol(null);
            mBluetoothController.unregisterBluetoothStateChangeListener();
            mBluetoothController.unregisterConnectStateChangeListener();
            mBluetoothController.disconnect();
            mBluetoothController.stopScan();
        }
        DialogUtils.dismissDialog();
        super.onDestroy();
    }

    private void initView() {
        appVersion = findViewById(R.id.app_version);
        bleStatus = findViewById(R.id.ble_status);
        bleConnectStatus = findViewById(R.id.ble_connect_status);
        appVersion.setText("" + BuildConfig.VERSION_NAME + " - " + BuildConfig.VERSION_CODE);
    }

    /**
     * 初始化蓝牙框架
     */
    private void initBluetooth() {
        mBluetoothController = BluetoothController.getController(this);
        mBluetoothController.registerBluetoothStateChangeListener(this);
        mBluetoothController.registerConnectStateChangeListener(this);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            LogUtils.d(TAG + "lpq", "initBluetooth: 该设备不支持蓝牙");
            ToastUtil.showShort(this, "该设备不支持蓝牙！无法正常使用");
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            bleStatus.setText("已关闭");
            bluetoothAdapter.enable();
        } else {
            bleStatus.setText("已打开");
        }
        /**
         * 可以选择作为客户端搜索传统蓝牙，低功耗蓝牙
         * 或者作为传统蓝牙服务端
         */
        mBluetoothController.setBluetoothType(BluetoothController.TYPE_BLE);
    }

    /**
     * 搜索和连接蓝牙
     * @param view
     */
    public void btnSearchBle(View view) {
        if (!GpsUtils.isOPen(this)) {
            DialogUtils.showNormalDialog(this, "提示", "为保证初始化功能的正常使用，请开启定位功能", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    DialogUtils.dismissDialog();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            return;
        }
        if (mBluetoothController.isConnected()) {
            mBluetoothController.disconnect();
        } else {
            Intent deviceIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(deviceIntent, QUEST_BLUETOOTH_DEVICE);
        }
    }

    /**
     * 使用mac地址连接蓝牙
     * @param view
     */
    public void btnMacConnect(View view) {
        mBluetoothController.connect("40:C8:1F:6A:CD:2B", new CustomProtocol(this, null));
    }

    /**
     * 断开蓝牙
     */
    public void btnDisconnectBle(View view) {
        LogUtils.d(TAG + "lpq", "btnDisconnectBle: ");
        if (mBluetoothController.isConnected()) {
            mBluetoothController.disconnect();
        } else {
            mBluetoothController.stopScan();
        }
    }

    /**
     * 蓝牙搜索结果
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        mDeviceModel = (BluetoothDeviceBean) data.getSerializableExtra(DeviceListActivity.KEY_SCAN_DEVICE);
        LogUtils.d(TAG + "lpq", "onActivityResult: " + mDeviceModel.toString());
        String deviceName = mDeviceModel.getDeviceName();
        if (deviceName != null) {
            if (mDeviceModel.isBle()) {
                mBluetoothController.setBluetoothType(BluetoothController.TYPE_BLE);
            } else {
                mBluetoothController.setBluetoothType(BluetoothController.TYPE_TRADITION);
            }
            mBluetoothController.setProtocol(new CustomProtocol(this, null));
            mBluetoothController.connect(mDeviceModel.getDeviceAddress());
        } else {
            ToastUtil.showShort(this, "蓝牙名称为空，请重试");
        }
    }

    /********************************* 蓝牙监听回调 *************************************/

    @Override
    public void onBluetoothOpen() {
        LogUtils.d(TAG + "lpq", "onBluetoothOpen: 蓝牙已打开");
        bleStatus.setText("已打开");
    }

    @Override
    public void onBluetoothClose() {
        LogUtils.d(TAG + "lpq", "onBluetoothClose: 蓝牙已关闭");
        bleStatus.setText("已关闭");
    }

    /**
     * 蓝牙连接事件回调
     * @param device 蓝牙设备
     * @param isSuccess 是否成功
     */
    @Override
    public void onBluetoothConnect(BluetoothDevice device, boolean isSuccess) {
        LogUtils.d(TAG + "lpq", "onBluetoothConnect: 蓝牙连接结果：" + isSuccess);
        if (isSuccess) {
            bleConnectStatus.setText("已连接");
            mBluetoothController.setProtocol(new CustomProtocol(this, null));
        } else {
            bleConnectStatus.setText("未连接");
        }
    }

    /**
     * 蓝牙断开连接事件回调
     * @param device 蓝牙设备
     */
    @Override
    public void onBluetoothDisconnect(BluetoothDevice device) {
        bleConnectStatus.setText("未连接");
    }

    /**
     * 协议解析测试
     * @param view
     */
    public void btnProtocolParse(View view) {
        LogUtils.d(TAG + "lpq", "btnProtocolParse: 蓝牙协议解析测试");
        CustomProtocol customProtocol = new CustomProtocol(this, null);
        String a1String = "02413130313139323032303";
        byte[] bytes = ByteUtils.hexStringToBytes(a1String);
        LogUtils.d(TAG + "lpq", "btnProtocolParse: bytes = " + ByteUtils.toString(bytes, ""));
        customProtocol.parse(bytes);
    }

    /**
     * 协议打包测试
     * @param view
     */
    public void btnProtocolPacket(View view) {
        LogUtils.d(TAG + "lpq", "btnProtocolParse: 蓝牙协议解析测试");
        CustomProtocol customProtocol = new CustomProtocol(this, null);
        byte[] sendBytes = "00".getBytes(StandardCharsets.US_ASCII);
        LogUtils.d(TAG + "lpq", "confirm: sendBytes = " + ByteUtils.toString(sendBytes));
        new CustomProtocol(this, null).packetToBytes(new CustomPacket(0x01 /* 命令码 */, sendBytes));
    }

    /**
     * 发送简单数据
     * 这个不会走封装好的协议
     * @param view
     */
    public void btnSendConfirmData(View view) {
        byte[] bytes = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06};
        mBluetoothController.sendData(bytes);
    }

}
