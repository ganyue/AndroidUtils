package com.gy.utils.tcp;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by yue.gan on 2016/6/3.
 *
 */
public class TcpClient extends Thread{

    private String dstIp;
    private int dstPort;
    private Socket mSocket;
    private TcpSender mSender;
    private TcpReceiver mReceiver;
    private boolean isInited;
    private List<TcpClientListener> tcpClientListeners;
    private boolean isConnected = false;
    private String unique = "";

    private boolean enableHart = false;

    public TcpClient addTcpClientListener (TcpClientListener tcpClientListener) {
        if (tcpClientListeners == null) {
            tcpClientListeners = Collections.synchronizedList(new ArrayList<TcpClientListener>());
        }

        if (!tcpClientListeners.contains(tcpClientListener)) {
            tcpClientListeners.add(tcpClientListener);
        }
        return this;
    }

    public void removeTcpClientListener (TcpClientListener tcpClientListener) {
        if (tcpClientListeners != null) {
            tcpClientListeners.remove(tcpClientListener);
        }
    }

    /***
     * @param unique 用来区分各个client
     * */
    public TcpClient(Socket socket, String unique) {
        isConnected = true;
        this.unique = unique;
        mSocket = socket;
        InetSocketAddress socketAddress = (InetSocketAddress) mSocket.getRemoteSocketAddress();
        dstIp = socketAddress.getHostName();
        dstPort = socketAddress.getPort();
    }

    public TcpClient(String ip, int port, String unique) {
        this.unique = unique;
        dstIp = ip;
        dstPort = port;
        isInited = false;
    }

    public TcpClient enableHart (boolean enableHart) {
        this.enableHart = enableHart;
        if (mSender != null) mSender.enableHart(enableHart);
        return this;
    }

    public boolean isHartEnabled () {
        return enableHart;
    }

    public String getDstIp () {
        return dstIp;
    }

    public int getDstPort () {
        return dstPort;
    }

    public boolean isConnected () {
        return isConnected;
    }

    private void init () {
        if (!isInited) {
            mSender = new TcpSender(mSocket);
            mSender.enableHart(enableHart);
            mSender.setTcpSenderListener(tcpSenderListener);
            mSender.start();

            mReceiver = new TcpReceiver(mSocket);
            mReceiver.setTcpReceiverListener(tcpReceiverListener);
            mReceiver.start();
            isInited = true;
            if (tcpClientListeners != null && tcpClientListeners.size() > 0) {
                for (TcpClientListener tcpClientListener: tcpClientListeners) {
                    tcpClientListener.onSocketConnectSuccess(unique, dstIp, dstPort);
                }
            }
        }
    }

    @Override
    public synchronized void start() {
        if (mSocket != null) init();
        else super.start();
    }

    public void sendString (String unique, String msg) {
        if (mSender != null) mSender.sendString (unique, msg);
    }

    public void sendStream (String unique, InputStream fIn) {
        if (mSender != null) mSender.sendStream(unique, fIn);
    }

    @Override
    public void run() {
        if (mSocket == null) {
            try {
                mSocket = new Socket(dstIp, dstPort);
                isConnected = true;
            } catch (Exception e) {
                e.printStackTrace();
                if (tcpClientListeners != null && tcpClientListeners.size() > 0) {
                    for (TcpClientListener tcpClientListener: tcpClientListeners) {
                        tcpClientListener.onSocketConnectFail(unique, e, dstIp, dstPort);
                    }
                }
            }
        }

        if (mSocket == null) {
            return;
        }

        init();
    }

    private TcpSender.TcpSenderListener tcpSenderListener = new TcpSender.TcpSenderListener() {
        @Override
        public boolean onSendBefore(SendItem item) {
            if (tcpClientListeners != null && tcpClientListeners.size() > 0) {
                for (TcpClientListener tcpClientListener: tcpClientListeners) {
                    tcpClientListener.onSendBefore(unique, item, dstIp, dstPort);
                }
            }
            return false;
        }

        @Override
        public void onSendSuccess(SendItem item) {
            if (tcpClientListeners != null && tcpClientListeners.size() > 0) {
                for (TcpClientListener tcpClientListener: tcpClientListeners) {
                    tcpClientListener.onSendSuccess(unique, item, dstIp, dstPort);
                }
            }
        }

        @Override
        public void onSendFailed(SendItem item, Exception e) {
            isConnected = false;
            if (tcpClientListeners != null && tcpClientListeners.size() > 0) {
                for (TcpClientListener tcpClientListener: tcpClientListeners) {
                    tcpClientListener.onSendFailed(unique, item, e, dstIp, dstPort);
                }
            }
        }
    };

    private TcpMessageProcessor.TcpReceiveListener tcpReceiverListener = new TcpMessageProcessor.TcpReceiveListener() {
        @Override
        public void onReceive(byte[] buf, int offset, int len) {
            if (tcpClientListeners != null && tcpClientListeners.size() > 0) {
                for (TcpClientListener tcpClientListener: tcpClientListeners) {
                    tcpClientListener.onReceive(unique, new String(buf, offset, len), dstIp, dstPort);
                }
            }
        }

        @Override
        public void onReceiveError(Exception e) {
            if (tcpClientListeners != null && tcpClientListeners.size() > 0) {
                for (TcpClientListener tcpClientListener: tcpClientListeners) {
                    tcpClientListener.onReceiveError(unique, e, dstIp, dstPort);
                }
            }
        }
    };

    public void release () {
        mSender.release();
        mReceiver.release();
        isInited = false;
        try {
            if (mSocket != null) {
                mSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        interrupt();
    }

    public interface TcpClientListener {
        void onSocketConnectFail (String unique, Exception e, String dstIp, int dstPort);
        void onSocketConnectSuccess (String unique, String dstIp, int dstPort);
        boolean onSendBefore (String unique, SendItem item, String dstIp, int dstPort);
        void onSendSuccess (String unique, SendItem item, String dstIp, int dstPort);
        void onSendFailed (String unique, SendItem item, Exception e, String dstIp, int dstPort);
        void onReceive (String unique, String msg, String fromIp, int fromPort);
        void onReceiveError (String unique, Exception e, String fromIp, int fromPort);
    }
}
