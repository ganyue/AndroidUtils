package com.gy.utils.audio.mediaplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;

import com.gy.utils.audio.AudioPlayerConst;
import com.gy.utils.audio.AudioUtils;
import com.gy.utils.audio.IAudioPlayer;
import com.gy.utils.audio.Playlist;
import com.gy.utils.audio.Track;

import java.io.IOException;

/**
 * Created by ganyu on 2016/7/19.
 *
 */
public class MediaPlayerService extends Service implements IAudioPlayer {

    private Playlist playlist;
    private MediaPlayer mediaPlayer;
    private MediaPlayerServiceBinder binder;
    private AudioManager audioManager;
    private AudioPlayerConst.PlayerState state;
    private int operation;
    private boolean startAfterPrepare;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(onPreparedListener);
        mediaPlayer.setOnCompletionListener(onCompletionListener);
        mediaPlayer.setOnErrorListener(onErrorListener);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        state = AudioPlayerConst.PlayerState.UNINITED;
        startAfterPrepare = false;
        operation = AudioPlayerConst.PlayerConsts.Operation.NONE;
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (binder == null) {
            binder = new MediaPlayerServiceBinder(this);
        }
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_STICKY;
        int cmd = intent.getIntExtra(AudioPlayerConst.PlayerConsts.Keys.KEY_CMD_I, AudioPlayerConst.PlayerConsts.Cmds.CMD_UNKNOWN);

        switch (cmd) {
            case AudioPlayerConst.PlayerConsts.Cmds.CMD_PLAY:
                playlist = intent.getParcelableExtra(AudioPlayerConst.PlayerConsts.Keys.KEY_PLAYLIST_O);
                startAfterPrepare = true;
                initPlaylist(playlist);
                break;
            case AudioPlayerConst.PlayerConsts.Cmds.CMD_STOP:
                stop();
                break;
            case AudioPlayerConst.PlayerConsts.Cmds.CMD_PLAY_OR_PAUSE:
                playOrPause();
                break;
            case AudioPlayerConst.PlayerConsts.Cmds.CMD_SEEK:
                int seek = intent.getIntExtra(AudioPlayerConst.PlayerConsts.Keys.KEY_SEEK_I, 0);
                seek(seek);
                break;
            case AudioPlayerConst.PlayerConsts.Cmds.CMD_GET_STATE:
                onStateChanged();
                break;
            case AudioPlayerConst.PlayerConsts.Cmds.CMD_MODE:
                int mode = intent.getIntExtra(AudioPlayerConst.PlayerConsts.Keys.KEY_MODE_I, 0);
                playlist.setMode(mode);
                break;
            default:
                break;
        }

