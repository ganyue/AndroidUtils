package com.gy.utils.audio.mpd;

/**
 * Created by ganyu on 2016/8/12.
 *
 */
public class MpdPlayer{

//    private TcpClient client;
//    private boolean connect;
//    private MpdStatus status;
//    private Map<String, Playlist> playlists;
//    private List<String> playlistNames;
//    private List<String> receiveStrs;
//
//    private Playlist currentPlaylist;
//
//    private Activity activity;
//    private TextView tvLog;
//
//
//    public MpdPlayer (String ip, Activity activity, TextView textView) {
//        this.activity = activity;
//        tvLog = textView;
//
//        client = new TcpClient(ip, 6600);
//        client.addTcpClientListener(tcpClientListener);
//        client.start();
//        connect = true;
//        receiveStrs = new ArrayList<>();
//        playlists = new HashMap<>();
//    }
//
//    public void disconnect () {
//        connect = false;
//        sendCmd(MpdConsts.Cmd.MPD_CMD_CLOSE);
//    }
//
//    public void sendCmd (String cmd, String... arg) {
//        client.send(MpdConsts.getCommandStr(cmd, arg));
//    }
//
//    public void sendCmds (List<String> cmdStrs, boolean withSweparator) {
//        String cmdStr = (withSweparator? "command_list_ok_begin": "command_list_begin") + "\n";
//        for (String str : cmdStrs) cmdStr += str;
//        cmdStr += "command_list_end" + "\n";
//        client.send(cmdStr);
//    }
//
//    public void testCmd () {
//        ArrayList list = new ArrayList();
//    }
//
//    public void release () {
//        connect = false;
//        client.release();
//        playlists.clear();
//        playlistNames.clear();
//        receiveStrs.clear();
//        playlistNames = null;
//    }
//
//    private String latestCmdSended = "";
//    private final Object sendLock = new Object();
//
//    /** 处理收到的消息 */
//    private void parseMessage (List<String> msg) {
//
//        latestCmdSended = latestCmdSended.trim();
//        if (latestCmdSended.contains(MpdConsts.Cmd.MPD_CMD_STATUS)) {
//            /** 状态  status */
//            if (status == null) status = new MpdStatus();
//            MpdMessageParser.parseStatus(status, msg);
//
//            if (currentPlaylist != null) {
//                currentPlaylist.setMode(status.mode);
//                currentPlaylist.setCurrentPos(status.song);
//            }
//            //TODO
//        } else if (latestCmdSended.contains(MpdConsts.Cmd.MPD_CMD_LISTPLAYLISTS)) {
//            /** 歌单名字列表 */
//            if (playlistNames == null) playlistNames = new ArrayList<>();
//            playlistNames = MpdMessageParser.parsePlaylistNames(playlistNames, msg);
//            /** 获取所有歌单歌曲列表 */
//            if (playlistNames.size() > 0) {
//                for (String pName: playlistNames) {
//                    sendCmd(MpdConsts.Cmd.MPD_CMD_PLAYLIST_INFO, pName);
//                }
//                refreshCurrentPlaylist();
//            }
//        } else if (latestCmdSended.contains(MpdConsts.Cmd.MPD_CMD_PLAYLIST_INFO)) {
//            /** 歌单歌曲列表 */
//            String pName = latestCmdSended.substring(
//                    latestCmdSended.indexOf("\"") + 1,
//                    latestCmdSended.lastIndexOf("\""));
//            Playlist playlist = MpdMessageParser.parsePlaylist(msg);
//            playlist.setName(pName);
//            playlists.put(pName, playlist);
//        } else if (latestCmdSended.equals(MpdConsts.Cmd.MPD_CMD_LIST)) {
//            /** 当前正在播放的歌单歌曲列表 */
//            currentPlaylist = MpdMessageParser.parsePlaylist(msg);
//        } else if (latestCmdSended.equals(MpdConsts.Cmd.MPD_CMD_PLAY)
//                || latestCmdSended.equals(MpdConsts.Cmd.MPD_CMD_PAUSE)
//                || latestCmdSended.equals(MpdConsts.Cmd.MPD_CMD_NEXT)
//                || latestCmdSended.equals(MpdConsts.Cmd.MPD_CMD_PREV)
//                || latestCmdSended.equals(MpdConsts.Cmd.MPD_CMD_STOP)
//                || latestCmdSended.contains(MpdConsts.Cmd.MPD_CMD_SEEK)
//                || latestCmdSended.contains(MpdConsts.Cmd.MPD_CMD_PLAY + "\"")) {
//            /** 播放、暂停、停止、上一首、下一首 、播放模式 需要更新状态*/
//            refreshStatus();
//        }
//
//        synchronized (sendLock) {
//            receiveStrs.clear();
//            latestCmdSended = "";
//            sendLock.notify();
//        }
//    }
//
//    private TcpClient.TcpClientListener tcpClientListener = new TcpClient.TcpClientListener() {
//        @Override
//        public void onSocketConnectFail(Exception e, String dstIp, int dstPort) {
//            LogUtils.d("yue.gan", "connect fail : " + dstIp + ":"+dstPort);
//            /** TODO tcp client 连接失败，应该重连 */
//            activity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    tvLog.setText("" + tvLog.getText() + "connect fail\n");
//                }
//            });
//        }
//
//        @Override
//        public void onSocketConnectSuccess(String dstIp, int dstPort) {
//            /**连接成功后立即获取状态信息*/
//            if (status == null) refreshStatus();
//            /** 获取歌单信息，获取到歌单名字后会做歌曲列表的获取动作 */
//            if (playlistNames == null) sendCmd(MpdConsts.Cmd.MPD_CMD_LISTPLAYLISTS);
//        }
//
//        @Override
//        public boolean onSendBefore(String msg, String dstIp, int dstPort) {
//            LogUtils.d("yue.gan", "send before : " + msg);
//            /** 如果上次命令发送后没有数据返回则等待3s， 3s后仍未返回当上次命令同步失败 */
//            if (!TextUtils.isEmpty(latestCmdSended)) {
//                synchronized (sendLock) {
//                    try {
//                        sendLock.wait(3000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            latestCmdSended = msg;
//            return false;
//        }
//
//        @Override
//        public void onSendSuccess(final String msg, String dstIp, int dstPort) {
//            LogUtils.d("yue.gan", "send : " + msg);
//            activity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    tvLog.setText("" + tvLog.getText() + "send : " + msg);
//                }
//            });
//        }
//
//        @Override
//        public void onSendFailed(final String msg, Exception e, String dstIp, int dstPort) {
//            /** TODO tcp client 连接断开，应该重连 */
//            LogUtils.d("yue.gan", "send fail : " + msg);
//            activity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    tvLog.setText("" + tvLog.getText() + "send fail : " + msg);
//                }
//            });
//
//            client.release();
//            if (connect) {
//                client = new TcpClient(dstIp, dstPort);
//                client.addTcpClientListener(tcpClientListener);
//                client.start();
//            }
//        }
//
//        @Override
//        public void onReceive(final String msg, String fromIp, int fromPort) {
//
//            activity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    LogUtils.d("yue.gan", "receive msg : " + msg);
//                }
//            });
//
//            if (TextUtils.isEmpty(msg) || TextUtils.isEmpty(latestCmdSended)) {
//                receiveStrs.clear();
//                return;
//            }
//
//
//            String[] lines = msg.split("\n");
//
//            if (lines.length > 0 && lines[0].startsWith("OK MPD ")) {
//                lines[0] = "";
//            }
//
//            /**有可能一次收到多个命令的回复 e.g: XXX OK XXX OK... */
//            for (String line: lines) {
//                receiveStrs.add(line);
//                if (line.startsWith("OK") || line.startsWith("ACK")) {
//                    parseMessage(receiveStrs);
//                }
//            }
//        }
//
//        @Override
//        public void onReceiveError(Exception e, String fromIp, int fromPort) {
//            LogUtils.d("yue.gan", "recieve error");
//            activity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    tvLog.setText("" + tvLog.getText() + "receive error\n");
//                }
//            });
//        }
//    };
//
//    private void refreshCurrentPlaylist () {
//        /** 获取当前正在播放的歌单歌曲列表 */
//        sendCmd(MpdConsts.Cmd.MPD_CMD_LIST);
//    }
//
//    private void refreshStatus () {
//        /** 更新状态 */
//        sendCmd(MpdConsts.Cmd.MPD_CMD_STATUS);
//    }
//
//    public void addUrlsToCurrentPlist (List<String> urls) {
//        for (String url: urls) {
//            //如果存在这个url就不再重复添加
//            if (currentPlaylist != null && currentPlaylist.getTracks() != null) {
//                boolean hasUrl = false;
//                for (Track track: currentPlaylist.getTracks()) {
//                    if (track.file.equals(url)) {
//                        hasUrl = true;
//                        break;
//                    }
//                }
//                if (hasUrl) continue;
//            }
//            sendCmd(MpdConsts.getCommandStr(MpdConsts.Cmd.MPD_CMD_ADD, url));
//        }
//        refreshCurrentPlaylist();
//    }
//
//    public void removeFromCurrentPlistByIndex (List<Integer> index) {
//        //首先要给index排序，防止删除第n个，第n+1就变成第n个，再删除第n+1个会出问题
//        Collections.sort(index, new Comparator<Integer>() {
//            @Override
//            public int compare(Integer lhs, Integer rhs) {
//                return lhs == rhs? 0: lhs > rhs? -1: 1;
//            }
//        });
//        for (int i: index) {
//            sendCmd(MpdConsts.getCommandStr(MpdConsts.Cmd.MPD_CMD_REMOVE, Integer.toString(i)));
//        }
//        refreshCurrentPlaylist();
//    }
//
//    public void addUrlsToPlaylist (String playlistName, List<String> urls) {
//        Playlist playlist = playlists.get(playlistName);
//
//        for (String url: urls) {
//            //如果存在这个url就不再重复添加
//            if (playlist != null && playlist.getTracks() != null) {
//                boolean hasUrl = false;
//                for (Track track: playlist.getTracks()) {
//                    if (track.file.equals(url)) {
//                        hasUrl = true;
//                        break;
//                    }
//                }
//                if (hasUrl) continue;
//            }
//            sendCmd(MpdConsts.getCommandStr(MpdConsts.Cmd.MPD_CMD_PLAYLIST_ADD, playlistName, url));
//        }
//        sendCmd(MpdConsts.Cmd.MPD_CMD_PLAYLIST_INFO, playlistName);
//    }
//
//    public void removeFromPlaylistByIndex (String playlistName, List<Integer> index) {
//        //首先要给index排序，防止删除第n个，第n+1就变成第n个，再删除第n+1个会出问题
//        Collections.sort(index, new Comparator<Integer>() {
//            @Override
//            public int compare(Integer lhs, Integer rhs) {
//                return lhs == rhs? 0: lhs > rhs? -1: 1;
//            }
//        });
//        for (int i: index) {
//            sendCmd(MpdConsts.getCommandStr(MpdConsts.Cmd.MPD_CMD_PLAYLIST_DEL, playlistName, Integer.toString(i)));
//        }
//        sendCmd(MpdConsts.Cmd.MPD_CMD_PLAYLIST_INFO, playlistName);
//    }
//
//    @Override
//    public boolean initPlaylist(Playlist playlist) {
//        if (playlist != null
//                && !TextUtils.isEmpty(playlist.getName())
//                && playlist.getTracks() != null
//                && playlist.getTracks().size() > 0) {
//            sendCmd(MpdConsts.Cmd.MPD_CMD_CLEAR);
//            List<Track> tracks = playlist.getTracks();
//            List<String> cmdStrs = new ArrayList<>();
//            for (Track track: tracks) {
//                cmdStrs.add(MpdConsts.getCommandStr(MpdConsts.Cmd.MPD_CMD_ADD, track.file));
//            }
//            sendCmds(cmdStrs, false);
//            refreshCurrentPlaylist();
//            skipToPosition(playlist.getCurrentPos());
//            refreshStatus();
//        }
//        return false;
//    }
//
//    @Override
//    public boolean skipToPosition(int pos) {
//        if (status == null) {
//            return false;
//        }
//        sendCmd(MpdConsts.Cmd.MPD_CMD_PLAY, ""+pos);
//        return true;
//    }
//
//    @Override
//    public boolean stop() {
//        if (status == null) {
//            return false;
//        }
//        sendCmd(MpdConsts.Cmd.MPD_CMD_STOP);
//        return true;
//    }
//
//    @Override
//    public boolean playOrPause() {
//        if (status == null) {
//            return false;
//        }
//        if (status != null && status.state == MediaPlayerConst.PlayerState.PLAYING) {
//            sendCmd(MpdConsts.Cmd.MPD_CMD_PAUSE);
//        } else {
//            sendCmd(MpdConsts.Cmd.MPD_CMD_PLAY);
//        }
//        return true;
//    }
//
//    @Override
//    public boolean prev() {
//        if (status == null) {
//            return false;
//        }
//        sendCmd(MpdConsts.Cmd.MPD_CMD_PREV);
//        return true;
//    }
//
//    @Override
//    public boolean next() {
//        if (status == null) {
//            return false;
//        }
//        sendCmd(MpdConsts.Cmd.MPD_CMD_NEXT);
//        return true;
//    }
//
//    @Override
//    public boolean seek(int pos) {
//        if (status == null) {
//            return false;
//        }
//        sendCmd(MpdConsts.Cmd.MPD_CMD_SEEK, ""+status.songId, ""+pos);
//        return true;
//    }
//
//    @Override
//    public int getPosition() {
//        return currentPlaylist.getCurrentPos();
//    }
//
//    @Override
//    public boolean isPlaying() {
//        if (status != null && status.state == MediaPlayerConst.PlayerState.PLAYING) {
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public void setMode(int mode) {
//        /** mpd 播放模式由三个变量确定，所以要发送三种命令 */
//        if (status == null) {
//            return;
//        }
//        switch (mode) {
//            case MediaPlayerConst.Mode.NORMAL:
//            case MediaPlayerConst.Mode.REPEAT_ALL:
//                sendCmd(MpdConsts.Cmd.MPD_CMD_SINGLE, "0");
//                sendCmd(MpdConsts.Cmd.MPD_CMD_RANDOM, "0");
//                sendCmd(MpdConsts.Cmd.MPD_CMD_REPEAT, "1");
//                break;
//            case MediaPlayerConst.Mode.RANDOM:
//                sendCmd(MpdConsts.Cmd.MPD_CMD_SINGLE, "0");
//                sendCmd(MpdConsts.Cmd.MPD_CMD_RANDOM, "1");
//                sendCmd(MpdConsts.Cmd.MPD_CMD_REPEAT, "0");
//                break;
//            case MediaPlayerConst.Mode.REPEAT_ONE:
//                sendCmd(MpdConsts.Cmd.MPD_CMD_SINGLE, "1");
//                sendCmd(MpdConsts.Cmd.MPD_CMD_RANDOM, "0");
//                sendCmd(MpdConsts.Cmd.MPD_CMD_REPEAT, "1");
//                break;
//        }
//        refreshStatus();
//    }
//
//    @Override
//    public int getMode() {
//        return status == null? MediaPlayerConst.Mode.NORMAL: status.mode;
//    }
//
//    @Override
//    public void setVolume(int volume) {
//        if (volume >= 100) volume = 99;
//        if (volume <= 0) volume = 0;
//        sendCmd(MpdConsts.Cmd.MPD_CMD_SET_VOLUME, ""+volume);
//    }
//
//    @Override
//    public int getVolume() {
//        return status.volume;
//    }
//
//    @Override
//    public boolean isAlive() {
//        return client !=null && client.isConnected();
//    }
//
//    @Override
//    public Playlist getPlaylist() {
//        return currentPlaylist;
//    }
//
//    @Override
//    public void setOnAudioListener(OnAudioListener audioListener) {
//
//    }
//
//    public Map<String, Playlist> getPlaylists() {
//        return playlists;
//    }
}
