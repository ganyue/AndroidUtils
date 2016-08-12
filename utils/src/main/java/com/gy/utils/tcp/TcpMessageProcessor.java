package com.gy.utils.tcp;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by ganyu on 2016/7/25.
 *
 */
public class TcpMessageProcessor extends Thread{

    private ArrayBlockingQueue<TcpMessage> messages;
    private TcpReceiveListener onReceiveListener;
    private boolean isRun;

    public TcpMessageProcessor() {
        messages = new ArrayBlockingQueue<>(64);
    }

    public void onReceive (byte[] buff, int offset, int len) {
        messages.offer(new TcpMessage(buff, offset, len));
    }

    public void onReceiveError (Exception e) {
        messages.offer(new TcpMessage(e));
    }

    public void setOnReceiveListener (TcpReceiveListener listener) {
        onReceiveListener = listener;
    }

    @Override
    public void run() {
        isRun = true;
        while (isRun) {
            try {
                TcpMessage msg = messages.take();

                if (msg == null ||  onReceiveListener == null) {
                    continue;
                }

                if (msg.bMessage != null && msg.bMessage.length > 0) {
                    onReceiveListener.onReceive(msg.bMessage, 0, msg.bMessage.length);
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
        onReceiveListener = null;
        interrupt();
    }

    public interface TcpReceiveListener {
        void onReceive(byte[] buff, int offset , int len);
        void onReceiveError(Exception e);
    }
}
