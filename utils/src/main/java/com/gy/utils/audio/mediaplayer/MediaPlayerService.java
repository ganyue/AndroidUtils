package com.gy.utils.audio.mediaplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;

import java.io.IOException;

/**
 * Created by ganyu on 2016/7/19.
 *
 */
public class MediaPlayerService extends Service implements IMediaPlayer {

    public static final String ACTION_PLAYER_STATUS_CHANGED = MediaPlayerService.class.getName();

    enum  PlayerState {
        UNINITED,
        PREPARING,
        PREPARED,
        PLAYING,
        PAUSE,
        STOP,
    }
    
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private PlayerState state;
    private boolean startAfterPrepare;
    private String sourcePath = "";
    
    
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

        int cmd = intent.getIntExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_CMD_I, MediaPlayerConst.PlayerConsts.Cmds.CMD_UNKNOWN);

        switch (cmd) {
            case MediaPlayerConst.PlayerConsts.Cmds.CMD_PLAY:
                String path = intent.getStringExtra(MediaPlayerConst.PlayerConsts.Keys.KEY_SOURCE_PATH);
                play(path);
                break;
            case MediaPlayerConst.PlayerConsts.Cmds.CMD_STOP:
                stop();
                break;
            case MediaPlayerConst.PlayerConsts.Cmds.CMD_PLAY_OR_PAUSE:
                playOrPause();
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
        if (sourcePath == null) {
            state = PlayerState.UNINITED;
            return false;
        }

        if (TextUtils.isEmpty(sourcePath)) {
            state = PlayerState.UNINITED;
            return false;
        }

        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(sourcePath);
            mediaPlayer.prepareAsync();
            state = PlayerState.PREPARING;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void stop() {
        if (state != PlayerState.UNINITED) {
            mediaPlayer.stop();

            onStatusChanged(MediaPlayerConst.BroadCastConsts.States.STOP);

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
    public void play(String path) {
        if (TextUtils.isEmpty(path)) {
            onStateError("error : data source is empty");
            return;
        }
        if (isPlaying() && path.equals(sourcePath)) {
            seek(0);
            return;
        }
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

        } else if (state == PlayerState.PAUSE
                || state == PlayerState.STOP
                || state == PlayerState.PREPARED) {
            //播放
            audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            mediaPlayer.start();
            state = PlayerState.PLAYING;

            onStatusChanged(MediaPlayerConst.BroadCastConsts.States.PLAY);

        } else if (state == PlayerState.PREPARING){
            //正在prepare
            startAfterPrepare = true;
        } else if (state == PlayerState.UNINITED) {
            //初始化
            startAfterPrepare = true;
            preparePlayer();
        }
    }

    public boolean isPlaying() {
        return state == PlayerState.PLAYING;
    }

    @Override
    public void setVolume(int volume) {
        float maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int vol = (int) (volume * maxVol / 100f);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 1);
    }

    @Override
    public void updateStatus() {

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
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if (isPlaying()) {
                        mediaPlayer.pause();

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

            state = PlayerState.PAUSE;
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
        status.duration = mediaPlayer.getDuration();
        status.currentTime = mediaPlayer.getCurrentPosition();
        status.isPlaying = isPlaying()?1:0;
        status.volume = getVolume();
        return status;
    }

    private void onStatusChanged (int state) {
        Intent intent = new Intent(ACTION_PLAYER_STATUS_CHANGED);
        intent.putExtra(MediaPlayerConst.BroadCastConsts.Keys.KEY_STATE_I, state);
        intent.putExtra(MediaPlayerConst.BroadCastConsts.Keys.KEY_MEDIA_STATUS_O, getStatus());
        sendBroadcast(intent);
    }

    private void onStateError (String errorMsg) {
        Intent intent = new Intent(ACTION_PLAYER_STATUS_CHANGED);
        intent.putExtra(MediaPlayerConst.BroadCastConsts.Keys.KEY_STATE_I,
                MediaPlayerConst.BroadCastConsts.States.ERROR);
        intent.putExtra(MediaPlayerConst.BroadCastConsts.Keys.KEY_MEDIA_STATUS_O, getStatus());
        intent.putExtra(MediaPlayerConst.BroadCastConsts.Keys.KEY_ERROR_MSG, errorMsg);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        mediaPlayer = null;
    }
}
