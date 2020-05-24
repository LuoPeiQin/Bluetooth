package com.stag.bluetooth.util;

import android.util.Log;

/**
 * Created by Administrator on 2017/6/21.
 */

public class Logs {

    private static final boolean DEBUG = true;

    public static void d(String tag, String msg){
        if (DEBUG)
            Log.d(tag, msg);
    }

    public static void e(String tag, String msg){
        if (DEBUG)
            Log.e(tag, msg);
    }
}
