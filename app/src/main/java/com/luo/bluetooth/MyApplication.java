package com.luo.bluetooth;

import android.app.Application;

import com.tencent.bugly.Bugly;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        Bugly.init(getApplicationContext(), "4efc7ff5d2", false);
    }

}
