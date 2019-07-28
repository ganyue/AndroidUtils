package com.gy.utils.udp;

import android.text.TextUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by yue.gan on 2016/5/20.
 *
 */
public class UdpSender extends Thread {

    private final int MAX_MESSAGE_QUEUE_SIZE = 256;

    private DatagramSocket mSocket;
    private boolean isRun;
    private ArrayBlockingQueue<UdpMessage> mMessage;
    private OnSendListener onSendListener;

    public UdpSender (DatagramSocket socket) {
        mSocket = socket;
        mMessage = new ArrayBlockingQueue<>(MAX_MESSAGE_QUEUE_SIZE);
    }

    public UdpSender (int localPort) {
        try {
            mSocket = new DatagramSocket(null);
            mSocket.setReuseAddress(true);
            mSocket.bind(new InetSocketAddress(localPort));
            mMessage = new ArrayBlockingQueue<>(MAX_MESSAGE_QUEUE_SIZE);
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
        mMessage.offer(new UdpMessage(msg, ip, port));
    }

    @Override
    public void run() {
        if (mSocket == null) {
            return;
        }

        isRun = true;
        UdpMessage msg = null;
        while (isRun) {
            try {
                msg = mMessage.take();
                if (msg == null) {
                    return;
                }

                byte[] data;

                if (!TextUtils.isEmpty(msg.message)) {
                    data = msg.message.getBytes();
                } else if (msg.bMessage != null && msg.bMessage.length > 0){
                    data = msg.bMessage;
                } else {
                    continue;
                }

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
