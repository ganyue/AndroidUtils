package com.gy.utils.audio.mpd;

import android.text.TextUtils;

import com.gy.utils.tcp.TcpCmdSpeaker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganyu on 2016/10/11.
 *
 */
public class MpdPlayerUtils {

    private static MpdPlayerUtils mInstance;
    private MpdTcpCmdSpeaker cmdSpeaker;
    private List<OnMpdListener> onMpdListeners;

    public static MpdPlayerUtils getInstance () {
        if (mInstance == null) {
            mInstance = new MpdPlayerUtils();
        }

        return mInstance;
    }

    private MpdPlayerUtils () {
    }

    public void addMpdListener (OnMpdListener listener) {
        if (onMpdListeners == null) onMpdListeners = new ArrayList<>();
        if (!onMpdListeners.contains(listener)) {
            onMpdListeners.add(listener);
        }
    }

    public void removeMpdListener (OnMpdListener listener) {
        if (onMpdListeners == null || !onMpdListeners.contains(listener)) return;
        onMpdListeners.remove(listener);
    }

    public void connect (String ip) {
        if (ip == null || TextUtils.isEmpty(ip)) return;

        disConnect();
        cmdSpeaker = new MpdTcpCmdSpeaker(ip);
        cmdSpeaker.addTcpClientListener(tcpCmdSpeakerListener);
        cmdSpeaker.start();
    }

    public void disConnect () {
        if (cmdSpeaker != null) {
            cmdSpeaker.release();
        }
    }

    public void send (String cmd) {
        if (cmdSpeaker == null) return;
        if (!cmdSpeaker.isConnected()) {
            connect(cmdSpeaker.getDstIp());
        }
        cmdSpeaker.send(MpdConsts.getCommandStr(cmd));
    }

    private MpdTcpCmdSpeaker.TcpCmdSpeakerListener tcpCmdSpeakerListener
            = new TcpCmdSpeaker.TcpCmdSpeakerListener() {
        @Override
        public void onSocketConnectFail(Exception e, String dstIp, int dstPort) {
            if (onMpdListeners != null) {
                for (OnMpdListener listener: onMpdListeners) {
                    listener.onConnectFail(dstIp);
                }
            }
        }

        @Override
        public void onSocketConnectSuccess(String dstIp, int dstPort) {
            if (onMpdListeners != null) {
                for (OnMpdListener listener: onMpdListeners) {
                    listener.onConnectSuccess(dstIp);
                }
            }
        }

        @Override
        public boolean onSendBefore(String cmd, String dstIp, int dstPort) {
            return false;
        }

        @Override
        public void onSendSuccess(String cmd, String dstIp, int dstPort) {
            //do nothing
        }

        @Override
        public void onSendFailed(String cmd, Exception e, String dstIp, int dstPort) {
            /** 断线重连 */
            if (cmdSpeaker != null) {
                if (onMpdListeners != null) {
                    for (OnMpdListener listener: onMpdListeners) {
                        listener.onReconnect(cmdSpeaker.getDstIp());
                    }
                }
                connect(cmdSpeaker.getDstIp());
            }
        }

        @Override
        public void onReceive(String cmd, List<String> response, String fromIp, int fromPort) {
            //TODO parse response & callback
            if (onMpdListeners != null) {
                for (OnMpdListener listener: onMpdListeners) {
                    listener.onResponse(cmd, response);
                }
            }
        }

        @Override
        public void onReceiveError(String cmd, Exception e, String fromIp, int fromPort) {
            /** 断线重连 */
            if (cmdSpeaker != null) {
                if (onMpdListeners != null) {
                    for (OnMpdListener listener: onMpdListeners) {
                        listener.onReconnect(cmdSpeaker.getDstIp());
                    }
                }
                connect(cmdSpeaker.getDstIp());
            }
        }
    };

    public void release () {
        onMpdListeners.clear();
        if (cmdSpeaker != null) {
            cmdSpeaker.release();
        }
    }
}