        return START_STICKY;
    }

    private void onStateChanged () {
        Intent intent = new Intent(AudioUtils.ACTION_AUDIO_PLAYER_CALLBACK_RECEIVER);
        intent.putExtra(AudioPlayerConst.PlayerConsts.Keys.KEY_TYPE_I, AudioPlayerConst.PlayerConsts.BCastType.STATE);
        intent.putExtra(AudioPlayerConst.PlayerConsts.Keys.KEY_SENDER_S, MediaPlayerService.class.getSimpleName());
        intent.putExtra(AudioPlayerConst.PlayerConsts.Keys.KEY_PLAYLIST_O, playlist);
        intent.putExtra(AudioPlayerConst.PlayerConsts.Keys.KEY_IS_PLAYING_B, isPlaying());
        intent.putExtra(AudioPlayerConst.PlayerConsts.Keys.KEY_POSITION_I, getPosition());
        intent.putExtra(AudioPlayerConst.PlayerConsts.Keys.KEY_OPERATION_I, operation);
        operation = AudioPlayerConst.PlayerConsts.Operation.NONE;
        sendBroadcast(intent);
    }

    @Override
    public boolean initPlaylist(Playlist playlist) {
        if (playlist.equals(this.playlist) && state != AudioPlayerConst.PlayerState.UNINITED) {
            return true;
        }
        this.playlist = playlist;
        startAfterPrepare = false;
        return preparePlayer();
    }

    public boolean preparePlayer () {
        if (playlist == null) {
            state = AudioPlayerConst.PlayerState.UNINITED;
            return false;
        }
        Track track = playlist.getCurrentTrack();
        String dataSource = (track == null)? "" : (TextUtils.isEmpty(track.localPath) ?
                (TextUtils.isEmpty(track.mp3Url) ? "" : track.mp3Url) : track.localPath);

        if (TextUtils.isEmpty(dataSource)) {
            state = AudioPlayerConst.PlayerState.UNINITED;
            return false;
        }

        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(dataSource);
            mediaPlayer.prepareAsync();
            state = AudioPlayerConst.PlayerState.PREPARING;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean stop() {
        if (getPosition() > 0) {
            mediaPlayer.stop();
        }
        state = AudioPlayerConst.PlayerState.STOP;
        operation = AudioPlayerConst.PlayerConsts.Operation.STOP;
        onStateChanged();
        return true;
    }

    @Override
    public boolean playOrPause() {
        if (state == AudioPlayerConst.PlayerState.PLAYING) {
            //暂停
            mediaPlayer.pause();
            state = AudioPlayerConst.PlayerState.PAUSE;
            operation = AudioPlayerConst.PlayerConsts.Operation.PAUSE;
            onStateChanged();
        } else if (state == AudioPlayerConst.PlayerState.PAUSE
                || state == AudioPlayerConst.PlayerState.STOP) {
            //播放
            audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            mediaPlayer.start();
            state = AudioPlayerConst.PlayerState.PLAYING;
            operation = AudioPlayerConst.PlayerConsts.Operation.PLAY;
            onStateChanged();
        } else if (state == AudioPlayerConst.PlayerState.PREPARED) {
            //开始
            audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            mediaPlayer.start();
            state = AudioPlayerConst.PlayerState.PLAYING;
            operation = AudioPlayerConst.PlayerConsts.Operation.START;
            onStateChanged();
        } else if (state == AudioPlayerConst.PlayerState.PREPARING){
            //正在prepare
            startAfterPrepare = true;
        } else if (state == AudioPlayerConst.PlayerState.UNINITED) {
            //初始化
            operation = AudioPlayerConst.PlayerConsts.Operation.START;
            startAfterPrepare = true;
            preparePlayer();
        }
        return true;
    }

    @Override
    public boolean prev() {
        if (playlist == null) return false;
        operation = AudioPlayerConst.PlayerConsts.Operation.PREV;
        playlist.prev();
        startAfterPrepare = true;
        return preparePlayer();
    }

    @Override
    public boolean next() {
        if (playlist == null) return false;
        operation = AudioPlayerConst.PlayerConsts.Operation.NEXT;
        playlist.next();
        startAfterPrepare = true;
        return preparePlayer();
    }

    @Override
    public boolean seek(int pos) {
        if (state == AudioPlayerConst.PlayerState.UNINITED) {
            return false;
        }
        mediaPlayer.seekTo(pos);
        mediaPlayer.start();
        state = AudioPlayerConst.PlayerState.PLAYING;
        operation = AudioPlayerConst.PlayerConsts.Operation.SEEK;
        onStateChanged();
        return true;
    }

    @Override
    public int getPosition() {
        if (state == AudioPlayerConst.PlayerState.UNINITED) {
            return -1;
        }
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public boolean isPlaying() {
        return state == AudioPlayerConst.PlayerState.PLAYING;
    }

    @Override
    public void setMode(int mode) {
        if (playlist == null) return;
        playlist.setMode(mode);
        operation = AudioPlayerConst.PlayerConsts.Operation.MODE_CHANGED;
        onStateChanged();
    }

    @Override
    public int getMode() {
        if (playlist == null) return 0;
        return playlist.getMode();
    }

    @Override
    public void setVolume(int volume) {
        float maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int vol = (int) (volume * maxVol / 100f);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 1);
    }

    @Override
    public int getVolume() {
        float maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float vol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        return (int) (vol / maxVol * 100);
    }

    @Override
    public boolean isAlive() {
        return mediaPlayer != null;
    }

    @Override
    public Playlist getPlaylist() {
        return playlist;
    }

    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if (isPlaying()) {
                        mediaPlayer.pause();
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
            state = AudioPlayerConst.PlayerState.PREPARED;
            if (startAfterPrepare) {
                audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                mediaPlayer.start();
                state = AudioPlayerConst.PlayerState.PLAYING;
                startAfterPrepare = false;
            }
            onStateChanged();
        }
    };

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Intent intent = new Intent(AudioUtils.ACTION_AUDIO_PLAYER_CALLBACK_RECEIVER);
            intent.putExtra(AudioPlayerConst.PlayerConsts.Keys.KEY_TYPE_I, AudioPlayerConst.PlayerConsts.BCastType.COMPLETE);
            intent.putExtra(AudioPlayerConst.PlayerConsts.Keys.KEY_SENDER_S, MediaPlayerService.class.getSimpleName());
            intent.putExtra(AudioPlayerConst.PlayerConsts.Keys.KEY_PLAYLIST_O, playlist);
            sendBroadcast(intent);

            state = AudioPlayerConst.PlayerState.PAUSE;
            playlist.next();
            startAfterPrepare = true;
            preparePlayer();
            operation = AudioPlayerConst.PlayerConsts.Operation.COMPLETE_AUTO_NEXT;
        }
    };

    private long lastErrorTime;
    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Intent intent = new Intent(AudioUtils.ACTION_AUDIO_PLAYER_CALLBACK_RECEIVER);
            intent.putExtra(AudioPlayerConst.PlayerConsts.Keys.KEY_TYPE_I, AudioPlayerConst.PlayerConsts.BCastType.ERROR);
            intent.putExtra(AudioPlayerConst.PlayerConsts.Keys.KEY_SENDER_S, MediaPlayerService.class.getSimpleName());
            intent.putExtra(AudioPlayerConst.PlayerConsts.Keys.KEY_EXTRA_I, extra);
            sendBroadcast(intent);

            state = AudioPlayerConst.PlayerState.PAUSE;
            long currentErrorTime = SystemClock.currentThreadTimeMillis();
            long timeInterval = currentErrorTime - lastErrorTime;
            lastErrorTime = currentErrorTime;
            if (timeInterval < 5000) {
                onStateChanged();
                return true;
            }
            playlist.next();
            operation = AudioPlayerConst.PlayerConsts.Operation.ERROR_AUTO_NEXT;
            startAfterPrepare = true;
            return preparePlayer();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        mediaPlayer = null;
        binder = null;
        playlist = null;
    }
}
