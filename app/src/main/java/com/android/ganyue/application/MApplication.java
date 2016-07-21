package com.android.ganyue.application;

import com.gy.appbase.application.ApplicationBase;
import com.gy.utils.audio.AudioUtils;
import com.gy.utils.log.LogUtils;

/**
 * Created by ganyu on 2016/7/21.
 */
public class MApplication extends ApplicationBase {

    @Override
    public void onCreate() {
        super.onCreate();

        LogUtils.d("yue.gan", "start init media service");
        AudioUtils.getInstance(this);
    }

    public static AudioUtils getAudioUtils () {
        return AudioUtils.getInstance(getApplication());
    }
}
