package com.gy.utils.audio.mediaplayer;

/**
 * Created by yue.gan on 2016/10/10.
 *
 */
public interface OnMediaListener {
    void onPlay(MediaStatus mediaStatus);
    void onPause(MediaStatus mediaStatus);
    void onStop(MediaStatus mediaStatus);
    void onSeek(MediaStatus mediaStatus);
    void onCompelete(MediaStatus mediaStatus);
    void onError(MediaStatus mediaStatus, String errorMsg);
    void onPreparing(MediaStatus mediaStatus);
}
