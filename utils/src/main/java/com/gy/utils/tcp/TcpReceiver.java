package com.gy.utils.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by sam_gan on 2016/6/3.
 *
 */
public class TcpReceiver extends Thread {

    private InputStream mSockInStream;
    private TcpReceiverListener mTcpReceiverListener;
    private boolean isRun;

    public TcpReceiver (Socket socket) {
        try {
            mSockInStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTcpReceiverListener (TcpReceiverListener listener) {
        mTcpReceiverListener = listener;
    }

    @Override
    public void run() {
        if (mSockInStream == null) {
            return;
        }
        isRun = true;

        byte[] buff = new byte[1024];
        int len = 0;
        while (isRun) {
            try {
                len = mSockInStream.read(buff);
                if (len <= 0) {
                    break;
                }
                if (mTcpReceiverListener != null) {
                    mTcpReceiverListener.onReceive(buff, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (mTcpReceiverListener != null) {
                    mTcpReceiverListener.onReceiveError(e);
                }
            }
        }
    }

    public interface TcpReceiverListener {
        void onReceive (byte[] buf, int offset, int len);
        void onReceiveError (Exception e);
    }
}
