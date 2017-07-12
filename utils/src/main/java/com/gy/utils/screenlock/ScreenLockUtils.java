package com.gy.utils.screenlock;

import android.content.Context;

/**
 * Created by ganyu on 2017/4/13.
 *
 */

public class ScreenLockUtils {
    private static ScreenLockUtils mInstance;
    private Context context;
    public static ScreenLockUtils getInstance (Context context) {
        if (mInstance == null) {
            mInstance = new ScreenLockUtils(context);
        }
        return mInstance;
    }

    private ScreenLockUtils(){}

    private ScreenLockUtils(Context context) {

    }

    private void release () {

    }
}
