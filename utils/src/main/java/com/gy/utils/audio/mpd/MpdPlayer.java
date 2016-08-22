package com.gy.utils.audio.mpd;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.TextView;

import com.gy.utils.audio.AudioPlayerConst;
import com.gy.utils.audio.IAudioPlayer;
import com.gy.utils.audio.Playlist;
import com.gy.utils.log.LogUtils;
import com.gy.utils.tcp.TcpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ganyu on 2016/8/12.
 *
 */
public class MpdPlayer implements IAudioPlayer{

    private TcpClient client;
    private boolean connect;
    private MpdStatus status;
    private Map<String, Playlist> playlists;
    private List<String> playlistNames;
    private List<List<String>> receiveStrs;

    private Activity activity;
    private TextView tvLog;


    public MpdPlayer (String ip, Activity activity, TextView textView) {
        this.activity = activity;
        tvLog = textView;

        client = new TcpClient(ip, 6600);
        client.addTcpClientListener(tcpClientListener);
        client.start();
        connect = true;
        receiveStrs = new ArrayList<>();
        playlists = new HashMap<>();
    }

    public void disconnect () {
        connect = false;
        sendCmd(MpdConsts.Cmd.MPD_CMD_CLOSE);
    }

    public void sendCmd (String cmd, String... arg) {
        client.send(MpdConsts.getCommandStr(cmd, arg));
    }

    public void testCmd () {
//        setMode(AudioPlayerConst.Mode.REPEAT_ONE);
//        sendCmd(MpdConsts.Cmd.MPD_CMD_PLAYLIST_INFO, "8");
//        sendCmd(MpdConsts.Cmd.MPD_CMD_LISTALLINFO);
        getPlaylistTracks("0");
    }

    public void release () {
        connect = false;
        client.release();
        playlists.clear();
        playlistNames.clear();
        receiveStrs.clear();
        playlistNames = null;
    }

    private int indexTemp = 0;
    private boolean isInitPlaylist = false;
    private void getPlaylistTracks (String playlistName) {
        sendCmd(MpdConsts.Cmd.MPD_CMD_PLAYLIST_INFO, playlistName);
    }

    private void parseMessage (List<String> msg) {
        int len = msg.size() > 20? 20: msg.size();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            stringBuilder.append(msg.get(i));
        }
        String temp = stringBuilder.toString();

        /**状态信息*/
        if (temp.contains("state")) {
            if (status == null) status = new MpdStatus();
            MpdMessageParser.parseStatus(status, msg);
        }

        /**歌单名字列表*/
        if (temp.contains(MpdConsts.MpdKeys.PLAYLIST)
                && temp.contains(MpdConsts.MpdKeys.LAST_MODIFIED)) {
            if (playlistNames == null) playlistNames = new ArrayList<>();
            playlistNames = MpdMessageParser.parsePlaylistNames(playlistNames, msg);
            if (playlists == null || playlists.size() <= 0) {
                indexTemp = 0;
                isInitPlaylist = true;
                getPlaylistTracks(playlistNames.get(indexTemp));//获取歌单歌曲列表
            }
        }

