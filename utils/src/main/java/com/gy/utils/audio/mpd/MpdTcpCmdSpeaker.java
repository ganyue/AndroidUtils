package com.gy.utils.audio.mpd;

import com.gy.utils.log.LogUtils;
import com.gy.utils.tcp.TcpCmdSpeaker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganyu on 2016/10/11.
 *
 */
public class MpdTcpCmdSpeaker extends TcpCmdSpeaker {

    private MpdTcpResponseMessageProcessor responseMessageProcessor;

    public MpdTcpCmdSpeaker(Socket socket) {
        super(socket);
    }

    public MpdTcpCmdSpeaker(String ip, int port, int readTimeout) {
        super(ip, port, readTimeout);
    }

    public MpdTcpCmdSpeaker (String ip, int readTimeout) {
        this(ip, 6600, readTimeout);
    }

    @Override
    public synchronized void start() {
        super.start();
        if (responseMessageProcessor != null) {
            responseMessageProcessor.release();
        }

        responseMessageProcessor = new MpdTcpResponseMessageProcessor();
        responseMessageProcessor.setOnProcessListener(onProcessListener);
        responseMessageProcessor.start();
    }

    @Override
    protected void receiveCmdResponse (String cmd) {
        try {
            String line;
            boolean responsed = false;
            List<String> results = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(mSockInStream));
            while ((line = reader.readLine()) != null) {
                responsed = true;
                if (line.startsWith("OK MPD")) continue;
                if (line.startsWith("OK") || line.startsWith("ACK")) break;
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

            responseMessageProcessor.onReceive(cmd, results);
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

    private MpdTcpResponseMessageProcessor.OnProcessListener onProcessListener =
            new MpdTcpResponseMessageProcessor.OnProcessListener() {
                @Override
                public void onProcess(String cmd, List<String> msg) {
                    if (listeners != null && listeners.size() > 0) {
                        for (TcpCmdSpeakerListener listener: listeners) {
                            listener.onReceive(cmd, msg, dstIp, dstPort);
                        }
                    }
                }
            };

    @Override
    public void release() {
        super.release();
        if (responseMessageProcessor != null) {
            responseMessageProcessor.release();
        }
    }
}
