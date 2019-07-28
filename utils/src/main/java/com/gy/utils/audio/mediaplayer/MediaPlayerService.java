package com.gy.utils.audio.mediaplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;

import com.gy.utils.log.LogUtils;

import java.io.IOException;

/**
 * Created by yue.gan on 2016/7/19.
 *
 */
public class MediaPlayerService extends Service implements IMediaPlayer {

    public static final String ACTION_PLAYER_STATUS_CHANGED = MediaPlayerService.class.getName();
    public static final boolean enableNotification = true;

    enum  PlayerState {
        UNINITED,
        PREPARING,
        PREPARED,
        PLAYING,
        PAUSE,
        STOP,
    }

    private MediaNotifier mediaNotifier;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private PlayerState state;
    private boolean startAfterPrepare;
    private String sourcePath = "";
    private Bundle trackInfos;
    
    @Override
    public void onCreate() {
        super.onCreate();
        if (mediaPlayer == null) {
            initMediaPlayer();
        }
    }

    private void initMediaPlayer () {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(onPreparedListener);
        mediaPlayer.setOnCompletionListener(onCompletionListener);
        mediaPlayer.setOnErrorListener(onErrorListener);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        state = PlayerState.UNINITED;
        startAfterPrepare = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_STICKY;

        if (mediaPlayer == null) {
            initMediaPlayer();
        }

        if (mediaNotifier == null) {
            mediaNotifier = new MediaNotifier(this);
        }

        int cmd = intent.getIntExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_CMD_I, MediaPlayerConst.PlayerConsts.Cmds.CMD_UNKNOWN);

