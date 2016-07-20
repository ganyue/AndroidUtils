package com.gy.utils.audio;

/**
 * Created by ganyu on 2016/7/19.
 *
 */
public interface IAudioPlayer {
    boolean play (Playlist playlist);
    boolean stop ();
    boolean playOrPause ();
    boolean seek (int pos);
    int getPosition ();
    boolean isPlaying ();
    void setMode (int mode);
    int getMode ();
    Playlist getPlaylist();
}
