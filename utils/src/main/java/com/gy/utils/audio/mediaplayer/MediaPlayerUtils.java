package com.gy.utils.audio.mediaplayer;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Process;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganyu on 2016/10/10.
 *
 */
public class MediaPlayerUtils implements IMediaPlayer{

    private static MediaPlayerUtils mInstance;
    private WeakReference<Context> mContext;
    private List<OnMediaListener> onMediaListeners;
    private MediaPlayerStateReceiver mediaPlayerStateReceiver;

    public static MediaPlayerUtils getInstance (Context context) {
        if (mInstance == null) {
            mInstance = new MediaPlayerUtils(context.getApplicationContext());
        }

        return mInstance;
    }

    private MediaPlayerUtils(Context context) {
        mContext = new WeakReference<>(context);
        mediaPlayerStateReceiver = new MediaPlayerStateReceiver();
        IntentFilter intentFilter = new IntentFilter(MediaPlayerService.ACTION_PLAYER_STATUS_CHANGED);
//        LocalBroadcastManager.getInstance(application).registerReceiver(mediaPlayerStateReceiver, intentFilter);
        context.registerReceiver(mediaPlayerStateReceiver, intentFilter);
    }

    @Override
    public void play(String path, Bundle extras) {
        Intent intent = new Intent(mContext.get(), MediaPlayerService.class);
        intent.putExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_CMD_I, MediaPlayerConst.PlayerConsts.Cmds.CMD_PLAY);
        intent.putExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_SOURCE_PATH, path);
        if (extras != null) intent.putExtras(extras);
        mContext.get().startService(intent);
    }

    @Override
    public void playOrPause() {
        Intent intent = new Intent(mContext.get(), MediaPlayerService.class);
        intent.putExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_CMD_I, MediaPlayerConst.PlayerConsts.Cmds.CMD_PLAY_OR_PAUSE);
        mContext.get().startService(intent);
    }

    public void playOrPause(String path, Bundle extras) {
        Intent intent = new Intent(mContext.get(), MediaPlayerService.class);
        intent.putExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_CMD_I, MediaPlayerConst.PlayerConsts.Cmds.CMD_PLAY_OR_PAUSE);
        intent.putExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_SOURCE_PATH, path);
        if (extras != null) intent.putExtras(extras);
        mContext.get().startService(intent);
    }

    @Override
    public void playOnly() {
        Intent intent = new Intent(mContext.get(), MediaPlayerService.class);
        intent.putExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_CMD_I, MediaPlayerConst.PlayerConsts.Cmds.CMD_PLAY_ONLY);
        mContext.get().startService(intent);
    }

    @Override
    public void pauseOnly() {
        Intent intent = new Intent(mContext.get(), MediaPlayerService.class);
        intent.putExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_CMD_I, MediaPlayerConst.PlayerConsts.Cmds.CMD_PAUSE_ONLY);
        mContext.get().startService(intent);
    }

    @Override
    public void stop() {
        Intent intent = new Intent(mContext.get(), MediaPlayerService.class);
        intent.putExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_CMD_I, MediaPlayerConst.PlayerConsts.Cmds.CMD_STOP);
        mContext.get().startService(intent);
    }

    @Override
    public void seek(int pos) {
        Intent intent = new Intent(mContext.get(), MediaPlayerService.class);
        intent.putExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_CMD_I, MediaPlayerConst.PlayerConsts.Cmds.CMD_SEEK);
        intent.putExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_SEEK_I, pos);
        mContext.get().startService(intent);
    }

    @Override
    public void setVolume(int volume) {
        Intent intent = new Intent(mContext.get(), MediaPlayerService.class);
        intent.putExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_CMD_I, MediaPlayerConst.PlayerConsts.Cmds.CMD_SET_VOL);
        intent.putExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_VOLUME_I, volume);
        mContext.get().startService(intent);
    }

    @Override
    public void updateStatus() {
        Intent intent = new Intent(mContext.get(), MediaPlayerService.class);
        intent.putExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_CMD_I, MediaPlayerConst.PlayerConsts.Cmds.CMD_UPDATE_STATUS);
        mContext.get().startService(intent);
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
        try {
            mContext.get().stopService(new Intent(mContext.get(), MediaPlayerService.class));
            mContext.get().unregisterReceiver(mediaPlayerStateReceiver);
            mInstance = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取当前进程的进程名
     */
    public static String getProcessName (Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int pid = Process.myPid();
        List<ActivityManager.RunningAppProcessInfo> infos = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info: infos) {
            if (pid == info.pid) {
                return info.processName;
            }
        }
        return "";
    }

    class MediaPlayerStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (onMediaListeners == null || onMediaListeners.size() <= 0) return;
            MediaStatus mediaStatus = intent.getParcelableExtra(MediaPlayerConst.BroadCastConsts.Keys.KEY_MEDIA_STATUS_O);
            int state = intent.getIntExtra(MediaPlayerConst.BroadCastConsts.Keys.KEY_STATE_I, 0);

//            LogUtils.d("yue.gan", "state : "+state + "\n"+mediaStatus);
//            LogUtils.d("yue.gan", "process : " + AppConstants.getProcessName(context));

            String processName = getProcessName(context);
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
            } else if (state == MediaPlayerConst.BroadCastConsts.States.PREPARING) {
                for (OnMediaListener listener: onMediaListeners) {
                    listener.onPreparing(mediaStatus);
                }
            }
        }
    }
}
