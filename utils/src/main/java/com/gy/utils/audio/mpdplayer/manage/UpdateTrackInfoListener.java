package com.gy.utils.audio.mpdplayer.manage;

import com.gy.utils.audio.mpdplayer.mpd.MPDStatus;
import com.gy.utils.audio.mpdplayer.mpd.Music;

/**
 * Created by lwk on 2016/2/24.
 */
public interface UpdateTrackInfoListener {
    public void onSuccess(MPDStatus status, Music music);

    public void onError(String msg);
}
