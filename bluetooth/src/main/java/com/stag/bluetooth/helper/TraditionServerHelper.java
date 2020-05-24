package com.stag.bluetooth.helper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.stag.bluetooth.util.ByteUtils;
import com.stag.bluetooth.util.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class TraditionServerHelper extends BluetoothHelper {
    private static final String TAG = "TraditionServerHelp-";
    private static final String NAME = "XGD N6";
    private final static UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // SPP服务UUID号
    private static TraditionServerHelper instance;

    public static TraditionServerHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (TraditionServerHelper.class) {
                instance = new TraditionServerHelper(context);
            }
        }
        return instance;
    }

    private TraditionServerHelper(Context context) {
        super(context);
        acceptThread = new AcceptThread();
    }

    @Override
    public void startScan() {
        LogUtils.d(TAG + "lpq", "startScan: 不可调用");
        if (acceptThread == null) {
            acceptThread = new AcceptThread();
        }
        acceptThread.start();
        configHandler();
    }

    @Override
    public void stopScan() {
        LogUtils.d(TAG + "lpq", "stopScan: 不可调用");
        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }
    }

    @Override
    public void connect(String address) {
        LogUtils.d(TAG + "lpq", "connect: 不可调用");
    }

    @Override
    public void disconnect() {
        LogUtils.d(TAG + "lpq", "disconnect: 不可调用");
        if (connectedThread != null) {

            connectedThread.cancel();
        }
    }

    private AcceptThread acceptThread;

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord(NAME, SPP_UUID);
            } catch (IOException e) {
                Log.e(TAG + "lpq", "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    setConnecting(true);
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG + "lpq", "等待连接时发生异常", e);
                    break;
                }
                setConnecting(false);

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
//                    manageMyConnectedSocket(socket);
                    connectedThread = new ConnectedThread(socket);
                    connectedThread.start();
                    setConnected(true);
                    sendConnectResultMessage(true);
                    Log.d(TAG + "lpq", "run: 蓝牙连接成功");
                    try {
                        mmServerSocket.close();
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG + "lpq", "Could not close the connect socket", e);
            }
        }
    }

    private ConnectedThread connectedThread;

    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG + "lpq", "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG + "lpq", "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer, 0, 1024);
                    Log.d(TAG + "lpq", "run: 收到数据: " + numBytes);
                    if (numBytes > 0) {
                        recv(ByteUtils.subBytes(mmBuffer, 0, numBytes));
                    }
//                    Message readMsg = handler.obtainMessage(
//                            MessageConstants.MESSAGE_READ, numBytes, -1,
//                            mmBuffer);
//                    readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.d(TAG + "lpq", "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
            } catch (IOException e) {
                Log.e(TAG + "lpq", "Error occurred when sending data", e);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
                sendDisconnectResultMessage();
            } catch (IOException e) {
                Log.e(TAG + "lpq", "Could not close the connect socket", e);
            }
        }
    }

    @Override
    public void send(byte[] data) {
        super.send(data);
        if (data == null || data.length == 0)
            return;
        if (connectedThread != null && connectedThread.mmOutStream != null) {
            try {
                connectedThread.mmOutStream.write(data, 0, data.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
