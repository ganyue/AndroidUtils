package com.gy.utils.udp;

import android.util.SparseArray;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yue.gan on 2016/5/19.
 *
 * use UdpSpeaker.get(port) method to get a speaker
 */
public class UdpSpeaker implements UdpSender.OnSendListener, UdpMessageProcessor.OnReceiveListener {

    private static SparseArray<UdpSpeaker> mSpeakers;

    private DatagramSocket mSocket;
    private int mLocalPort;
    private boolean isInited;

    private List<UdpSpeakerCallback> mCallbacks;
    private UdpSender mSender;
    private UdpReceiver mReceiver;

    public static UdpSpeaker get (int localPort) {
        if (mSpeakers == null || mSpeakers.indexOfKey(localPort) < 0) {
            return new UdpSpeaker(localPort);
        }

        return mSpeakers.get(localPort, new UdpSpeaker(localPort));
    }

    private UdpSpeaker (){}

    private UdpSpeaker (int localPort) {
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
            if (mSpeakers == null) {
                mSpeakers = new SparseArray<>();
            }
            mSpeakers.put(mLocalPort, this);
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
    public void onReceive(byte[] buff, String fromIp, int fromPort) {
        if (mCallbacks == null) {
            return;
        }
        String msg = new String (buff);
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
