package com.gy.utils.audio.mediaplayer;

import android.os.Binder;

import com.gy.utils.audio.IAudioPlayer;
import com.gy.utils.audio.Playlist;

/**
 * Created by ganyu on 2016/7/20.
 *
 */
public class MediaPlayerServiceBinder extends Binder implements IAudioPlayer{

    private MediaPlayerService service;

    public MediaPlayerServiceBinder (MediaPlayerService service) {
        this.service = service;
    }

    @Override
    public boolean play(Playlist playlist) {
        return service.play(playlist);
    }

    @Override
    public boolean stop() {
        return service.stop();
    }

    @Override
    public boolean playOrPause() {
        return service.playOrPause();
    }

    @Override
    public boolean seek(int pos) {
        return service.seek(pos);
    }

    @Override
    public int getPosition() {
        return service.getPosition();
    }

    @Override
    public boolean isPlaying() {
        return service.isPlaying();
    }

    @Override
    public void setMode(int mode) {
        service.setMode(mode);
    }

    @Override
    public int getMode() {
        return service.getMode();
    }

    @Override
    public Playlist getPlaylist() {
        return service.getPlaylist();
    }
}
