package com.gy.utils.constants;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
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
    private float density;

    private WindowConstants (Activity activity) {
        if (!isInited) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            windowWidth = metrics.widthPixels;
            windowHeight = metrics.heightPixels;

            View view = activity.getWindow().getDecorView();
            View contentV = view.findViewById(android.R.id.content);
            contentHeight = contentV.getHeight();

            Rect rect = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            notificationBarHeight = rect.top;

            density = activity.getResources().getDisplayMetrics().density;

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

    public float convertDpToPix (int dp) {
        return dp * density;
    }
}
