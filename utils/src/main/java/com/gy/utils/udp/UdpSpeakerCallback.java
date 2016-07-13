package com.gy.utils.udp;

/**
 * Created by sam_gan on 2016/5/20.
 *
 */
public interface UdpSpeakerCallback {
    boolean onSendBefore(String msg, String ip, int port);
    void onSendSuccess(String msg, String ip, int port);
    void onSendFailed(String msg, String ip, int port, Exception e);
    void onReceive(String msg, String ip, int port);
    void onReceiveError(Exception e);
}
