package com.gy.utils.audio;

import com.gy.utils.audio.AudioPlayerConst;
import com.gy.utils.audio.Playlist;

/**
 * Created by ganyu on 2016/9/22.
 *
 */
public interface OnAudioListener {
    void onPlay (AudioPlayerConst.PlayerType playerType, Playlist playlist);
    void onPause (AudioPlayerConst.PlayerType playerType, Playlist playlist);
    void onStop (AudioPlayerConst.PlayerType playerType, Playlist playlist);
    void onSeek (AudioPlayerConst.PlayerType playerType, int pos);
    void onModeChanged (AudioPlayerConst.PlayerType playerType, int mode);
    void onVolumeChanged (int volume);
    void onComplete (AudioPlayerConst.PlayerType playerType, Playlist playlist);
    void onError (AudioPlayerConst.PlayerType playerType, Playlist playlist, int extra);
    boolean onDied (AudioPlayerConst.PlayerType playerType);
}
