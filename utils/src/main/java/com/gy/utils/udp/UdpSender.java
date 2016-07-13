package com.gy.utils.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * Created by ganyu on 2016/5/20.
 *
 */
public class UdpSender extends Thread {
    private DatagramSocket mSocket;
    private boolean isRun;
    private UdpMessageQueue mMessage;
    private OnSendListener onSendListener;
    private final Object mLock = new Object();

    public UdpSender (DatagramSocket socket) {
        mSocket = socket;
        mMessage = new UdpMessageQueue();
    }

    public UdpSender (int localPort) {
        try {
            mSocket = new DatagramSocket(null);
            mSocket.setReuseAddress(true);
            mSocket.bind(new InetSocketAddress(localPort));
            mMessage = new UdpMessageQueue();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private UdpSender() {
    }

    public void setOnSendListener (OnSendListener listener) {
        onSendListener = listener;
    }

    public void send (String msg, String ip, int port) {
        mMessage.addMessage(new UdpMessage(msg, ip, port));
        synchronized (mLock) {
            mLock.notify();
        }
    }

    @Override
    public void run() {
        if (mSocket == null) {
            return;
        }

        isRun = true;
        while (isRun) {
            UdpMessage msg = mMessage.getMessage();
            try {
                if (msg == null) {
                    synchronized (mLock) {
                        mLock.wait();
                        continue;
                    }
                }

                byte[] data = msg.message.getBytes();
                InetAddress address = InetAddress.getByName(msg.ip);
                DatagramPacket packet = new DatagramPacket(data, data.length, address, msg.port);
                boolean handled = false;
                if (onSendListener != null) {
                    handled = onSendListener.onSendBefore(msg.message, msg.ip, msg.port);
                }

                if (handled) {
                    continue;
                }

                mSocket.send(packet);

                if (onSendListener != null) {
                    onSendListener.onSendSuccess(msg.message, msg.ip, msg.port);
                }
            } catch (InterruptedException e) {
                //just interrupt wait
            } catch (IOException e) {
                onSendListener.onSendFailed(msg.message, msg.ip, msg.port, e);
            }
        }
    }

    public interface OnSendListener {
        boolean onSendBefore(String msg, String dstIp, int dstPort);
        void onSendSuccess(String msg, String dstIp, int dstPort);
        void onSendFailed(String msg, String dstIp, int dstPort, Exception e);
    }

    public void release() {
        isRun = false;
        onSendListener = null;
        interrupt();
        if (mSocket != null && !mSocket.isClosed()) {
            mSocket.disconnect();
            mSocket.close();
        }
        mSocket = null;
    }
}
