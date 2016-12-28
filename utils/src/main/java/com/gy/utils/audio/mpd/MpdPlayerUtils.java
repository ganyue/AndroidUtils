package com.gy.utils.audio.mpd;

import android.text.TextUtils;

import com.gy.utils.tcp.TcpCmdSpeaker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ganyu on 2016/10/11.
 *
 */
public class MpdPlayerUtils {

    private static MpdPlayerUtils mInstance;
    private MpdTcpCmdSpeaker cmdSpeaker;
    private MpdTcpCmdSpeaker idleSpeaker;
    private List<OnMpdListener> onMpdListeners;
    private MpdMessageParser mParser;

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

    public String getDstIp () {
        if (isConnected()) {
            return cmdSpeaker.getDstIp();
        }
        return "";
    }

    /** 5s读取超时，30s发送一次status命令 */
    public void connect (String ip) {
        if (ip == null || TextUtils.isEmpty(ip)) return;

        disConnect();
        cmdSpeaker = new MpdTcpCmdSpeaker(ip, 5000);
        cmdSpeaker.addTcpClientListener(tcpCmdSpeakerListener);
        cmdSpeaker.enableHart (true, MpdConsts.getCommandStr(MpdConsts.Cmd.MPD_CMD_STATUS), 30000);

        cmdSpeaker.start();

        connectIdle(ip);

        mParser = new MpdMessageParser();
    }

    /** 监听状态 */
    private void connectIdle (String ip) {
        if (idleSpeaker != null) {
            idleSpeaker.release();
            idleSpeaker = null;
        }

        idleSpeaker = new MpdTcpCmdSpeaker(ip, 0);
        idleSpeaker.addTcpClientListener(tcpCmdSpeakerListener);
        idleSpeaker.enableHart (true, MpdConsts.getCommandStr(MpdConsts.Cmd.MPD_CMD_IDLE), 500);
        idleSpeaker.start();
    }

    public void disConnect () {
        if (cmdSpeaker != null) {
            cmdSpeaker.release();
            cmdSpeaker = null;
        }
        if (idleSpeaker != null) {
            idleSpeaker.release();
            idleSpeaker = null;
        }
    }

    public boolean isConnected () {
        return cmdSpeaker != null && cmdSpeaker.isConnected()
                && idleSpeaker != null && idleSpeaker.isConnected();
    }

    /** 如果发送命令时发现连接断开则重连，如果tcp连接断开而状态没变过来，在发送失败后会重连 */
    public void send (String cmd, String... args) {
        if (cmdSpeaker == null) return;
        if (!cmdSpeaker.isConnected()) {
            connect(cmdSpeaker.getDstIp());
        }
        cmdSpeaker.send(MpdConsts.getCommandStr(cmd, args));
    }

    public void send (List<String> cmdStrs, boolean withSweparator) {
        if (cmdSpeaker == null) return;
        if (!cmdSpeaker.isConnected()) {
            connect(cmdSpeaker.getDstIp());
        }
        cmdSpeaker.send(MpdConsts.getQueuedCommand(cmdStrs, withSweparator));
    }


    public void getStatus() {
        send(MpdConsts.Cmd.MPD_CMD_STATUS);
    }

    public void getAllFiles () {
        send(MpdConsts.Cmd.MPD_CMD_LISTALL);
    }

    public void getCurrentPlayList() {
        send(MpdConsts.Cmd.MPD_CMD_LIST);
    }

    public void getPlaylist(String plistName) {
        send(MpdConsts.Cmd.MPD_CMD_PLAYLIST_INFO, plistName);
    }

    public void getPlaylistInfos() {
        send(MpdConsts.Cmd.MPD_CMD_LISTPLAYLISTS);
    }

    public void clearPlaylist () {
        send(MpdConsts.Cmd.MPD_CMD_CLEAR);
    }

    public void play (List<String> files, int playIndex) {
        clearPlaylist();
        List<String> commands = new ArrayList<>();
        for (String file: files) {
            commands.add(MpdConsts.getCommandStr(MpdConsts.Cmd.MPD_CMD_ADD, file));
        }
        send(commands, true);
        skipToPosition(playIndex);
    }

    public void playBySongId(int songId){
        send(MpdConsts.Cmd.MPD_CMD_PLAY_ID, ""+songId);
    }

    public void skipToPosition(int pos) {
        send(MpdConsts.Cmd.MPD_CMD_PLAY, ""+pos);
    }

    public void skipToId(int id) {
        send(MpdConsts.Cmd.MPD_CMD_PLAY_ID, ""+id);
    }