        switch (cmd) {
            case MediaPlayerConst.PlayerConsts.Cmds.CMD_PLAY:
                String path = intent.getStringExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_SOURCE_PATH);
                play(path, intent.getExtras());
                break;
            case MediaPlayerConst.PlayerConsts.Cmds.CMD_STOP:
                stop();
                break;
            case MediaPlayerConst.PlayerConsts.Cmds.CMD_PLAY_ONLY:
                playOnly();
                break;
            case MediaPlayerConst.PlayerConsts.Cmds.CMD_PAUSE_ONLY:
                pauseOnly();
                break;
            case MediaPlayerConst.PlayerConsts.Cmds.CMD_PLAY_OR_PAUSE:
                if (TextUtils.isEmpty(sourcePath)) {
                    play(intent.getStringExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_SOURCE_PATH), intent.getExtras());
                } else {
                    playOrPause();
                }
                break;
            case MediaPlayerConst.PlayerConsts.Cmds.CMD_SEEK:
                int seek = intent.getIntExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_SEEK_I, 0);
                seek(seek);
                break;
            case MediaPlayerConst.PlayerConsts.Cmds.CMD_SET_VOL:
                int vol = intent.getIntExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_VOLUME_I, 0);
                setVolume(vol);
                break;
            case MediaPlayerConst.PlayerConsts.Cmds.CMD_UPDATE_STATUS:
                updateStatus();
                break;
            default:
                break;
        }

        return START_STICKY;
    }

    public boolean preparePlayer () {
        if (TextUtils.isEmpty(sourcePath)) {
            state = PlayerState.UNINITED;
            onStateError("************ source path can not be null **************");
            return false;
        }

        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(sourcePath);
            mediaPlayer.prepareAsync();
            state = PlayerState.PREPARING;
            onStatusChanged(MediaPlayerConst.BroadCastConsts.States.PREPARING);
            return true;
        } catch (IOException e) {
            onStateError("************ exception while setDataSource or prepareAsync **************");
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void stop() {
        if (state != PlayerState.UNINITED && !TextUtils.isEmpty(sourcePath)) {
            mediaPlayer.stop();
            onStatusChanged(MediaPlayerConst.BroadCastConsts.States.STOP);
            if (trackInfos != null) {
                mediaNotifier.sendNotification(trackInfos.getString(MediaPlayerConst.PlayerConsts.Keys.NAME),
                        trackInfos.getString(MediaPlayerConst.PlayerConsts.Keys.SINGER),
                        trackInfos.getString(MediaPlayerConst.PlayerConsts.Keys.PIC_URL),
                        false);
            }
        }
        state = PlayerState.STOP;
    }

    @Override
    public void seek(int pos) {
        if (state != PlayerState.UNINITED) {
            mediaPlayer.seekTo(pos);
            mediaPlayer.start();
            state = PlayerState.PLAYING;

            onStatusChanged(MediaPlayerConst.BroadCastConsts.States.SEEK);

        }
    }

    @Override
    public void play(String path, Bundle extras) {
        if (TextUtils.isEmpty(path)) {
            onStateError("error : data source is empty");
            return;
        }
        if (isPlaying() && path.equals(sourcePath)) {
            seek(0);
            return;
        }

        trackInfos = extras;
        LogUtils.d("yue.gan", ""+extras);
        sourcePath = path;
        startAfterPrepare = true;
        preparePlayer();
    }

    @Override
    public void playOrPause() {
        if (state == PlayerState.PLAYING) {
            //暂停
            mediaPlayer.pause();
            state = PlayerState.PAUSE;

            onStatusChanged(MediaPlayerConst.BroadCastConsts.States.PAUSE);
            if (trackInfos != null) {
                mediaNotifier.sendNotification(trackInfos.getString(MediaPlayerConst.PlayerConsts.Keys.NAME),
                        trackInfos.getString(MediaPlayerConst.PlayerConsts.Keys.SINGER),
                        trackInfos.getString(MediaPlayerConst.PlayerConsts.Keys.PIC_URL),
                        false);
            }
        } else if (state == PlayerState.PAUSE
                || state == PlayerState.STOP
                || state == PlayerState.PREPARED) {
            //播放
            audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            mediaPlayer.start();
            state = PlayerState.PLAYING;

            onStatusChanged(MediaPlayerConst.BroadCastConsts.States.PLAY);
            if (trackInfos != null) {
                mediaNotifier.sendNotification(trackInfos.getString(MediaPlayerConst.PlayerConsts.Keys.NAME),
                        trackInfos.getString(MediaPlayerConst.PlayerConsts.Keys.SINGER),
                        trackInfos.getString(MediaPlayerConst.PlayerConsts.Keys.PIC_URL),
                        true);
            }
        } else if (state == PlayerState.PREPARING){
            //正在prepare
            startAfterPrepare = true;
        } else if (state == PlayerState.UNINITED) {
            //初始化
            startAfterPrepare = true;
            preparePlayer();
        }
    }

    @Override
    public void playOnly() {
        if (state == PlayerState.UNINITED) return;
        if (state == PlayerState.PREPARING) {
            startAfterPrepare = true;
            return;
        }
        if (mediaPlayer != null && !isPlaying()) {
            mediaPlayer.start();
            state = PlayerState.PLAYING;
            onStatusChanged(MediaPlayerConst.BroadCastConsts.States.PLAY);

            if (trackInfos != null) {
                mediaNotifier.sendNotification(trackInfos.getString(MediaPlayerConst.PlayerConsts.Keys.NAME),
                        trackInfos.getString(MediaPlayerConst.PlayerConsts.Keys.SINGER),
                        trackInfos.getString(MediaPlayerConst.PlayerConsts.Keys.PIC_URL),
                        true);
            }
        }
    }

    public void pauseOnly () {
        if (state == PlayerState.UNINITED) return;
        if (state == PlayerState.PREPARING) {
            startAfterPrepare = false;
            return;
        }
        if (mediaPlayer != null && isPlaying()) {
            mediaPlayer.pause();
            state = PlayerState.PAUSE;
            onStatusChanged(MediaPlayerConst.BroadCastConsts.States.PAUSE);

            if (trackInfos != null) {
                mediaNotifier.sendNotification(trackInfos.getString(MediaPlayerConst.PlayerConsts.Keys.NAME),
                        trackInfos.getString(MediaPlayerConst.PlayerConsts.Keys.SINGER),
                        trackInfos.getString(MediaPlayerConst.PlayerConsts.Keys.PIC_URL),
                        false);
            }
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && state == PlayerState.PLAYING && mediaPlayer.isPlaying();
    }

    @Override
    public void setVolume(int volume) {
        float maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int vol = (int) (volume * maxVol / 100f);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0);
    }

    @Override
    public void updateStatus() {
        onStatusChanged(MediaPlayerConst.BroadCastConsts.States.SEEK);
    }

    public int getVolume() {
        float maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float vol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        return (int) (vol / maxVol * 100);
    }

    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener
            = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (mediaPlayer == null) return;
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if (isPlaying()) {
                        pauseOnly();
                        onStatusChanged(MediaPlayerConst.BroadCastConsts.States.PAUSE);
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    mediaPlayer.setVolume(0.5f, 0.5f);
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    mediaPlayer.setVolume(1f, 1f);
                    break;
            }
        }
    };

    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            state = PlayerState.PREPARED;
            if (startAfterPrepare) {
                audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                mediaPlayer.start();
                state = PlayerState.PLAYING;
                startAfterPrepare = false;
                onStatusChanged(MediaPlayerConst.BroadCastConsts.States.PLAY);

                if (trackInfos != null) {
                    mediaNotifier.sendNotification(trackInfos.getString(MediaPlayerConst.PlayerConsts.Keys.NAME),
                            trackInfos.getString(MediaPlayerConst.PlayerConsts.Keys.SINGER),
                            trackInfos.getString(MediaPlayerConst.PlayerConsts.Keys.PIC_URL),
                            isPlaying());
                }
            }
        }
    };

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            state = PlayerState.PAUSE;

            onStatusChanged(MediaPlayerConst.BroadCastConsts.States.COMPLETE);

        }
    };

    private long lastErrorTime;
    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            state = PlayerState.UNINITED;
            long currentErrorTime = SystemClock.currentThreadTimeMillis();
            long timeInterval = currentErrorTime - lastErrorTime;
            lastErrorTime = currentErrorTime;

            /** 此处设置error回调时间，为了防止一直下一首无限死循环 */
            if (timeInterval > 2000) {
                onStateError("play " + sourcePath + "occor error --- what:" + what + " extra:" + extra);
            }

            return true;
        }
    };

    private MediaStatus getStatus () {
        MediaStatus status = new MediaStatus();
        status.sourcePath = sourcePath;
        if (state == PlayerState.UNINITED || state == PlayerState.PREPARING || state == PlayerState.STOP)  {
            status.duration = 0;
            status.currentTime = 0;
        } else {
            status.duration = mediaPlayer.getDuration();
            status.currentTime = mediaPlayer.getCurrentPosition();
        }
        if (state == PlayerState.PREPARING) {
            status.isPlaying = 2;
        } else {
            status.isPlaying = isPlaying()?1:0;
        }
        status.volume = getVolume();
        return status;
    }

    private void onStatusChanged (int state) {
        Intent intent = new Intent(ACTION_PLAYER_STATUS_CHANGED);
        MediaStatus status = getStatus();

        //android 5.x 系统中，complete事件中获取到的current position有误
        if (state == MediaPlayerConst.BroadCastConsts.States.COMPLETE) status.currentTime = status.duration;
        intent.putExtra(MediaPlayerConst.BroadCastConsts.Keys.KEY_STATE_I, state);
        intent.putExtra(MediaPlayerConst.BroadCastConsts.Keys.KEY_MEDIA_STATUS_O, status);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        sendBroadcast(intent);
    }

    private void onStateError (String errorMsg) {
        Intent intent = new Intent(ACTION_PLAYER_STATUS_CHANGED);
        intent.putExtra(MediaPlayerConst.BroadCastConsts.Keys.KEY_STATE_I,
                MediaPlayerConst.BroadCastConsts.States.ERROR);
        intent.putExtra(MediaPlayerConst.BroadCastConsts.Keys.KEY_MEDIA_STATUS_O, getStatus());
        intent.putExtra(MediaPlayerConst.BroadCastConsts.Keys.KEY_ERROR_MSG, errorMsg);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        mediaPlayer = null;
    }

}
