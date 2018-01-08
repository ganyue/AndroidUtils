package com.gy.utils.audio.mediaplayer;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.gy.utils.app.AppUtils;
import com.gy.utils.constants.AppConstants;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganyu on 2016/10/10.
 *
 */
public class MediaPlayerUtils implements IMediaPlayer{

    private static MediaPlayerUtils mInstance;
    private WeakReference<Application> mApp;
    private List<OnMediaListener> onMediaListeners;
    private MediaPlayerStateReceiver mediaPlayerStateReceiver;

    public static MediaPlayerUtils getInstance (Application application) {
        if (mInstance == null) {
            mInstance = new MediaPlayerUtils(application);
        }

        return mInstance;
    }

    private MediaPlayerUtils(Application application) {
        mApp = new WeakReference<>(application);
        mediaPlayerStateReceiver = new MediaPlayerStateReceiver();
        IntentFilter intentFilter = new IntentFilter(MediaPlayerService.ACTION_PLAYER_STATUS_CHANGED);
//        LocalBroadcastManager.getInstance(application).registerReceiver(mediaPlayerStateReceiver, intentFilter);
        application.registerReceiver(mediaPlayerStateReceiver, intentFilter);
    }

    @Override
    public void play(String path, Bundle extras) {
        Intent intent = new Intent(mApp.get(), MediaPlayerService.class);
        intent.putExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_CMD_I, MediaPlayerConst.PlayerConsts.Cmds.CMD_PLAY);
        intent.putExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_SOURCE_PATH, path);
        intent.putExtras(extras);
        mApp.get().startService(intent);
    }

    @Override
    public void playOrPause() {
        Intent intent = new Intent(mApp.get(), MediaPlayerService.class);
        intent.putExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_CMD_I, MediaPlayerConst.PlayerConsts.Cmds.CMD_PLAY_OR_PAUSE);
        mApp.get().startService(intent);
    }

    public void playOrPause(String path, Bundle extras) {
        Intent intent = new Intent(mApp.get(), MediaPlayerService.class);
        intent.putExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_CMD_I, MediaPlayerConst.PlayerConsts.Cmds.CMD_PLAY_OR_PAUSE);
        intent.putExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_SOURCE_PATH, path);
        intent.putExtras(extras);
        mApp.get().startService(intent);
    }

    @Override
    public void playOnly() {
        Intent intent = new Intent(mApp.get(), MediaPlayerService.class);
        intent.putExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_CMD_I, MediaPlayerConst.PlayerConsts.Cmds.CMD_PLAY_ONLY);
        mApp.get().startService(intent);
    }

    @Override
    public void pauseOnly() {
        Intent intent = new Intent(mApp.get(), MediaPlayerService.class);
        intent.putExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_CMD_I, MediaPlayerConst.PlayerConsts.Cmds.CMD_PAUSE_ONLY);
        mApp.get().startService(intent);
    }

    @Override
    public void stop() {
        Intent intent = new Intent(mApp.get(), MediaPlayerService.class);
        intent.putExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_CMD_I, MediaPlayerConst.PlayerConsts.Cmds.CMD_STOP);
        mApp.get().startService(intent);
    }

    @Override
    public void seek(int pos) {
        Intent intent = new Intent(mApp.get(), MediaPlayerService.class);
        intent.putExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_CMD_I, MediaPlayerConst.PlayerConsts.Cmds.CMD_SEEK);
        intent.putExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_SEEK_I, pos);
        mApp.get().startService(intent);
    }

    @Override
    public void setVolume(int volume) {
        Intent intent = new Intent(mApp.get(), MediaPlayerService.class);
        intent.putExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_CMD_I, MediaPlayerConst.PlayerConsts.Cmds.CMD_SET_VOL);
        intent.putExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_VOLUME_I, volume);
        mApp.get().startService(intent);
    }

    @Override
    public void updateStatus() {
        Intent intent = new Intent(mApp.get(), MediaPlayerService.class);
        intent.putExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_CMD_I, MediaPlayerConst.PlayerConsts.Cmds.CMD_UPDATE_STATUS);
        mApp.get().startService(intent);
    }


    public void addOnMediaListener(OnMediaListener listener) {
        if (onMediaListeners == null) onMediaListeners = new ArrayList<>();
        if (!onMediaListeners.contains(listener)) {
            onMediaListeners.add(listener);
        }
    }

    public void removeOnMediaListener(OnMediaListener listener) {
        if (onMediaListeners.contains(listener)) {
            onMediaListeners.remove(listener);
        }
    }

    public void release () {
        mApp.get().unregisterReceiver(mediaPlayerStateReceiver);
        mApp.get().stopService(new Intent(mApp.get(), MediaPlayerService.class));
    }

    class MediaPlayerStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaStatus mediaStatus = intent.getParcelableExtra(MediaPlayerConst.BroadCastConsts.Keys.KEY_MEDIA_STATUS_O);
            int state = intent.getIntExtra(MediaPlayerConst.BroadCastConsts.Keys.KEY_STATE_I, 0);

//            LogUtils.d("yue.gan", "state : "+state + "\n"+mediaStatus);
//            LogUtils.d("yue.gan", "process : " + AppConstants.getProcessName(context));

            String processName = AppUtils.getProcessName(context);
            if (processName.contains(":")) return;

            if (state == MediaPlayerConst.BroadCastConsts.States.PLAY) {
                for (OnMediaListener listener: onMediaListeners) {
                    listener.onPlay(mediaStatus);
                }
            }else if (state == MediaPlayerConst.BroadCastConsts.States.PAUSE) {
                for (OnMediaListener listener: onMediaListeners) {
                    listener.onPause(mediaStatus);
                }
            } else if (state == MediaPlayerConst.BroadCastConsts.States.STOP) {
                for (OnMediaListener listener: onMediaListeners) {
                    listener.onStop(mediaStatus);
                }
            } else if (state == MediaPlayerConst.BroadCastConsts.States.SEEK) {
                for (OnMediaListener listener: onMediaListeners) {
                    listener.onSeek(mediaStatus);
                }
            } else if (state == MediaPlayerConst.BroadCastConsts.States.COMPLETE) {
                for (OnMediaListener listener: onMediaListeners) {
                    listener.onCompelete(mediaStatus);
                }
            } else if (state == MediaPlayerConst.BroadCastConsts.States.ERROR) {
                String errorMsg = intent.getStringExtra(MediaPlayerConst.BroadCastConsts.Keys.KEY_ERROR_MSG);
                for (OnMediaListener listener: onMediaListeners) {
                    listener.onError(mediaStatus, errorMsg);
                }
            }
        }
    }
}
