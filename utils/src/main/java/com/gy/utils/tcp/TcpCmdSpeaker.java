package com.gy.utils.tcp;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by yue.gan on 2016/6/3.
 *
 */
public class TcpCmdSpeaker extends Thread{

    protected String dstIp;
    protected int dstPort;
    protected Socket mSocket;
    protected List<TcpCmdSpeakerListener> listeners;
    protected boolean isConnected = false;

    protected boolean isRun;
    protected OutputStream mSockOutStream;
    protected InputStream mSockInStream;
    protected ArrayBlockingQueue<String> mMessage;

    protected boolean enableHart = false;
    protected String hearCmd = "0xff";
    protected int soTimeOut = 0;
    protected int interval = 3000;

    public void addTcpClientListener (TcpCmdSpeakerListener listener) {
        if (listeners == null) {
            listeners = Collections.synchronizedList(new ArrayList<TcpCmdSpeakerListener>());
        }

        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeTcpClientListener (TcpCmdSpeakerListener listener) {
        if (listeners != null && listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    public TcpCmdSpeaker(Socket socket) {
        mSocket = socket;
        InetSocketAddress socketAddress = (InetSocketAddress) mSocket.getRemoteSocketAddress();
        dstIp = socketAddress.getHostName();
        dstPort = socketAddress.getPort();
        mMessage = new ArrayBlockingQueue<>(64);
    }

    public TcpCmdSpeaker(String ip, int port, int soTimeOut) {
        dstIp = ip;
        dstPort = port;
        mMessage = new ArrayBlockingQueue<>(64);
        this.soTimeOut = soTimeOut;
    }

    public void enableHart (boolean enableHart, String cmd, int interval) {
        this.enableHart = enableHart;
        this.hearCmd = cmd;
        this.interval = interval;
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

    public void send(String msg) {
        if (mMessage.contains(msg)) {
            return;
        }
        mMessage.offer(msg);
    }

    @Override
    public void run() {
        if (mSocket == null) {
            try {
                mSocket = new Socket(dstIp, dstPort);
                mSocket.setSoTimeout(soTimeOut);
                mSockOutStream = mSocket.getOutputStream();
                mSockInStream = mSocket.getInputStream();
                isConnected = true;

                if (listeners != null && listeners.size() > 0) {
                    for (TcpCmdSpeakerListener listener: listeners) {
                        listener.onSocketConnectSuccess(dstIp, dstPort);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                isConnected = false;
                if (listeners != null && listeners.size() > 0) {
                    for (TcpCmdSpeakerListener listener: listeners) {
                        listener.onSocketConnectFail(e, dstIp, dstPort);
                    }
                }
                return;
            }
        }

        isRun = true;
        String cmd;
        while (isRun) {
            /**3秒后如果没有消息发送则发送一个简单消息来确认tcp连接是否活着*/
            try {
                cmd = mMessage.poll(interval, TimeUnit.MILLISECONDS);
                if (cmd == null || TextUtils.isEmpty(cmd)) {
                    if (enableHart) {
                        cmd = hearCmd;
                    } else {
                        continue;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            if (!sendCmd(cmd)) continue;
            receiveCmdResponse(cmd);
        }//end of while
        isConnected = false;

    }//end of run

    protected boolean sendCmd (String cmd) {
        try {

            /** 发送命令前需要先清空buff, skip方法有问题，所以直接采用读取的方式清空了 */
            int available = mSockInStream.available();
            while (available > 0) {
                if (available > 1024) {
                    mSockInStream.read(new byte[1024], 0, 1024);
                    available = mSockInStream.available();
                } else {
                    mSockInStream.read(new byte[available], 0, available);
                    available = mSockInStream.available();
                }
            }

            boolean handled = false;
            if (listeners != null && listeners.size() > 0) {
                for (TcpCmdSpeakerListener listener: listeners) {
                    handled = listener.onSendBefore(cmd, dstIp, dstPort);
                }
            }
            /**如果在发送前消息被处理，则不再发送*/
            if (handled) {
                return false;
            }

            byte[] data = cmd.getBytes();
            mSockOutStream.write(data, 0, data.length);
            mSockOutStream.flush();

            if (listeners != null && listeners.size() > 0) {
                for (TcpCmdSpeakerListener listener: listeners) {
                    listener.onSendSuccess(cmd, dstIp, dstPort);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            isConnected = false;
            if (listeners != null && listeners.size() > 0) {
                for (TcpCmdSpeakerListener listener: listeners) {
                    listener.onSendFailed(cmd, e, dstIp, dstPort);
                }
            }
        }
        return false;
    }

    protected void receiveCmdResponse (String cmd) {
        try {
            String line;
            boolean responsed = false;
            List<String> results = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(mSockInStream));
            while ((line = reader.readLine()) != null) {
                responsed = true;
                if (line.contains("OK") || line.contains("ACK")) break;
                results.add(line);
            }

            if (!responsed) {
                if (listeners != null && listeners.size() > 0) {
                    for (TcpCmdSpeakerListener listener: listeners) {
                        listener.onReceiveError(cmd, new Exception("connection broken!"), dstIp, dstPort);
                    }
                }
                return;
            }

            if (listeners != null && listeners.size() > 0) {
                for (TcpCmdSpeakerListener listener: listeners) {
                    listener.onReceive(cmd, results, dstIp, dstPort);
                }
            }
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            isConnected = false;
            if (listeners != null && listeners.size() > 0) {
                for (TcpCmdSpeakerListener listener: listeners) {
                    listener.onReceiveError(cmd, e, dstIp, dstPort);
                }
            }
        }
    }

    public void release () {
        isRun = false;
        isConnected = false;
        listeners.clear();
        mMessage.clear();

        try {
            if (mSockOutStream != null) {
                mSockOutStream.close();
            }

            if (mSockInStream != null) {
                mSockInStream.close();
            }

            if (mSocket != null) {
                mSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        interrupt();
    }

    public interface TcpCmdSpeakerListener {
        void onSocketConnectFail(Exception e, String dstIp, int dstPort);
        void onSocketConnectSuccess(String dstIp, int dstPort);
        boolean onSendBefore(String cmd, String dstIp, int dstPort);
        void onSendSuccess(String cmd, String dstIp, int dstPort);
        void onSendFailed(String cmd, Exception e, String dstIp, int dstPort);
        void onReceive(String cmd, List<String> response, String fromIp, int fromPort);
        void onReceiveError(String cmd, Exception e, String fromIp, int fromPort);
    }
}
