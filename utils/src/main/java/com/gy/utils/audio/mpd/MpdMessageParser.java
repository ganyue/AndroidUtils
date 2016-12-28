package com.gy.utils.audio.mpd;

import com.gy.utils.audio.mpd.beans.MPDStatus;
import com.gy.utils.audio.mpd.beans.Music;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganyu on 2016/8/13.
 *
 */
public class MpdMessageParser {

    public Object parseResponse (String cmd, List<String> response) {
        if (cmd.trim().equals(MpdConsts.Cmd.MPD_CMD_STATUS)) {
            /** 状态 */
            return new MPDStatus(response);
        } else if (cmd.trim().equals(MpdConsts.Cmd.MPD_CMD_LISTALL)) {
            /** 所有文件 */
            return parseFiles(response);
        } else if (cmd.trim().equals(MpdConsts.Cmd.MPD_CMD_LIST)) {
            /** 正在播放列表 */
            return Music.getMusicFromList(response);
        } else if (cmd.trim().contains(MpdConsts.Cmd.MPD_CMD_PLAYLIST_INFO)) {
            /** 指定歌单列表 */
            return Music.getMusicFromList(response);
        } else if (cmd.trim().equals(MpdConsts.Cmd.MPD_CMD_LISTPLAYLISTS)) {
            /** 解析所有歌单的名字，时间目前用不到，暂时不做解析 */
            return parsePlaylistInfos(response);
        }
        /**  */
        return response;
    }

    private List<String> parseFiles (List<String> response){
        List<String> result = new ArrayList<>();

        for (String str: response) {
            if (str.contains("file:")) {
                result.add(str.substring("file:".length()).trim());
            }
        }
        return result;
    }

    private List<String> parsePlaylistInfos (List<String> response){
        List<String> playlistNames = new ArrayList<>();
        for (String str: response) {
            if (str.contains("playlist:")) {
                playlistNames.add(str.substring("playlist:".length()).trim());
            }
        }
        return playlistNames;
    }

}
