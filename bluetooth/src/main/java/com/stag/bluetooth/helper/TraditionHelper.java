/*
 * Copyright (c) 2019. luopeiqin All rights reserved.
 */

package com.stag.bluetooth.helper;

import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.stag.bluetooth.util.ByteUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public final class TraditionHelper extends BluetoothHelper {

    private final static UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // SPP服务UUID号
    private static TraditionHelper instance;
    private BluetoothSocket bthSocket;
    private InputStream is;
    private OutputStream os;

    public static TraditionHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (TraditionHelper.class) {
                instance = new TraditionHelper(context);
            }
        }
        return instance;
    }

    private TraditionHelper(Context context) {
        super(context);
    }

    @Override
    public void startScan() {
        mBluetoothAdapter.startDiscovery();
    }

    @Override
    public void stopScan() {
        mBluetoothAdapter.cancelDiscovery();
    }

    @Override
    public void connect(final String address) {
        setConnecting(true);
        configHandler();
        new Thread() {
            @Override
            public void run() {
                super.run();
                mDevice = mBluetoothAdapter.getRemoteDevice(address);
                try {
                    bthSocket = mDevice.createRfcommSocketToServiceRecord(SPP_UUID);
                    bthSocket.connect();
                    is = bthSocket.getInputStream();
                    os = bthSocket.getOutputStream();
                } catch (IOException e) {
                    try {
                        bthSocket = (BluetoothSocket) mDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(mDevice, 1);
                        bthSocket.connect();
                        is = bthSocket.getInputStream();
                        os = bthSocket.getOutputStream();
                    } catch (IllegalAccessException e1) {
                        e1.printStackTrace();
                        sendConnectResultMessage(false);
                        return;
                    } catch (InvocationTargetException e1) {
                        e1.printStackTrace();
                        sendConnectResultMessage(false);
                        return;
                    } catch (NoSuchMethodException e1) {
                        e1.printStackTrace();
                        sendConnectResultMessage(false);
                        return;
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        sendConnectResultMessage(false);
                        return;
                    } finally {
                        setConnecting(false);
                    }
                }
                setConnected(true);
                new Thread(readThread).start();
                sendConnectResultMessage(true);
            }
        }.start();
    }

    @Override
    public void disconnect() {
        if (isConnected()) {
            setConnected(false);
            if (bthSocket != null) {
                try {
                    is.close();
                    os.close();
                    bthSocket.close();
                    bthSocket = null;
                    is = null;
                    os = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            sendDisconnectResultMessage();
        }
    }

    private Runnable readThread = new Runnable() {
        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int num;
            while (isConnected()) {
                try {
//                    if (is.available() > 0) {
                    num = is.read(buffer, 0, 1024);
                    if (num > 0) {
                        recv(ByteUtils.subBytes(buffer, 0, num));
                    }
//                    } else {
//                        Thread.sleep(1);
//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void send(byte[] data) {
        super.send(data);
        if (data == null || data.length == 0)
            return;
        if (os != null) {
            try {
                os.write(data, 0, data.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
