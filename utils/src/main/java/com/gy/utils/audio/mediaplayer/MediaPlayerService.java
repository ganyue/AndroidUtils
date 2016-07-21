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
            onStateChanged();
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
        state = AudioPlayerConst.PlayerState.STOPED;
        onStateChanged();
        return true;
    }

    @Override
    public boolean playOrPause() {
        if (state == AudioPlayerConst.PlayerState.PLAYING) {
            mediaPlayer.pause();
            state = AudioPlayerConst.PlayerState.PAUSE;
        } else if (state == AudioPlayerConst.PlayerState.PAUSE
                || state == AudioPlayerConst.PlayerState.STOPED
                || state == AudioPlayerConst.PlayerState.PREPARED) {
            audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            mediaPlayer.start();
            state = AudioPlayerConst.PlayerState.PLAYING;
        } else if (state == AudioPlayerConst.PlayerState.PREPARING){
            startAfterPrepare = true;
        } else if (state == AudioPlayerConst.PlayerState.UNINITED) {
            startAfterPrepare = true;
            preparePlayer();
        }
        onStateChanged();
        return true;
    }

    @Override
    public boolean prev() {
        if (playlist == null) return false;
        playlist.prev();
        startAfterPrepare = true;
        return preparePlayer();
    }

    @Override
    public boolean next() {
        if (playlist == null) return false;
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
        onStateChanged();
    }

    @Override
    public int getMode() {
        if (playlist == null) return 0;
        return playlist.getMode();
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

            playlist.next();
            startAfterPrepare = true;
            preparePlayer();
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

            long currentErrorTime = SystemClock.currentThreadTimeMillis();
            long timeInterval = currentErrorTime - lastErrorTime;
            lastErrorTime = currentErrorTime;
            if (timeInterval < 5000) {
                onStateChanged();
                return true;
            }
            playlist.next();
            startAfterPrepare = true;
            return preparePlayer();
        }
    };
}
