package com.gy.utils.tcp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.gy.utils.wifi.WifiUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by yue.gan on 2016/6/3.
 *
 */
public class TcpServer extends Thread {

    private ServerSocket mServerSocket;
    private int port;
    private String ip;

    private TcpServerListener mOnServerListener;
    private boolean isRun;

    public TcpServer(Context context, int port) {
        this.port = port;
        this.ip = WifiUtils.getInstance(context.getApplicationContext()).getIp();
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

        if (mOnServerListener != null) {
            mOnServerListener.onServerStartSuccess(ip, port);
        }

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
        void onServerStartSuccess (String ip, int port);
        void onSererStartFail(Exception e);
        void onAccept (Socket socket);
        void onAcceptError (IOException e);
    }
}
