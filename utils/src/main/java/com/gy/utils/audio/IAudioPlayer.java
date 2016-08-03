package com.gy.utils.audio;

/**
 * Created by ganyu on 2016/7/19.
 *
 */
public interface IAudioPlayer {
    boolean initPlaylist(Playlist playlist);
    boolean stop ();
    boolean playOrPause ();
    boolean prev ();
    boolean next ();
    boolean seek (int pos);
    int getPosition ();
    boolean isPlaying ();
    void setMode (int mode);
    int getMode ();
    void setVolume (int volume);
    int getVolume ();
    boolean isAlive ();
    Playlist getPlaylist();
}
