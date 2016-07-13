package com.gy.utils.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * Created by ganyu on 2016/5/19.
 *
 */
public class UdpReceiver extends Thread {

    private DatagramSocket mSocket;
    private boolean isRun;
    private OnReceiveListener onReceiveListener;

    public UdpReceiver (int port) {
        try {
            mSocket = new DatagramSocket(null);
            mSocket.setReuseAddress(true);
            mSocket.bind(new InetSocketAddress(port));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public UdpReceiver(DatagramSocket socket) {
        super();
        mSocket = socket;
    }

    private UdpReceiver(){}

    public void setOnReceiveListener (OnReceiveListener listener) {
        onReceiveListener = listener;
    }

    @Override
    public void run() {
        if (mSocket == null) {
            return;
        }

        isRun = true;
        byte[] buff = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buff, 1024);
        while (isRun) {
            try {
                mSocket.receive(packet);
                if (onReceiveListener != null) {
                    onReceiveListener.onReceive(packet.getData(), packet.getOffset(), packet.getLength(),
                            packet.getAddress().getHostName(), packet.getPort());
                }
            } catch (IOException e) {
                if (onReceiveListener != null) {
                    onReceiveListener.onReceiveError(e);
                }
            }
        }
    }

    public void release() {
        isRun = false;
        onReceiveListener = null;
        interrupt();
        if (mSocket != null && !mSocket.isClosed()) {
            mSocket.disconnect();
            mSocket.close();
        }
        mSocket = null;
    }

    public interface OnReceiveListener {
        void onReceive(byte[] buff, int offset, int len, String fromIp, int fromPort);
        void onReceiveError(Exception e);
    }
}
