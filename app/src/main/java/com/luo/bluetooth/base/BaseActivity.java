package com.luo.bluetooth.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public class BaseActivity extends AppCompatActivity {

    //全局变量
    protected static boolean isConnecting = false;
    protected static boolean isLockConnecting = false;

    public static WeakReference<Context> currentResumeContext;
    protected Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    }


    @Override
    protected void onResume() {
        currentResumeContext = new WeakReference<Context>(this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void startToActivity(Class activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

}
