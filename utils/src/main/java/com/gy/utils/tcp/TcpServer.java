package com.gy.utils.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by ganyu on 2016/6/3.
 *
 */
public class TcpServer extends Thread {

    private ServerSocket mServerSocket;
    private int port;

    private TcpServerListener mOnServerListener;
    private boolean isRun;

    public TcpServer(int port) {
        this.port = port;
    }

    public void setTcpServerListener (TcpServerListener listener) {
        mOnServerListener = listener;
    }

    @Override
    public void run() {
        try {
            mServerSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            if (mOnServerListener != null) {
                mOnServerListener.onSererStartFail(e);
            }
        }

        if (mServerSocket == null) {
            return;
        }
        isRun = true;

        while (isRun) {
            try {
                Socket socket = mServerSocket.accept();
                if (mOnServerListener != null) {
                    mOnServerListener.onAccept(socket);
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (mOnServerListener != null) {
                    mOnServerListener.onAcceptError(e);
                }
            }
        }
    }

    public void release () {
        isRun = false;
        mOnServerListener = null;
        interrupt();
        if (mServerSocket != null && !mServerSocket.isClosed()) {
            try {
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mServerSocket = null;
    }

    public interface TcpServerListener {
        void onSererStartFail(Exception e);
        void onAccept (Socket socket);
        void onAcceptError (IOException e);
    }
}
