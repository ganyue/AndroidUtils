package com.gy.appbase.application;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.CallSuper;

import com.gy.utils.BuildConfig;
import com.gy.utils.bluetooth.BluetoothUtils;
import com.gy.utils.http.HttpUtils;
import com.gy.utils.img.ImageLoaderUtils;
import com.gy.utils.log.LogUtils;
import com.gy.utils.preference.SharedPreferenceUtils;
import com.gy.utils.wifi.WifiUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yue.gan on 2016/7/19.
 *
 */
public class BaseApplication extends Application{

    protected static Application application;
    protected static boolean isDebug;
    private List<WeakReference<Activity>> activities;

    @CallSuper
    @Override
    public void onCreate() {
        super.onCreate();

        application = this;
        isDebug = BuildConfig.DEBUG;
        LogUtils.enableLogToFile(this, isDebug);   //debug 版本打印日志, release版本不打印
        if (!isDebug()) {
            LogUtils.registReceiverForRuntime(this);
        }
        initCrashHandler();         //设置全局异常处理器
        getImageLoader();           //初始化 image loader
        getHttpUtils();             //初始化 http utils
        getWifiUtils();             //初始化网络状态监听
    }

    public static boolean isDebug () {
        return isDebug;
    }

    public void initCrashHandler () {
        new DefaultCrashHandler(application);
    }

    public static Application getApplication () {
        return application;
    }

    public static ImageLoaderUtils getImageLoader () {
        return ImageLoaderUtils.getInstance(application);
    }

    public static HttpUtils getHttpUtils () {
        return HttpUtils.getInstance(application);
    }

    public static WifiUtils getWifiUtils () {
        return WifiUtils.getInstance(application);
    }

    public static BluetoothUtils getBluetoothUtils () {
        return BluetoothUtils.getInstance(application);
    }

    public static SharedPreferenceUtils getPreferenceUtils () {
        return SharedPreferenceUtils.getInstance(application);
    }

    /**
     * 一些数据库操作是很耗时的，
     * 但同时多个线程一起存取数据库容易出问题，
     * 所以这里提供一个单线程的线程池来做这些操作
     */
    private static ExecutorService executorService;
    public static ExecutorService getExecutorService () {
        if (executorService == null) executorService = Executors.newSingleThreadExecutor();
        return executorService;
    }

    public void addActivity (Activity activity) {
        if (activities == null) activities = new ArrayList<>();
        activities.add(new WeakReference<>(activity));
    }

    public void finishAllActivity () {
        if (activities == null) return;
        for (WeakReference<Activity> activity: activities) {
            if (activity.get() == null) continue;
            activity.get().finish();
        }
        activities.clear();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (!isDebug()) {
            LogUtils.unRegistReceiverForRuntime(this);
        }
    }
}
