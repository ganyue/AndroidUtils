package com.gy.utils.audio.mediaplayer;

/**
 * Created by ganyu on 2016/10/10.
 *
 */
public interface IMediaPlayer {

    void play (String path);
    void playOrPause ();
    void stop ();
    void playOnly ();
    void pauseOnly ();
    void seek(int pos);
    void setVolume(int volume);
    void updateStatus ();
}
