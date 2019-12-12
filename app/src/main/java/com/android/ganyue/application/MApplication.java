package com.android.ganyue.application;


import com.gy.appbase.application.BaseApplication;
import com.gy.utils.audio.mediaplayer.MediaPlayerUtils;

/**
 * Created by yue.gan on 2016/7/21.
 *
 */
public class MApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        getMediaPlayerUtils();              //启动MediaPlayerService
    }

    public static MediaPlayerUtils getMediaPlayerUtils () {
        return MediaPlayerUtils.getInstance(application);
    }
}
