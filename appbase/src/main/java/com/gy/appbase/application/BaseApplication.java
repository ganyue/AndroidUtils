package com.gy.appbase.application;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.support.annotation.CallSuper;

import com.gy.utils.http.HttpUtils;
import com.gy.utils.img.ImageLoaderUtils;
import com.gy.utils.log.LogUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by ganyu on 2016/7/19.
 *
 */
public class BaseApplication extends Application{

    private static Application application;
    private static boolean isDebug;

    @CallSuper
    @Override
    public void onCreate() {
        super.onCreate();

        application = this;
        isDebug = (getApplicationInfo().flags& ApplicationInfo.FLAG_DEBUGGABLE)!=0;

        LogUtils.enable(isDebug);   //debug 版本打印日志, release版本不打印
        initCrashHandler();         //设置全局异常处理器
        getImageLoader();           //初始化 image loader
        getHttpUtils();             //初始化 http utils
    }

    public static boolean isDebug () {
        return isDebug;
    }

    public static Application getApplication () {
        return application;
    }

    public void initCrashHandler () {
        new DefaultCrashHandler(application);
    }

    public static ImageLoader getImageLoader () {
        return ImageLoaderUtils.getImageLoader(application);
    }

    public static HttpUtils getHttpUtils () {
        return HttpUtils.getInstance(application);
    }
}
