package com.gy.utils.constants;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Process;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by ganyu on 2016/10/10.
 *
 * <p>1、获取版本号、版本名、包名、meta data</p>
 * <p>2、检查指定应用是否存在，启动其他应用</p>
 * <p></p>
 */
public class AppConstants {

    public static Intent getAppIntentByPackageName (Context context, String pkgName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (packageInfo == null) return null;

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(packageInfo.packageName);

        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, 0);
        ResolveInfo info = resolveInfos.iterator().next();
        if (info != null) {
            String packageName = info.activityInfo.packageName;
            String className = info.activityInfo.name;
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName componentName = new ComponentName(packageName, className);
            i.setComponent(componentName);
            return i;
        }
        return null;
    }

    public static String getProcessName (Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int pid = Process.myPid();
        List<ActivityManager.RunningAppProcessInfo> infos = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info: infos) {
            if (pid == info.pid) {
                return info.processName;
            }
        }
        return "";
    }

    public static boolean isBackground(Context context) {
        android.app.ActivityManager activityManager = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfos = activityManager.getRunningTasks(1);
        if (!taskInfos.isEmpty()) {
            ComponentName topActivity = taskInfos.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

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
        }
        return defaultVal;
    }

    public static boolean isApkExist (Context context, String packageName){
        if (TextUtils.isEmpty(packageName)) return false;
        try {
            ApplicationInfo info = context.getPackageManager()
                    .getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isApkExist (Context context, Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, 0);
        return list.size() > 0;
    }

    public static String getMarketSchemeStr (String pkgName) {
        return "market:/details?id="+pkgName;
    }
}
