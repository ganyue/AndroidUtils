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
    private TcpMessageProcessor tcpMessageProcessor;
    private boolean isRun;

    public TcpReceiver (Socket socket) {
        try {
            mSockInStream = socket.getInputStream();
            tcpMessageProcessor = new TcpMessageProcessor();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTcpReceiverListener (TcpMessageProcessor.TcpReceiveListener listener) {
        tcpMessageProcessor.setOnReceiveListener(listener);
    }

    @Override
    public void run() {
        if (mSockInStream == null) {
            return;
        }
        tcpMessageProcessor.start();
        isRun = true;

        byte[] buff = new byte[2048];
        int len = 0;
        while (isRun) {
            try {
                len = mSockInStream.read(buff);
                if (len <= 0) {
                    break;
                }
                tcpMessageProcessor.onReceive(buff, 0, len);
            } catch (IOException e) {
                e.printStackTrace();
                tcpMessageProcessor.onReceiveError(e);
            }
        }
    }

    public void release () {
        isRun = false;
        interrupt();
        try {
            if (mSockInStream != null) {
                mSockInStream.close();
                mSockInStream = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        tcpMessageProcessor.release();
    }

}
