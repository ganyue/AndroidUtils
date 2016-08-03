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
    public boolean isBinderAlive() {
        return service != null;
    }

    @Override
    public boolean initPlaylist(Playlist playlist) {
        return service.initPlaylist(playlist);
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
    public boolean prev() {
        return service.prev();
    }

    @Override
    public boolean next() {
        return service.next();
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
    public void setVolume(int volume) {
        service.setVolume(volume);
    }

    @Override
    public int getVolume() {
        return service.getVolume();
    }

    @Override
    public boolean isAlive() {
        return service.isAlive();
    }

    @Override
    public Playlist getPlaylist() {
        return service.getPlaylist();
    }
}
