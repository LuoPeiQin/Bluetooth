package com.stag.bluetooth.helper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.stag.bluetooth.BluetoothTransfer;
import com.stag.bluetooth.OnBluetoothConnectStateChangeListener;
import com.stag.bluetooth.OnBluetoothTransmitListener;
import com.stag.bluetooth.protocol.Protocol;

/**
 * Created by Administrator on 2016/11/14.
 */

public abstract class BluetoothHelper {

    protected static final int CONNECT_TIMEOUT = 10000;    //连接超时时间
    private static final int HANDLER_CONNECT_EVENT = 1;
    private static final int HANDLER_DISCONNECT_EVENT = 2;
    protected Context mContext;
    protected boolean isConnected;
    protected boolean isConnecting; //是否为尝试连接，用于判断连接断开的情况：连接失败或者已经连接成功后断开
    protected BluetoothAdapter mBluetoothAdapter;
    protected BluetoothDevice mDevice;
    protected OnBluetoothConnectStateChangeListener listener;
    protected OnBluetoothTransmitListener transmitListener;//蓝牙数据收发监听
    protected Protocol mProtocol;
    private Handler handler;
    protected BluetoothHelper(Context context){
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public abstract void startScan();

    public abstract void stopScan();

    /**
     * BLE和传统都为改为异步进行
     * */
    public abstract void connect(String address);

    public abstract void disconnect();

    public void send(byte[] data){
        if (transmitListener!=null)
            transmitListener.onBluetoothSendData(data);
    }

    /**
     * 接收数据，不可主动调用
     * */
    protected void recv(byte[] data){
        if (data==null||data.length==0)
            return;
        if (transmitListener!=null)
            transmitListener.onBluetoothRecvData(data);
        BluetoothTransfer.getInstance().addRecvData(data);
    }

    protected void configHandler(){
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case HANDLER_CONNECT_EVENT:
                        /*final boolean isSuccess = (Boolean) msg.obj;
                        if (isSuccess){
                            postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    connectCallback(isSuccess);
                                }
                            }, 10);
                        }else {
                            connectCallback(isSuccess);
                        }*/
                        connectCallback((Boolean) msg.obj);
                        break;
                    case HANDLER_DISCONNECT_EVENT:
                        disconnectCallback();
                        break;
                }
            }
        };
    }

    protected void sendConnectResultMessage(boolean isSuccess){
        Message.obtain(handler, HANDLER_CONNECT_EVENT, isSuccess).sendToTarget();
    }

    protected void sendDisconnectResultMessage(){
        Message.obtain(handler, HANDLER_DISCONNECT_EVENT).sendToTarget();
    }

    /**
     * 连接回调
     * @param isSuccess 是否成功
     * */
    private void connectCallback(boolean isSuccess){
        if (listener!=null)
            listener.onBluetoothConnect(mDevice, isSuccess);
    }

    /**
     * 断开回调
     * */
    private void disconnectCallback(){
        if (listener!=null)
            listener.onBluetoothDisconnect(mDevice);
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public boolean isConnecting() {
        return isConnecting;
    }

    public void setConnecting(boolean connecting) {
        isConnecting = connecting;
    }

    public OnBluetoothConnectStateChangeListener getConnectStateChangeListener() {
        return listener;
    }

    public void setConnectStateChangeListener(OnBluetoothConnectStateChangeListener listener) {
        this.listener = listener;
    }

    public OnBluetoothTransmitListener getTransmitListener() {
        return transmitListener;
    }

    public void setTransmitListener(OnBluetoothTransmitListener transmitListener) {
        this.transmitListener = transmitListener;
    }

    public void setProtocol(Protocol protocol){
        mProtocol = protocol;
    }
}
