package com.gy.utils.constants;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

/**
 * Created by ganyu on 2016/10/10.
 *
 * <p>1、获取版本号、版本名、包名、meta data</p>
 * <p>2、检查指定应用是否存在，启动其他应用</p>
 * <p></p>
 */
public class AppConstants {

    /**
     * 获取版本名
     */
    public static String getVersionName(Context context) {
        String versionName = null;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 获取版本号
     */
    public static int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versionCode = info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取包名
     */
    public static String getPackageName(Context context) {
        try {
            return context.getPackageName();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取meta data
     */
    public static String getMetaData (Context context, String key, String defaultVal) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().
                    getApplicationInfo(getPackageName(context), PackageManager.GET_META_DATA);
            String data = applicationInfo.metaData.getString(key);
            if (TextUtils.isEmpty(data)) return defaultVal;
            return data;
        } catch (Exception e) {
        }
        return defaultVal;
    }

}
