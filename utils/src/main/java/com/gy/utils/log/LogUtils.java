package com.gy.utils.log;

import android.util.Log;

/**
 * Created by ganyu on 2016/5/23.
 *
 */
public class LogUtils {
    public static boolean isDebug = true;

    public static void d (String tag, String log) {
        if (isDebug) {
            Log.d(tag, log);
        }
    }

    public static void e (String tag, String log) {
        if (isDebug) {
            Log.e(tag, log);
        }
    }

    public static void i (String tag, String log) {
        if (isDebug) {
            Log.i(tag, log);
        }
    }

    public static void v (String tag, String log) {
        if (isDebug) {
            Log.v(tag, log);
        }
    }
}
