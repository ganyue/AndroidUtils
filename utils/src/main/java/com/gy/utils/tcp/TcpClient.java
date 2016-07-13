package com.gy.utils.tcp;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ganyu on 2016/6/3.
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

    public void addTcpClientListener (TcpClientListener tcpClientListener) {
        if (tcpClientListeners == null) {
            tcpClientListeners = Collections.synchronizedList(new ArrayList<TcpClientListener>());
        }

        if (!tcpClientListeners.contains(tcpClientListener)) {
            tcpClientListeners.add(tcpClientListener);
        }
    }

    public void removeTcpClientListener (TcpClientListener tcpClientListener) {
        if (tcpClientListeners != null && tcpClientListeners.contains(tcpClientListener)) {
            tcpClientListeners.remove(tcpClientListener);
        }
    }

    public TcpClient(Socket socket) {
        mSocket = socket;
        InetSocketAddress socketAddress = (InetSocketAddress) mSocket.getRemoteSocketAddress();
        dstIp = socketAddress.getHostName();
        dstPort = socketAddress.getPort();
    }

    public TcpClient(String ip, int port) {
        dstIp = ip;
        dstPort = port;
        isInited = false;
    }

    private void init () {
        if (!isInited) {
            mSender = new TcpSender(mSocket);
            mReceiver = new TcpReceiver(mSocket);
            mSender.setTcpSenderListener(tcpSenderListener);
            mReceiver.setTcpReceiverListener(tcpReceiverListener);
            mSender.start();
            mReceiver.start();
            isInited = true;
        }
    }

    public void send (String msg) {
        mSender.send(msg);
    }

    @Override
    public void run() {
        if (mSocket == null) {
            try {
                mSocket = new Socket(dstIp, dstPort);
            } catch (Exception e) {
                e.printStackTrace();
                if (tcpClientListeners != null && tcpClientListeners.size() > 0) {
                    for (TcpClientListener tcpClientListener: tcpClientListeners) {
                        tcpClientListener.onSocketConnectFail(e, dstIp, dstPort);
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
        public boolean onSendBefore(String msg) {
            if (tcpClientListeners != null && tcpClientListeners.size() > 0) {
                for (TcpClientListener tcpClientListener: tcpClientListeners) {
                    tcpClientListener.onSendBefore(msg, dstIp, dstPort);
                }
            }
            return false;
        }

        @Override
        public void onSendSuccess(String msg) {
            if (tcpClientListeners != null && tcpClientListeners.size() > 0) {
                for (TcpClientListener tcpClientListener: tcpClientListeners) {
                    tcpClientListener.onSendSuccess(msg, dstIp, dstPort);
                }
            }
        }

        @Override
        public void onSendFailed(String msg, Exception e) {
            if (tcpClientListeners != null && tcpClientListeners.size() > 0) {
                for (TcpClientListener tcpClientListener: tcpClientListeners) {
                    tcpClientListener.onSendFailed(msg, e, dstIp, dstPort);
                }
            }
        }
    };

    private TcpReceiver.TcpReceiverListener tcpReceiverListener = new TcpReceiver.TcpReceiverListener() {
        @Override
        public void onReceive(byte[] buf, int offset, int len) {
            if (tcpClientListeners != null && tcpClientListeners.size() > 0) {
                for (TcpClientListener tcpClientListener: tcpClientListeners) {
                    tcpClientListener.onReceive(new String(buf, offset, len), dstIp, dstPort);
                }
            }
        }

        @Override
        public void onReceiveError(Exception e) {
            if (tcpClientListeners != null && tcpClientListeners.size() > 0) {
                for (TcpClientListener tcpClientListener: tcpClientListeners) {
                    tcpClientListener.onReceiveError(e, dstIp, dstPort);
                }
            }
        }
    };

    public interface TcpClientListener {
        void onSocketConnectFail (Exception e, String dstIp, int dstPort);
        void onSendBefore (String msg, String dstIp, int dstPort);
        void onSendSuccess (String msg, String dstIp, int dstPort);
        void onSendFailed (String msg, Exception e, String dstIp, int dstPort);
        void onReceive (String msg, String fromIp, int fromPort);
        void onReceiveError (Exception e, String fromIp, int fromPort);
    }
}
