package com.stag.bluetooth;

import com.stag.bluetooth.protocol.ParseResult;
import com.stag.bluetooth.protocol.Protocol;
import com.stag.bluetooth.util.ByteUtils;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * 蓝牙传输控制类
 * 目前不支持流水号，如果同一条指令并发发送，采取FIFO的形式
 * Created by LPQ on 2016/11/14.
 */

public final class BluetoothTransfer {

    private static final short RECV_BUFFER_SIZE = 1024;       //接收缓冲区大小
    private static final short TIMEOUT_CHECK_ACCURACY = 100; //超时检查的精确度，即超时时间的误差范围，毫秒级
    public static BluetoothTransfer instance;
    private Protocol mProtocol;                             //所使用协议
    private SendMethod send;                                //发送蓝牙数据的方法，发送时必须设置好Task的发送时间
    private LinkedList<BluetoothTask> sendList;             //发送任务执行队列，暂时先自行管理同步
    private LinkedList<BluetoothTask> waitRespondList;      //待响应队列，暂时先自行管理同步
    private ByteBuffer recvBuf;                               //接收缓冲区
    private Thread sendThread;                              //负责发送
    private Thread recvHandlerThread;                       //负责处理接收到的数据
    private Thread timeoutCheckThread;
    private boolean isResume;
    private Object recvLock, sendLock, waitRespondLock;
    private BluetoothDispatch dispatch;

    public static BluetoothTransfer getInstance(){
        if (instance==null){
            synchronized (BluetoothTransfer.class){
                if (instance==null)
                    instance = new BluetoothTransfer();
            }
        }
        return instance;
    }

    private BluetoothTransfer(){
        sendList = new LinkedList<>();
        waitRespondList = new LinkedList<>();
        recvBuf = ByteBuffer.allocate(RECV_BUFFER_SIZE);
        recvLock = new Object();
        sendLock = new Object();
        waitRespondLock = new Object();
        dispatch = BluetoothDispatch.getInstance();
    }

    /**
     * 启动蓝牙传输
     * */
    public void start(){
        stop();
        dispatch.start();
        isResume = true;
        startSendQueue();
        startTimeoutCheck();
        startRecvHandlerQueue();
    }

    /**
     * 停止蓝牙传输
     * */
    public void stop(){
        dispatch.stop();
        isResume = false;

        stopSendQueue();
        stopRecvHandlerQueue();
        stopTimeoutCheck();

        synchronized (sendLock){
            for (BluetoothTask task:sendList){
                if (task.getOnResult()!=null)
                    task.getOnResult().onResult(true, null);
            }
            sendList.clear();
        }
        synchronized (waitRespondLock){
            for (BluetoothTask task:waitRespondList){
                if (task.getOnResult()!=null)
                    task.getOnResult().onResult(true, null);
            }
            waitRespondList.clear();
        }
        synchronized (recvLock){
            recvBuf.clear();
        }
    }

