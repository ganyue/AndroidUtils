package com.android.ganyue.application;

import com.baidu.mapapi.SDKInitializer;
import com.gy.appbase.application.BaseApplication;
import com.gy.utils.audio.AudioUtils;
import com.gy.utils.log.LogUtils;

/**
 * Created by ganyu on 2016/7/21.
 *
 */
public class MApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        LogUtils.d("yue.gan", "start init media service");
        AudioUtils.getInstance(this);       //初始化audio播放器
        SDKInitializer.initialize(this);    //初始化百度地图api
    }

    public static AudioUtils getAudioUtils () {
        return AudioUtils.getInstance(getApplication());
    }
}
