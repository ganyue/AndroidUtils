package com.gy.utils.constants;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;

/**
 * Created by ganyu on 2016/5/16.
 *
 */
public class WindowConstants {

    private static WindowConstants mInstance;

    public static WindowConstants getInstance (Activity activity) {
        if (mInstance == null) {
            mInstance = new WindowConstants(activity);
        }
        return mInstance;
    }

    private boolean isInited;
    private int windowWidth;
    private int windowHeight;
    private int notificationBarHeight;
    private int contentHeight;

    private WindowConstants (Activity activity) {
        if (!isInited) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            windowWidth = metrics.widthPixels;
            windowHeight = metrics.heightPixels;

            View view = activity.getWindow().getDecorView();
            View contentV = view.findViewById(android.R.id.content);
            notificationBarHeight = contentV.getTop();
            contentHeight = contentV.getHeight();

            isInited = true;
        }
    }

    public int getNotificationBarHeight() {
        return notificationBarHeight;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getContentHeight() {
        return contentHeight;
    }

}