    /**
     * 蓝牙发送数据
     * */
    private void startSendQueue(){
        sendThread = new Thread(){
            @Override
            public void run() {
                super.run();
                while (isResume){
                    BluetoothTask task;
                    synchronized (sendLock){
                        while (sendList.isEmpty()){
                            try {
                                sendLock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (!isResume)
                                return;
                            /*检查和处理超时的已发送的待响应任务*/
//                            checkTimeoutTask();
                        }
                        /*发送数据，把task放入待响应队列*/
                        task = sendList.removeFirst();
                    }
                    if (task.getOnResult()!=null){
                        synchronized (waitRespondLock){
                            waitRespondList.add(task);
                        }
                    }
                    send.send(mProtocol.packetToBytes(task.getPacket()));
                    task.setSentTime(System.nanoTime());
                }
            }
        };
        sendThread.start();
    }

    /**
     * 蓝牙收到数据后的处理
     * */
    private void startRecvHandlerQueue(){
        recvHandlerThread = new Thread(){
            @Override
            public void run() {
                super.run();
                while (isResume){
                    synchronized (recvLock){
                        while (recvBuf.position()==0){
                            try {
                                recvLock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (!isResume)
                                return;
                        }
                        final ParseResult result = mProtocol.parse(ByteUtils.getBytesWithBuffer(recvBuf));
                        recvBuf.clear();
                        switch (result.getType()){
                            case RESPOND://响应事件
                                synchronized (waitRespondLock){
                                    Iterator<BluetoothTask> iterator = waitRespondList.iterator();
                                    while (iterator.hasNext()){
                                        final BluetoothTask task = iterator.next();
                                        if (task.getPacket().match(result.getPacket())){
                                            dispatch.dispatch(new BluetoothDispatch.Callback() {
                                                @Override
                                                public void callback() {
                                                    if (task.getOnResult()!=null)
                                                        task.getOnResult().onResult(false, result.getPacket());
                                                }
                                            }, task.isResultCallbackInMainThread());
                                            iterator.remove();
                                            break;
                                        }
                                    }
                                }
                                break;
                            case ACTIVE://主动事件
                                dispatch.dispatch(result.getCallback(), false);
                                break;
                            case INCOMPLETE://非完整一帧
                                //好像暂时没啥事干，多余的数据在对应协议里会保存以备下次解析
                                break;
                            default:break;
                        }
                    }
                }
            }
        };
        recvHandlerThread.start();
    }

    private void startTimeoutCheck(){
        timeoutCheckThread = new Thread(){
            @Override
            public void run() {
                super.run();
                while (isResume){
                    try {
                        Thread.sleep(TIMEOUT_CHECK_ACCURACY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!isResume)
                        return;

                    synchronized (waitRespondLock){
                        Iterator<BluetoothTask> iterator = waitRespondList.iterator();
                        while (iterator.hasNext()){
                            final BluetoothTask task = iterator.next();
                            if (task.isTimeout()){
                                if (task.getTryCount()>1){
                                    task.setTryCount(task.getTryCount()-1);
                                    addSendTask(task);
                                }else {
                                    BluetoothDispatch.getInstance().dispatch(new BluetoothDispatch.Callback() {
                                        @Override
                                        public void callback() {
                                            task.getOnResult().onResult(true, null);
                                        }
                                    }, task.isResultCallbackInMainThread());
                                }
                                iterator.remove();
                            }else {
//                                Logs.d("LPQ", "没有timeout");
                            }
                        }
                    }
                }
            }
        };
        timeoutCheckThread.start();
    }

    private void stopSendQueue(){
        if (sendThread!=null){
            sendThread.interrupt();
            sendThread = null;
        }
    }

    private void stopRecvHandlerQueue(){
        if (recvHandlerThread !=null){
            recvHandlerThread.interrupt();
            recvHandlerThread = null;
        }
    }

    private void stopTimeoutCheck(){
        if (timeoutCheckThread !=null){
            timeoutCheckThread.interrupt();
            timeoutCheckThread = null;
        }
    }

    public boolean addSendTask(BluetoothTask task){
        if (isResume){
            synchronized (sendLock){
                task.setSentTime(0);
                sendList.add(task);
                sendLock.notify();
            }
        }else {
            if (task.getOnResult()!=null)
                task.getOnResult().onResult(true, null);
        }
        return isResume;
    }

    public void addRecvData(byte[] data){
        synchronized (recvLock){
            recvBuf.put(data);
            recvLock.notify();
        }
    }

    public boolean isStop(){
        return !isResume;
    }

    /**
     * 检查和处理超时的发送任务
     * */
    private void checkTimeoutTask(){
        synchronized (waitRespondLock){
            Iterator<BluetoothTask> iterator = waitRespondList.iterator();
            while (iterator.hasNext()){
                final BluetoothTask task = iterator.next();
                if (task.isTimeout()){
                    if (task.getTryCount()>1){
                        task.setTryCount(task.getTryCount()-1);
                        sendList.add(task);
                    }else {
                        BluetoothDispatch.getInstance().dispatch(new BluetoothDispatch.Callback() {
                            @Override
                            public void callback() {
                                task.getOnResult().onResult(true, null);
                            }
                        }, task.isResultCallbackInMainThread());
                    }
                    iterator.remove();
                }
            }
        }
    }

    public Protocol getProtocol() {
        return mProtocol;
    }

    public void setProtocol(Protocol mProtocol) {
        this.mProtocol = mProtocol;
    }

    public SendMethod getSend() {
        return send;
    }

    public void setSend(SendMethod send) {
        this.send = send;
    }

    public interface SendMethod{
        void send(byte[] data);
    }
}