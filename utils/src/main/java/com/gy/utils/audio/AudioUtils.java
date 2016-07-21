package com.gy.utils.audio;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.gy.utils.audio.mediaplayer.MediaPlayerService;
import com.gy.utils.log.LogUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganyu on 2016/7/20.
 *
 * <p>must pass a application instance to this use init method</p>
 */
public class AudioUtils {

    public enum AudioType {
        MEDIA, MPD
    }

    public static final String ACTION_AUDIO_PLAYER_CALLBACK_RECEIVER = AudioPlayerCallbackReceiver.class.getName();

    private IAudioPlayer mMediaPlayer;
    private IAudioPlayer mMpdPlayer;

    private static AudioUtils mInstance;
    private WeakReference<Application> mApp;
    private List<OnAudioListener> mOnAudioListeners;
    private BroadcastReceiver mAudioCallbackReceiver;

    public static AudioUtils getInstance (Application application) {
        if (mInstance == null) {
            mInstance = new AudioUtils(application);
        }
        return mInstance;
    }

    public IAudioPlayer getPlayer (AudioType type) {
        switch (type) {
            case MEDIA:
                return mMediaPlayer;
            case MPD:
                return null;
            default:
                return null;
        }
    }

    private AudioUtils(Application application) {
        mApp = new WeakReference<>(application);
        mAudioCallbackReceiver = new AudioPlayerCallbackReceiver();
        IntentFilter intentFilter = new IntentFilter(ACTION_AUDIO_PLAYER_CALLBACK_RECEIVER);
        mApp.get().registerReceiver(mAudioCallbackReceiver, intentFilter);

        initPlayers();
    }

    public void initPlayers () {
        Intent intent = new Intent(mApp.get(), MediaPlayerService.class);
        LogUtils.d("yue.gan", "start media service");
        mApp.get().startService(intent);
        mApp.get().bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mMediaPlayer = (IAudioPlayer) service;
                LogUtils.d("yue.gan", "media player inited");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mMediaPlayer = null;
            }
        }, Context.BIND_AUTO_CREATE);
    }

    public void addOnAudioListener (OnAudioListener listener) {
        if (mOnAudioListeners == null) {
            mOnAudioListeners = new ArrayList<>();
        }

        if (!mOnAudioListeners.contains(listener)) {
            mOnAudioListeners.add(listener);
        }
    }

    public void removeOnAudioListener (OnAudioListener listener) {
        if (mOnAudioListeners == null || !mOnAudioListeners.contains(listener)) {
            return;
        }
        mOnAudioListeners.remove(listener);
    }

    public void release () {
        mApp.get().unregisterReceiver(mAudioCallbackReceiver);
        mApp.clear();
        mApp = null;
        if (mOnAudioListeners != null) {
            mOnAudioListeners.clear();
            mOnAudioListeners = null;
        }
    }

    class AudioPlayerCallbackReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mOnAudioListeners == null) {
                return;
            }

            int type = intent.getIntExtra(AudioPlayerConst.PlayerConsts.Keys.KEY_TYPE_I, 0);

            if (type == AudioPlayerConst.PlayerConsts.BCastType.STATE) {
                String sender = intent.getStringExtra(AudioPlayerConst.PlayerConsts.Keys.KEY_SENDER_S);
                Playlist playlist = intent.getParcelableExtra(AudioPlayerConst.PlayerConsts.Keys.KEY_PLAYLIST_O);
                boolean isPlaying = intent.getBooleanExtra(AudioPlayerConst.PlayerConsts.Keys.KEY_IS_PLAYING_B, false);
                int position = intent.getIntExtra(AudioPlayerConst.PlayerConsts.Keys.KEY_POSITION_I, 0);

                for (OnAudioListener listener: mOnAudioListeners) {
                    listener.onStateChanged(sender, playlist, position, isPlaying);
                }
            } else if (type == AudioPlayerConst.PlayerConsts.BCastType.ERROR) {
                String sender = intent.getStringExtra(AudioPlayerConst.PlayerConsts.Keys.KEY_SENDER_S);
                int extra = intent.getIntExtra(AudioPlayerConst.PlayerConsts.Keys.KEY_EXTRA_I, 0);

                for (OnAudioListener listener: mOnAudioListeners) {
                    listener.onError(sender, extra);
                }
            } else if (type == AudioPlayerConst.PlayerConsts.BCastType.COMPLETE) {
                String sender = intent.getStringExtra(AudioPlayerConst.PlayerConsts.Keys.KEY_SENDER_S);
                Playlist playlist = intent.getParcelableExtra(AudioPlayerConst.PlayerConsts.Keys.KEY_PLAYLIST_O);

                for (OnAudioListener listener: mOnAudioListeners) {
                    listener.onComplete(sender, playlist);
                }
            }

        }
    }

    public interface OnAudioListener {
        void onStateChanged (String sender, Playlist playlist, int position, boolean isPlaying);
        void onComplete (String sender, Playlist playlist);
        void onError (String sender, int extra);
    }
}
