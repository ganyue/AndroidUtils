package com.gy.utils.audio.mpd;

/**
 * Created by ganyu on 2016/10/11.
 *
 */
public interface OnMpdListener {
    void onConnectSuccess (String ip);
    void onConnectFail (String ip);
    void onReconnect (String ip);
    void onResponse (String cmd, Object obj);
}
