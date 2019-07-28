package com.gy.utils.constants;

import android.app.Activity;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;

/**
 * Created by yue.gan on 2016/5/16.
 *
 * 设备屏幕宽、高等信息，由于需要初始化通知栏高度，所以
 * 最好在Activity的onWindowFocusChanged里头初始化一次
 */
@Deprecated
public class WindowConstants {

    private static WindowConstants mInstance;

    public static WindowConstants getInstance (Activity activity) {
        if (mInstance == null) {
            mInstance = new WindowConstants(activity);
        }
        return mInstance;
    }

    private boolean isInited;
    private int windowWidth;            //屏幕宽度
    private int windowHeight;           //屏幕高度
    private int notificationBarHeight;  //顶部通知栏高度
    private int contentHeight;          //页面高度
    private float density;              //像素密度

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

    /**
     * 获取顶部通知栏高度
     */
    public int getNotificationBarHeight() {
        return notificationBarHeight;
    }

    /**
     * 获取屏幕高度
     */
    public int getWindowHeight() {
        return windowHeight;
    }

    /**
     * 获取屏幕宽度
     */
    public int getWindowWidth() {
        return windowWidth;
    }

    /**
     * 获取页面高度
     */
    public int getContentHeight() {
        return contentHeight;
    }

    /**
     * 把dp转换成实际像素数
     */
    public float convertDpToPix (int dp) {
        return dp * density;
    }
}
