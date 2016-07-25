package com.gy.utils.udp;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by ganyu on 2016/7/25.
 *
 */
public class UdpMessageProcessor extends Thread{

    private ArrayBlockingQueue<UdpMessage> messages;
    private OnReceiveListener onReceiveListener;
    private boolean isRun;

    public UdpMessageProcessor () {
        messages = new ArrayBlockingQueue<>(64);
    }

    public void onReceive (byte[] buff, int offset, int len, String fromIp, int fromPort) {
        byte[] msg = new byte[len];
        System.arraycopy(buff, offset, msg, 0, len);
        messages.offer(new UdpMessage(msg, fromIp, fromPort));
    }

    public void onReceiveError (Exception e) {
        messages.offer(new UdpMessage(e));
    }

    public void setOnReceiveListener (OnReceiveListener listener) {
        onReceiveListener = listener;
    }

    @Override
    public void run() {
        isRun = true;
        while (isRun) {
            try {
                UdpMessage msg = messages.take();

                if (msg == null ||  onReceiveListener == null) {
                    continue;
                }

                if (msg.bMessage != null && msg.bMessage.length > 0) {
                    onReceiveListener.onReceive(msg.bMessage, msg.ip, msg.port);
                } else if (msg.exception != null) {
                    onReceiveListener.onReceiveError(msg.exception);
                }

            } catch (Exception e) {
                //nothing to do
            }
        }
    }

    public void release() {
        isRun = false;
        messages.clear();
        messages = null;
        onReceiveListener = null;
        interrupt();
    }

    public interface OnReceiveListener {
        void onReceive(byte[] buff, String fromIp, int fromPort);
        void onReceiveError(Exception e);
    }
}
