package com.gy.utils.app;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganyu on 2016/10/10.
 *
 * <p>1、获取版本号、版本名、包名、meta data</p>
 * <p>2、检查指定应用是否存在，启动其他应用</p>
 * <p></p>
 */
public class AppUtils {

    /**
     * 打开应用商店，如果打开失败，则打开默认链接
     */
    public static void openMarket (Context context, String pkgName, String defaultMarketUrl) {

        try {
            String str = "market://details?id=" + pkgName;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(str));
            context.startActivity(intent);
        } catch (Exception e) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(defaultMarketUrl));
            context.startActivity(intent);
        }
    }

    /**
     * 判断是否安装指定包名对应的应用，如果安装返回打开该应用的intent，如果未安装返回null
     */
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

    /**
     * 获取当前进程的进程名
     */
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

    /**
     * 应用是否在后台
     */
    public static boolean isBackground(Context context, String pkgName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT > 20) {
            List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo info: processInfos) {
                if (info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    String[] pkgList = info.pkgList;
                    for (String pkg : pkgList) {
                        if (pkg.equals(pkgName)) return false;
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfos = activityManager.getRunningTasks(1);
            if (!taskInfos.isEmpty()) {
                ComponentName topActivity = taskInfos.get(0).topActivity;
                if (topActivity.getPackageName().equals(context.getPackageName())) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * am命令行启动另一个app某个页面，e.g: am start --user 0 -n com.example.test/.activity.MainActivity
     * @param pkgName       包名
     * @param activtyPath  如果包名相同路径下，直接用   ./xxx/xxx/xxxActivity
     * @return 调用的命令行
     */
    public static String startOtherAppActivity (String pkgName, String activtyPath) {
        String cmd = "am start --user 0 -n " + pkgName + "/" + activtyPath;
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append("try to start component by cmd : ");
        resultBuilder.append(cmd);
        try {
            java.lang.Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader successReader= new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            resultBuilder.append("result is : \n");
            String tmp;
            while ((tmp = successReader.readLine()) != null) {resultBuilder.append(tmp).append("\n");}
            while ((tmp = errorReader.readLine()) != null) {resultBuilder.append(tmp).append("\n");}
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultBuilder.toString();
    }


    /**
     * am命令行启动另一个app某个服务，e.g: am start --user 0 -n com.example.test/.service.xxxService
     * @param pkgName       包名
     * @param servicePath  如果包名相同路径下，直接用   ./xxx/xxx/xxxActivity
     * @return 调用的命令行
     */
    public static String startOtherAppService (String pkgName, String servicePath) {
        String cmd = "am startservice --user 0 -n " + pkgName + "/" + servicePath;
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append("try to start component by cmd : ");
        resultBuilder.append(cmd);
        try {
            java.lang.Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader successReader= new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            resultBuilder.append("result is : \n");
            String tmp;
            while ((tmp = successReader.readLine()) != null) {resultBuilder.append(tmp).append("\n");}
            while ((tmp = errorReader.readLine()) != null) {resultBuilder.append(tmp).append("\n");}
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultBuilder.toString();
    }
}
