package com.gy.utils.audio;

import android.app.Application;

import com.gy.utils.audio.mediaplayer.MediaPlayerUtils;
import com.gy.utils.audio.mpd.MpdPlayerUtils;

/**
 * Created by ganyu on 2016/10/13.
 *
 */
public class AudioPlayerUtils {

    private static AudioPlayerUtils mInstance;
    private Application mApp;
    private MediaPlayerUtils mediaPlayerUtils;
    private MpdPlayerUtils mpdPlayerUtils;

    public static AudioPlayerUtils getInstance (Application application) {
        if (mInstance == null) {
            mInstance = new AudioPlayerUtils(application);
        }

        return mInstance;
    }

    private AudioPlayerUtils(Application application) {
        mApp = application;
        mediaPlayerUtils = MediaPlayerUtils.getInstance(application);
        mpdPlayerUtils = MpdPlayerUtils.getInstance();
    }

    

}