        /**解析歌单歌曲列表，并获取其他歌单歌曲列表*/
        if (temp.contains(MpdConsts.MpdKeys.FILE)
                && temp.contains(MpdConsts.MpdKeys.ALBUM)) {
            Playlist playlist = MpdMessageParser.parsePlaylist(msg);
            String playlistName = playlistNames.get(indexTemp);
            playlist.setName(playlistName);
            if (playlistName.equals(status.playlist)) {
                playlist.setCurrentPos(status.song);
            }
            playlists.put(playlistNames.get(indexTemp), playlist);
            if (isInitPlaylist) {
                if (indexTemp < playlistNames.size() - 1) {
                    indexTemp++;
                    getPlaylistTracks(playlistNames.get(indexTemp));
                } else {
                    isInitPlaylist = false;
                }
            }
        } else if (isInitPlaylist && msg.size() == 1 && msg.get(0).equals("OK")) {
            //歌单中没有单曲，直接返回OK，这时候没法判断，只好这么搞了
            if (indexTemp >= 0 && indexTemp < playlistNames.size() - 1) {
                indexTemp++;
                getPlaylistTracks(playlistNames.get(indexTemp));
            } else {
                isInitPlaylist = false;
            }
        }
    }

    private TcpClient.TcpClientListener tcpClientListener = new TcpClient.TcpClientListener() {
        @Override
        public void onSocketConnectFail(Exception e, String dstIp, int dstPort) {
            LogUtils.d("yue.gan", "connect fail : " + dstIp + ":"+dstPort);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvLog.setText("" + tvLog.getText() + "connect fail\n");
                }
            });
        }

        @Override
        public void onSocketConnectSuccess(String dstIp, int dstPort) {
            /**连接成功后立即获取状态信息*/
            if (status == null) sendCmd(MpdConsts.Cmd.MPD_CMD_STATUS);
            /**获取歌单信息，获取到歌单名字后会做歌曲列表的获取动作*/
            if (playlistNames == null) sendCmd(MpdConsts.Cmd.MPD_CMD_LISTPLAYLISTS);
        }

        @Override
        public void onSendBefore(String msg, String dstIp, int dstPort) {
            LogUtils.d("yue.gan", "send before : " + msg);
        }

        @Override
        public void onSendSuccess(final String msg, String dstIp, int dstPort) {
            LogUtils.d("yue.gan", "send : " + msg);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvLog.setText("" + tvLog.getText() + "send : " + msg);
                }
            });
        }

        @Override
        public void onSendFailed(final String msg, Exception e, String dstIp, int dstPort) {
            //断线重连
            LogUtils.d("yue.gan", "send fail : " + msg);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvLog.setText("" + tvLog.getText() + "send fail : " + msg);
                }
            });

            client.release();
            if (connect) {
                client = new TcpClient(dstIp, dstPort);
                client.addTcpClientListener(tcpClientListener);
                client.start();
            }
        }

        @Override
        public void onReceive(final String msg, String fromIp, int fromPort) {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    tvLog.setText("" + tvLog.getText() + "receive : " + msg);
                    LogUtils.d("yue.gan", "receive msg : " + msg);
                }
            });

            if (TextUtils.isEmpty(msg)) return;
            String[] lines = msg.split("\n");

            /**有可能一次收到多个命令的回复 e.g: XXX OK XXX OK... */
            for (String line: lines) {
                if (line.startsWith("ACK")) continue;
                if (receiveStrs.size() <= 0) receiveStrs.add(new ArrayList<String>());
                receiveStrs.get(receiveStrs.size() - 1).add(line);
                if (line.startsWith("OK")) {
                    receiveStrs.add(new ArrayList<String>());
                    List<String> list = receiveStrs.remove(0);
                    parseMessage(list);
                }
            }
        }

        @Override
        public void onReceiveError(Exception e, String fromIp, int fromPort) {
            LogUtils.d("yue.gan", "recieve error");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvLog.setText("" + tvLog.getText() + "receive error\n");
                }
            });
        }
    };

    @Override
    public boolean initPlaylist(Playlist playlist) {
        //TODO
        return false;
    }

    @Override
    public boolean stop() {
        //TODO
        return false;
    }

    @Override
    public boolean playOrPause() {
        //TODO
        return false;
    }

    @Override
    public boolean prev() {
        //TODO
        return false;
    }

    @Override
    public boolean next() {
        //TODO
        return false;
    }

    @Override
    public boolean seek(int pos) {
        //TODO
        return false;
    }

    @Override
    public int getPosition() {
        //TODO
        return 0;
    }

    @Override
    public boolean isPlaying() {
        //TODO
        return false;
    }

    @Override
    public void setMode(int mode) {
        switch (mode) {
            case AudioPlayerConst.Mode.NORMAL:
            case AudioPlayerConst.Mode.REPEAT_ALL:
                sendCmd(MpdConsts.Cmd.MPD_CMD_SINGLE, "0");
                sendCmd(MpdConsts.Cmd.MPD_CMD_RANDOM, "0");
                sendCmd(MpdConsts.Cmd.MPD_CMD_REPEAT, "1");
                break;
            case AudioPlayerConst.Mode.RANDOM:
                sendCmd(MpdConsts.Cmd.MPD_CMD_SINGLE, "0");
                sendCmd(MpdConsts.Cmd.MPD_CMD_RANDOM, "1");
                sendCmd(MpdConsts.Cmd.MPD_CMD_REPEAT, "0");
                break;
            case AudioPlayerConst.Mode.REPEAT_ONE:
                sendCmd(MpdConsts.Cmd.MPD_CMD_SINGLE, "1");
                sendCmd(MpdConsts.Cmd.MPD_CMD_RANDOM, "0");
                sendCmd(MpdConsts.Cmd.MPD_CMD_REPEAT, "1");
                break;
        }
    }

    @Override
    public int getMode() {
        return status == null? AudioPlayerConst.Mode.NORMAL: status.mode;
    }

    @Override
    public void setVolume(int volume) {
        //TODO
    }

    @Override
    public int getVolume() {
        //TODO
        return 0;
    }

    @Override
    public boolean isAlive() {
        return client !=null && client.isConnected();
    }

    @Override
    public Playlist getPlaylist() {
        return playlists.get(status.playlist);
    }

    public Playlist getPlaylist(String playlistName) {
        if (playlists.containsKey(playlistName)) {
            Playlist playlist = playlists.get(playlistName);
            if (playlist.getTracks() == null || playlist.getTracks().size() <= 0) {
                getPlaylistTracks(playlistName);
            }
            return playlists.get(playlists);
        } else {
            getPlaylistTracks(playlistName);
            return null;
        }
    }
}
