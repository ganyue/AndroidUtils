package com.gy.appbase.application;

import android.app.Application;
import android.support.annotation.CallSuper;

import com.gy.utils.http.HttpUtils;
import com.gy.utils.img.ImageLoaderUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by ganyu on 2016/7/19.
 *
 */
public class ApplicationBase extends Application{

    private static Application application;

    @CallSuper
    @Override
    public void onCreate() {
        super.onCreate();

        application = this;
        ImageLoaderUtils.getImageLoader(this); //init image loader
        HttpUtils.getInstance(this);//init http utils
    }

    public Application getApplication () {
        return application;
    }

    public void initCrashHandler () {
        DefaultCrashHandler defaultCrashHandler = new DefaultCrashHandler(application);
    }

    public static ImageLoader getImageLoader () {
        return ImageLoaderUtils.getImageLoader(application);
    }

    public static HttpUtils getHttpUtils () {
        return HttpUtils.getInstance(application);
    }
}
