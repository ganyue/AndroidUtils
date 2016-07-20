package com.gy.utils.audio.mediaplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.text.TextUtils;

import com.gy.utils.audio.IAudioPlayer;
import com.gy.utils.audio.Playlist;
import com.gy.utils.audio.Track;

import java.io.IOException;

/**
 * Created by ganyu on 2016/7/19.
 *
 */
public class MediaPlayerService extends Service implements IAudioPlayer {

    public static class Keys {
        public static final String KEY_CMD = "cmd";
        public static final String KEY_PLAYLIST = "playlist";
        public static final String KEY_SEEK = "seek";
    }

    public static class CMD {
        public static final int CMD_UNKNOWN = 0;
        public static final int CMD_PLAY = 1;
        public static final int CMD_STOP = 2;
        public static final int CMD_PLAY_OR_PAUSE = 3;
        public static final int CMD_SEEK = 4;
        public static final int CMD_GET_POS = 5;
    }

    private Playlist playlist;
    private MediaPlayer mediaPlayer;
    private MediaPlayerServiceBinder binder;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(onCompletionListener);
        mediaPlayer.setOnErrorListener(onErrorListener);
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
        int cmd = intent.getIntExtra(Keys.KEY_CMD, CMD.CMD_UNKNOWN);

        switch (cmd) {
            case CMD.CMD_PLAY:
                playlist = intent.getParcelableExtra(Keys.KEY_PLAYLIST);
                play(playlist);
                break;
            case CMD.CMD_STOP:
                stop();
                break;
            case CMD.CMD_PLAY_OR_PAUSE:
                playOrPause();
                break;
            case CMD.CMD_SEEK:
                int seek = intent.getIntExtra(Keys.KEY_SEEK, 0);
                seek(seek);
                break;
            case CMD.CMD_GET_POS:
                getPosition();
                break;
            //TODO
            default:
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void onStateChanged () {
        //TODO
    }

    @Override
    public boolean play(Playlist playlist) {
        Track track = playlist.getCurrentTrack();
        String dataSource = (track == null)? "" : (TextUtils.isEmpty(track.localPath) ?
                (TextUtils.isEmpty(track.mp3Url) ? "" : track.mp3Url) : track.localPath);

        if (TextUtils.isEmpty(dataSource)) {
            return false;
        }

        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(dataSource);
            mediaPlayer.prepare();
            mediaPlayer.start();
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
        onStateChanged();
        return true;
    }

    @Override
    public boolean playOrPause() {
        if (isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
        onStateChanged();
        return true;
    }

    @Override
    public boolean seek(int pos) {
        mediaPlayer.seekTo(pos);
        onStateChanged();
        return false;
    }

    @Override
    public int getPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public void setMode(int mode) {
        onStateChanged();
        playlist.setMode(mode);
    }

    @Override
    public int getMode() {
        return playlist.getMode();
    }

    @Override
    public Playlist getPlaylist() {
        return playlist;
    }

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            //TODO
            onStateChanged();
        }
    };

    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            //TODO
            onStateChanged();
            return true;
        }
    };
}
