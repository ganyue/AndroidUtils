package com.gy.utils.tcp;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
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
    private ArrayBlockingQueue<SendItem> mMessage;
    private TcpSenderListener mTcpSenderListener;
    private WeakReference<Socket> mSocket;
    private boolean isRun;
    private boolean enableHart = true;

    public TcpSender(Socket socket) {
        mMessage = new ArrayBlockingQueue<>(64);
        try {
            mSockOutStream = socket.getOutputStream();
            mSocket = new WeakReference<>(socket);
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

    public void sendString(String unique, String msg) {
        if (TextUtils.isEmpty(msg)) return;
        mMessage.offer(SendItem.getStrItem(unique, msg));
    }

    public void sendStream (String unique, InputStream fIn) {
        if (fIn == null) return;
        mMessage.offer(SendItem.getStreamItem(unique, fIn));
    }

    @Override
    public void run() {
        if (mSockOutStream == null) {
            return;
        }
        isRun = true;

        SendItem item = null;
        while (isRun) {
            try {
                //3秒后如果没有消息发送则发送一个简单消息来确认tcp连接是否活着
                item = mMessage.poll(3000, TimeUnit.MILLISECONDS);
                if (item == null) {
                    if (enableHart) {
                        mSocket.get().sendUrgentData(0xFF);
                    }
                    continue;
                }
                boolean handled = false;
                if (mTcpSenderListener != null) {
                    handled = mTcpSenderListener.onSendBefore(item);
                }

                if (handled) {
                    continue;
                }

                if (item.type == SendItem.Type.STRING) {
                    byte[] strData = item.msg.getBytes();
                    mSockOutStream.write(strData, 0, strData.length);
                } else if (item.type == SendItem.Type.STREAM) {
                    byte[] data = new byte[1024];
                    int len;
                    while ((len = item.in.read(data)) > 0) {
                        mSockOutStream.write(data, 0, len);
                    }
                    item.in.close();
                }

                mSockOutStream.flush();
                if (mTcpSenderListener != null) {
                    mTcpSenderListener.onSendSuccess(item);
                }
            } catch (Exception e) {
                e.printStackTrace();

                if (mTcpSenderListener != null) {
                    mTcpSenderListener.onSendFailed(item, e);
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
        boolean onSendBefore (SendItem item);
        void onSendSuccess (SendItem item);
        void onSendFailed (SendItem item, Exception e);
    }
}
