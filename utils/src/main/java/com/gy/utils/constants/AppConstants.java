package com.gy.utils.constants;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

/**
 * Created by ganyu on 2016/10/10.
 *
 */
public class AppConstants {

    public static String getVerionName(Context context) {
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

    public static String getPackageName(Context context) {
        try {
            return context.getPackageName();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getMetaData (Context context, String key, String defaultVal) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().
                    getApplicationInfo(getPackageName(context), PackageManager.GET_META_DATA);
            String data = applicationInfo.metaData.getString(key);
            if (TextUtils.isEmpty(data)) return defaultVal;
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
