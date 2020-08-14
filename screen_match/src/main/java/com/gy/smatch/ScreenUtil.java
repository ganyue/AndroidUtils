package com.gy.smatch;

import android.content.Context;
import android.content.res.Resources;

import com.laosj.dimens.R;

/**
 * Created by yue.gan. 2019/7/28
 */
public class ScreenUtil {

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight (Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取状态栏高度，获取失败会默认给25dp高度（目前看到的源码基本都这么高）
     */
    public static int getStatusBarHeight (Context context) {
        Resources res = context.getResources();
        int id = res.getIdentifier("status_bar_height", "dimen", "android");
        if (id != 0) return res.getDimensionPixelSize(id);
        return getResDimenPx(context, R.dimen.dp_25) + 1;
    }

    /**
     * 未对小数位做处理
     */
    public static float getResDimen (Context context, int res) {
        return context.getResources().getDimension(res);
    }

    /**
     * 做了四舍五入的
     */
    public static int getResDimenPx (Context context, int res) {
        return context.getResources().getDimensionPixelSize(res);
    }

    /**
     * 做了四舍五入的
     */
    public static int px2dp (Context context, int px) {
        float dp = px / context.getResources().getDisplayMetrics().density;
        return (int) (dp > 0? dp + 0.5: dp - 0.5);
    }

    /**
     * 做了四舍五入的
     */
    public static int dp2px (Context context, int dp) {
        float px = dp * context.getResources().getDisplayMetrics().density;
        return (int) (px > 0? px + 0.5: px - 0.5);
    }
}
