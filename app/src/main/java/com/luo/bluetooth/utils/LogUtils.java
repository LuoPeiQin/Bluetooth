package com.luo.bluetooth.utils;

import android.util.Log;

import com.tencent.bugly.crashreport.BuglyLog;
import com.luo.bluetooth.BuildConfig;

public class LogUtils {

    private static boolean isDebug = BuildConfig.DEBUG;

    private LogUtils() {
    }

    public static void v(String tag, String msg) {
        BuglyLog.v(tag, msg);
        if (isDebug) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        BuglyLog.d(tag, msg);
        if (isDebug) {
            //信息太长,分段打印
            //因为String的length是字符数量不是字节数量所以为了防止中文字符过多，
            //  把4*1024的MAX字节打印长度改为2001字符数
            int max_str_length = 2001 - tag.length();
            //大于4000时
            while (msg.length() > max_str_length) {
                Log.d(tag, msg.substring(0, max_str_length));
                msg = msg.substring(max_str_length);
            }
            //剩余部分
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        BuglyLog.i(tag, msg);
        if (isDebug) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        BuglyLog.w(tag, msg);
        if (isDebug) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        BuglyLog.e(tag, msg);
        if (isDebug) {
            Log.e(tag, msg);
        }
    }

}
