package com.gy.utils.tcp;

import android.text.TextUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by sam_gan on 2016/6/3.
 *
 */
public class TcpSender extends Thread {

    private OutputStream mSockOutStream;
    private ArrayBlockingQueue<TcpMessage> mMessage;
    private TcpSenderListener mTcpSenderListener;
    private WeakReference<Socket> mSocket;
    private boolean isRun;
    private boolean enableHart = true;

    public TcpSender(Socket socket) {
        mMessage = new ArrayBlockingQueue<TcpMessage>(64);
        try {
            mSockOutStream = socket.getOutputStream();
            mSocket = new WeakReference<Socket>(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enableHart (boolean enableHart) {
        this.enableHart = enableHart;
    }

    public void setTcpSenderListener (TcpSenderListener listener) {
        mTcpSenderListener = listener;
    }

    public void send(String msg) {
        if (mMessage.contains(msg)) {
            return;
        }
        mMessage.offer(new TcpMessage("", 0, msg));
    }

    @Override
    public void run() {
        if (mSockOutStream == null) {
            return;
        }
        isRun = true;

        TcpMessage message = null;
        while (isRun) {
            try {
                //3秒后如果没有消息发送则发送一个简单消息来确认tcp连接是否活着
                message = mMessage.poll(3000, TimeUnit.MILLISECONDS);
                if (message == null || TextUtils.isEmpty(message.message)) {
                    if (enableHart) {
                        mSocket.get().sendUrgentData(0xFF);
                    }
                    continue;
                }
                boolean handled = false;
                if (mTcpSenderListener != null) {
                    handled = mTcpSenderListener.onSendBefore(message.message);
                }

                if (handled) {
                    continue;
                }

                byte[] data = message.message.getBytes();
                mSockOutStream.write(data, 0, data.length);
                mSockOutStream.flush();

                if (mTcpSenderListener != null) {
                    mTcpSenderListener.onSendSuccess(message.message);
                }
            } catch (Exception e) {
                e.printStackTrace();

                if (mTcpSenderListener != null) {
                    mTcpSenderListener.onSendFailed(message == null ? null : message.message, e);
                }
            }
        }
    }

    public void release () {
        isRun = false;
        mMessage.clear();
        interrupt();
        if (mSockOutStream != null) {
            try {
                mSockOutStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mSockOutStream = null;
        mTcpSenderListener = null;
    }

    public interface TcpSenderListener {
        boolean onSendBefore (String msg);
        void onSendSuccess (String msg);
        void onSendFailed (String msg, Exception e);
    }
}
