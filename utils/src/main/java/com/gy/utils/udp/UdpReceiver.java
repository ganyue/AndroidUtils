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
    private UdpMessageProcessor udpMessageProcessor;

    public UdpReceiver (int port) {
        try {
            mSocket = new DatagramSocket(null);
            mSocket.setReuseAddress(true);
            mSocket.bind(new InetSocketAddress(port));
            udpMessageProcessor = new UdpMessageProcessor();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public UdpReceiver(DatagramSocket socket) {
        super();
        mSocket = socket;
        udpMessageProcessor = new UdpMessageProcessor();
    }

    private UdpReceiver(){}

    public void setOnReceiveListener (UdpMessageProcessor.OnReceiveListener listener) {
        udpMessageProcessor.setOnReceiveListener(listener);
    }

    @Override
    public void run() {
        if (mSocket == null) {
            return;
        }

        udpMessageProcessor.start();

        isRun = true;
        byte[] buff = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buff, 1024);
        while (isRun) {
            try {
                mSocket.receive(packet);
                udpMessageProcessor.onReceive(packet.getData(), packet.getOffset(), packet.getLength(),
                        packet.getAddress().getHostName(), packet.getPort());
            } catch (IOException e) {
                udpMessageProcessor.onReceiveError(e);
            }
        }
    }

    public void release() {
        isRun = false;
        interrupt();
        if (mSocket != null && !mSocket.isClosed()) {
            mSocket.disconnect();
            mSocket.close();
        }
        mSocket = null;

        udpMessageProcessor.release();
    }

}
