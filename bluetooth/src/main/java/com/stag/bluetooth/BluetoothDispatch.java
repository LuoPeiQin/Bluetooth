package com.stag.bluetooth;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 派遣所有回调的事件，根据Task分发到主线程或者子线程，建立线程池分发执行
 * Created by LPQ on 2016/11/18.
 */

public final class BluetoothDispatch {

    private static BluetoothDispatch instance;
    private ExecutorService mCatchThreadPool;
    private Handler mMainHandler;

    public static BluetoothDispatch getInstance(){
        if (instance==null){
            synchronized (BluetoothDispatch.class){
                if (instance==null)
                    instance = new BluetoothDispatch();
            }
        }
        return instance;
    }

    public void start(){
        if (mCatchThreadPool ==null){
            mCatchThreadPool = Executors.newCachedThreadPool();
        }
        mMainHandler = new Handler(Looper.getMainLooper());
    }

    public void stop(){
        if (mCatchThreadPool !=null){
            mCatchThreadPool.shutdownNow();
            mCatchThreadPool = null;
        }
        mMainHandler = null;
    }

    public void dispatch(final Callback callback, boolean runInMainThread){
        if (callback==null) return;

        if (runInMainThread){
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.callback();
                }
            });
        }else {
            if (mCatchThreadPool !=null){
                mCatchThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.callback();
                    }
                });
            }
        }
    }

    /**
     * 以防扩展
     * */
    public interface Callback{
        void callback();
    }
}
