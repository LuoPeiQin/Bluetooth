/*
 * Copyright (c) 2019. luopeiqin All rights reserved.
 */

package com.stag.bluetooth;

import com.stag.bluetooth.protocol.Packet;

/**
 * 蓝牙发送任务
 */

public final class BluetoothTask<T extends Packet> {

    private static int DEFAULT_TRY_COUNT = 2;
    private static int DEFAULT_TIMEOUT = 3000;      //默认超时时间，毫秒级
    private static boolean DEFAULT_MAIN_THREAD = false;

    private int tryCount;                           //尝试发送次数
    private long timeout;                            //每次重发的超时时间，毫秒级
    private long sentTime;                          //至少在应用层真正被发送出去的时间
    private T packet;                          //数据包
    //    private int priority;                           //优先级，暂时业务不需要
    private boolean resultCallbackInMainThread;  //结果回调是否需要执行在主线程上
    private OnResultListener onResult;              //响应回调

    public <E extends T> BluetoothTask(E packet) {
        this(DEFAULT_TRY_COUNT, DEFAULT_TIMEOUT, packet, DEFAULT_MAIN_THREAD, null);
    }

    public <E extends T> BluetoothTask(E packet, OnResultListener<E> onResult) {
        this(DEFAULT_TRY_COUNT, DEFAULT_TIMEOUT, packet, DEFAULT_MAIN_THREAD, onResult);
    }

    public <E extends T>BluetoothTask(E packet, boolean resultCallbackInMainThread, OnResultListener<E> onResult) {
        this(DEFAULT_TRY_COUNT, DEFAULT_TIMEOUT, packet, resultCallbackInMainThread, onResult);
    }

    public <E extends T> BluetoothTask(int tryCount, int timeout, E packet, OnResultListener<E> onResult) {
        this(tryCount, timeout, packet, DEFAULT_MAIN_THREAD, onResult);
    }

    public <E extends T> BluetoothTask(int tryCount, int timeout, E packet, boolean resultCallbackInMainThread, OnResultListener<E> onResult) {
        this.tryCount = tryCount;
        setTimeout(timeout);
        this.packet = packet;
        this.resultCallbackInMainThread = resultCallbackInMainThread;
        this.onResult = onResult;
    }

    public boolean isTimeout() {
        long t = (System.nanoTime() - sentTime);
        return t > timeout && sentTime != 0;
    }

    /**
     * 异步发送
     * @return
     */
    public void send() {
        BluetoothTransfer.getInstance().addSendTask(this);
    }

    /**
     * 阻塞发送，返回数据部分的字节数组
     * @return
     */
    public byte[] sendBySync2() {
        if (BluetoothTransfer.getInstance().isStop())
            return null;
        final byte[][] res = new byte[1][];
        synchronized (res) {
            setOnResult(new OnDataResultListener() {
                @Override
                public void onResult(boolean isTimeout, byte[] data) {
                    synchronized (res) {
                        res[0] = data;
                        res.notify();
                    }
                }
            });
            send();
            try {
                res.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return res[0];
    }

    /**
     * 阻塞发送，返回完整的包
     * @return
     */
    public T sendBySync() {
        if (BluetoothTransfer.getInstance().isStop())
            return null;
        final T[] res = (T[]) new Packet[1];
        synchronized (this) {
            setOnResult(new OnResultListener<T>() {
                @Override
                public void onResult(boolean isTimeout, T packet) {
                    synchronized (res) {
                        res[0] = packet;
                        res.notify();
                    }
                }
            });
            send();
            try {
                res.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return res[0];
    }

    public int getTryCount() {
        return tryCount;
    }

    public BluetoothTask setTryCount(int tryCount) {
        this.tryCount = tryCount;
        return this;
    }

    public long getTimeout() {
        return timeout / 1000000;
    }

    public BluetoothTask setTimeout(long timeout) {
        this.timeout = timeout * 1000000;
        return this;
    }

    public long getSentTime() {
        return sentTime;
    }

    BluetoothTask setSentTime(long sendTime) {
        this.sentTime = sendTime;
        return this;
    }

    public Packet getPacket() {
        return packet;
    }

    public BluetoothTask setPacket(T packet) {
        this.packet = packet;
        return this;
    }

    public boolean isResultCallbackInMainThread() {
        return resultCallbackInMainThread;
    }

    public BluetoothTask setResultCallbackInMainThread(boolean resultCallbackInMainThread) {
        this.resultCallbackInMainThread = resultCallbackInMainThread;
        return this;
    }

    public OnResultListener getOnResult() {
        return onResult;
    }

    public BluetoothTask setOnResult(OnResultListener onResult) {
        this.onResult = onResult;
        return this;
    }

    /**
     * 只获取数据部分
     */
    public abstract static class OnDataResultListener implements OnResultListener<Packet> {

        @Override
        public void onResult(boolean isTimeout, Packet packet) {
            onResult(isTimeout, packet == null ? null : packet.getData());
        }
        
        public abstract void onResult(boolean isTimeout, byte[] data);
    }

    /**
     * 返回整个回应包
     */
    public interface OnResultListener<T extends Packet> {
        void onResult(boolean isTimeout, T packet);
    }
}
