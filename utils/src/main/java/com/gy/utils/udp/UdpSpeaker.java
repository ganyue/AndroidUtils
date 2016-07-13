package com.gy.utils.udp;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganyu on 2016/5/19.
 *
 */
public class UdpSpeaker implements UdpSender.OnSendListener, UdpReceiver.OnReceiveListener{
    private DatagramSocket mSocket;
    private int mLocalPort;
    private boolean isInited;

    private List<UdpSpeakerCallback> mCallbacks;
    private UdpSender mSender;
    private UdpReceiver mReceiver;

    public UdpSpeaker (int localPort) {
        mLocalPort = localPort;
        init();
    }

    private void init () {
        if (isInited) {
            return;
        }
        try {
            mCallbacks = new ArrayList<>();
            mSocket = new DatagramSocket(null);
            mSocket.setReuseAddress(true);
            mSocket.bind(new InetSocketAddress(mLocalPort));
            mSender = new UdpSender(mSocket);
            mReceiver = new UdpReceiver(mSocket);
            mSender.setOnSendListener(this);
            mReceiver.setOnReceiveListener(this);
            mSender.start();
            mReceiver.start();
            isInited = true;
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void addCallback (UdpSpeakerCallback callback) {
        if (!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    public void removeCallback (UdpSpeakerCallback callback) {
        if (mCallbacks.contains(callback)) {
            mCallbacks.remove(callback);
        }
    }

    public void send (String msg, String ip, int port) {
        mSender.send(msg, ip, port);
    }

    @Override
    public void onReceive(byte[] buff, int offset, int len, String fromIp, int fromPort) {
        if (mCallbacks == null) {
            return;
        }
        String msg = new String (buff, offset, len);
        for (UdpSpeakerCallback callback : mCallbacks) {
            callback.onReceive(msg, fromIp, fromPort);
        }
    }

    @Override
    public void onReceiveError(Exception e) {
        if (mCallbacks == null) {
            return;
        }
        for (UdpSpeakerCallback callback : mCallbacks) {
            callback.onReceiveError(e);
        }
    }

    @Override
    public boolean onSendBefore(String msg, String dstIp, int dstPort) {
        if (mCallbacks == null) {
            return false;
        }
        boolean handled = false;
        for (UdpSpeakerCallback callback : mCallbacks) {
            if (callback.onSendBefore(msg, dstIp, dstPort)) {
                handled = true;
            }
        }

        return handled;
    }

    @Override
    public void onSendSuccess(String msg, String dstIp, int dstPort) {
        if (mCallbacks == null) {
            return;
        }
        for (UdpSpeakerCallback callback : mCallbacks) {
            callback.onSendSuccess(msg, dstIp, dstPort);
        }
    }

    @Override
    public void onSendFailed(String msg, String dstIp, int dstPort, Exception e) {
        if (mCallbacks == null) {
            return;
        }
        for (UdpSpeakerCallback callback : mCallbacks) {
            callback.onSendFailed(msg, dstIp, dstPort, e);
        }
    }

    public void release() {
        if (mSocket != null && !mSocket.isClosed()) {
            mSocket.disconnect();
            mSocket.close();
        }
        mSocket = null;
        isInited = false;
        mSender.release();
        mReceiver.release();
    }
}
