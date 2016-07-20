package com.gy.utils.audio;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.lang.ref.WeakReference;

/**
 * Created by ganyu on 2016/7/20.
 *
 * <p>must pass a application instance to this use init method</p>
 */
public class AudioUtils {

    private static AudioUtils mInstance;
    private WeakReference<Application> mApp;

    public static AudioUtils getInstance (Application application) {
        if (mInstance == null) {
            mInstance = new AudioUtils(application);
        }
        return mInstance;
    }

    public AudioUtils(Application application) {
        mApp = new WeakReference<Application>(application);
    }

    class PlayerCallbackReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Playlist playlist = intent.getParcelableExtra(AudioPlayerConst.PlayerConsts.Keys.KEY_PLAYLIST_O);
            boolean isPlaying = intent.getBooleanExtra(AudioPlayerConst.PlayerConsts.Keys.KEY_IS_PLAYING_B, false);
            int position = intent.getIntExtra(AudioPlayerConst.PlayerConsts.Keys.KEY_POSITION_I, 0);

        }
    }

    public interface OnAudioListener {
        void onComplete (Playlist playlist);
    }
}
