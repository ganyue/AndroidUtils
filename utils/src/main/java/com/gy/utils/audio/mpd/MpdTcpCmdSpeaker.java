package com.gy.utils.audio.mpd;

import com.gy.utils.tcp.TcpCmdSpeaker;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganyu on 2016/10/11.
 *
 */
public class MpdTcpCmdSpeaker extends TcpCmdSpeaker {

    public MpdTcpCmdSpeaker(Socket socket) {
        super(socket);
    }

    public MpdTcpCmdSpeaker(String ip, int port) {
        super(ip, port);
    }

    public MpdTcpCmdSpeaker (String ip) {
        super(ip, 6600);
    }


    @Override
    protected void receiveCmdResponse (String cmd) {
        try {
            String line;
            boolean responsed = false;
            List<String> results = new ArrayList<>();
            while ((line = mSockReader.readLine()) != null) {
                responsed = true;
                if (line.contains("OK MPD")) continue;
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
}
