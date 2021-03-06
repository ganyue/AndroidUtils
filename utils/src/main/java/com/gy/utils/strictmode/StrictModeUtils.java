package com.gy.utils.strictmode;

import android.content.Context;
import android.os.StrictMode;

import com.gy.utils.log.LogUtils;

/**
 * created by yue.gan 18-9-5
 */
public class StrictModeUtils {

    public static void startDebugMode (Context context) {
        startVmStrictMode();
        startThreadStrictMode();
        LogUtils.enableLog(true);
        LogUtils.enableLogToFile(context, true);
    }

    private static void startVmStrictMode () {
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
            .detectAll()
            .penaltyLog()
            .build());
    }

    private static void startThreadStrictMode () {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
            .detectAll()
            .penaltyLog()
            .penaltyFlashScreen()
            .build());
    }
}
