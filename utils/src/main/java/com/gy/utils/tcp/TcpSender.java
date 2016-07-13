package com.gy.utils.tcp;

import android.text.TextUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by sam_gan on 2016/6/3.
 *
 */
public class TcpSender extends Thread {

    private OutputStream mSockOutStream;
    private TcpMessageQueue mMessage;
    private final Object mLock = new Object();
    private TcpSenderListener mTcpSenderListener;
    private boolean isRun;

    public TcpSender(Socket socket) {
        mMessage = new TcpMessageQueue();
        try {
            mSockOutStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTcpSenderListener (TcpSenderListener listener) {
        mTcpSenderListener = listener;
    }

    public void send(String msg) {
        mMessage.addMessage(new TcpMessage("", 0, msg));
        synchronized (mLock) {
            mLock.notify();
        }
    }

    @Override
    public void run() {
        if (mSockOutStream == null) {
            return;
        }
        isRun = true;

        while (isRun) {
            TcpMessage message = mMessage.getMessage();
            try {
                if (message == null || TextUtils.isEmpty(message.message)) {
                    synchronized (mLock) {
                        mLock.wait();
                        continue;
                    }
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
        mTcpSenderListener = null;
        interrupt();
        if (mSockOutStream != null) {
            try {
                mSockOutStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mSockOutStream = null;
    }

    public interface TcpSenderListener {
        boolean onSendBefore (String msg);
        void onSendSuccess (String msg);
        void onSendFailed (String msg, Exception e);
    }
}
