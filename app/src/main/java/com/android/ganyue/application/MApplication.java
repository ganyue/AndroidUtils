package com.android.ganyue.application;


import com.baidu.mapapi.SDKInitializer;
import com.gy.appbase.application.BaseApplication;
import com.gy.utils.audio.mediaplayer.MediaPlayerUtils;
import com.gy.utils.log.LogUtils;
import com.gy.xunfei.XunfeiUtils;

/**
 * Created by yue.gan on 2016/7/21.
 *
 */
public class MApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        XunfeiUtils.getInstance(this);      //初始化讯飞sdk
        SDKInitializer.initialize(this);    //初始化百度地图api

        getMediaPlayerUtils();              //启动MediaPlayerService

        LogUtils.enableLog(true);
        LogUtils.enableLogToFile(true);
    }

    public static MediaPlayerUtils getMediaPlayerUtils () {
        return MediaPlayerUtils.getInstance(application);
    }
}