    public void seek(int songIndex, int pos) {
        send(MpdConsts.Cmd.MPD_CMD_SEEK, ""+songIndex, ""+pos);
    }

    public void play () {
        send(MpdConsts.Cmd.MPD_CMD_PLAY);
    }

    public void pause () {
        send(MpdConsts.Cmd.MPD_CMD_PAUSE);
    }

    public void stop () {
        send(MpdConsts.Cmd.MPD_CMD_STOP);
    }

    public void prev () {
        send(MpdConsts.Cmd.MPD_CMD_PREV);
    }

    public void next () {
        send(MpdConsts.Cmd.MPD_CMD_NEXT);
    }

    public void single (boolean single) {
        send(MpdConsts.Cmd.MPD_CMD_SINGLE, single? "1": "0");
    }

    public void random (boolean random) {
        send(MpdConsts.Cmd.MPD_CMD_RANDOM, random? "1": "0");
    }

    public void repeat (boolean repeat) {
        send(MpdConsts.Cmd.MPD_CMD_REPEAT, repeat? "1": "0");
    }

    public void setVolume (int vol) {
        vol = vol <= 0? 0: vol>= 100? 100: vol;
        send(MpdConsts.Cmd.MPD_CMD_SET_VOLUME, ""+vol);
    }

    public void addToPlaylist (List<String> files, String playlistName) {
        List<String> commands = new ArrayList<>();
        for (String file: files) {
            commands.add(MpdConsts.getCommandStr(MpdConsts.Cmd.MPD_CMD_PLAYLIST_ADD, playlistName, file));
        }
        send(commands, true);
    }

    public void removeFromPlaylist (String playlistName, int index) {
        send(MpdConsts.getCommandStr(MpdConsts.Cmd.MPD_CMD_PLAYLIST_DEL, playlistName, ""+index));
    }

    public void removeFromCurrentPlaylistByPos (int index) {
        send(MpdConsts.getCommandStr(MpdConsts.Cmd.MPD_CMD_REMOVE, ""+index));
    }

    public void removeFromCurrentPlaylistById (int id) {
        send(MpdConsts.getCommandStr(MpdConsts.Cmd.MPD_CMD_REMOVE_ID, ""+id));
    }

    public void removeFromPlaylist (String playlistName, List<Integer> index) {
        //首先要给index排序，防止删除第n个，第n+1就变成第n个，再删除第n+1个会出问题
        Collections.sort(index, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return lhs == rhs? 0: lhs > rhs? -1: 1;
            }
        });
        List<String> commands = new ArrayList<>();
        for (Integer pos: index) {
            commands.add(MpdConsts.getCommandStr(MpdConsts.Cmd.MPD_CMD_PLAYLIST_DEL, playlistName, ""+pos));
        }
        send(commands, true);
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
            if (cmdSpeaker != null && !cmd.equals(MpdConsts.Cmd.MPD_CMD_IDLE)) {
                if (onMpdListeners != null) {
                    for (OnMpdListener listener: onMpdListeners) {
                        listener.onReconnect(cmdSpeaker.getDstIp());
                    }
                }
                connect(cmdSpeaker.getDstIp());
            } else if (cmdSpeaker != null && idleSpeaker != null && cmd.equals(MpdConsts.Cmd.MPD_CMD_IDLE)) {
                connectIdle(idleSpeaker.getDstIp());
            }
        }

        @Override
        public void onReceive(String cmd, List<String> response, String fromIp, int fromPort) {
            //TODO parse response & callback

            if (TextUtils.isEmpty(cmd) || response == null || response.size() <= 0) return;

            if (onMpdListeners != null) {
                for (OnMpdListener listener: onMpdListeners) {
                    listener.onResponse(cmd, mParser.parseResponse(cmd, response));
                }
            }
        }

        @Override
        public void onReceiveError(String cmd, Exception e, String fromIp, int fromPort) {
            /** 断线重连 */
            if (cmdSpeaker != null && !cmd.equals(MpdConsts.Cmd.MPD_CMD_IDLE)) {
                if (onMpdListeners != null) {
                    for (OnMpdListener listener: onMpdListeners) {
                        listener.onReconnect(cmdSpeaker.getDstIp());
                    }
                }
                connect(cmdSpeaker.getDstIp());
            } else if (cmdSpeaker != null && idleSpeaker != null && cmd.equals(MpdConsts.Cmd.MPD_CMD_IDLE)) {
                connectIdle(idleSpeaker.getDstIp());
            }
        }
    };

    public void release () {
        onMpdListeners.clear();
        if (cmdSpeaker != null) {
            cmdSpeaker.release();
        }
        if (idleSpeaker != null) {
            idleSpeaker.release();
        }
    }
}
