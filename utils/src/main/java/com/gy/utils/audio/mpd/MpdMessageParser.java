package com.gy.utils.audio.mpd;

import com.gy.utils.audio.Album;
import com.gy.utils.audio.AudioPlayerConst;
import com.gy.utils.audio.Playlist;
import com.gy.utils.audio.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganyu on 2016/8/13.
 *
 */
public class MpdMessageParser {

    /**状态信息，包括播放模式，声音大小，正在播放歌曲进度，id，专辑等信息*/
    public static MpdStatus parseStatus (MpdStatus status, List<String> receiveStr) {
        for (String line : receiveStr) {
            try {
                if (line.startsWith("volume:")) {
                    status.volume = Integer.parseInt(line.substring("volume: ".length()));
                } else if (line.startsWith("bitrate:")) {
                    status.bitrate = Long.parseLong(line.substring("bitrate: ".length()));
                } else if (line.startsWith("playlist:")) {
                    status.playlist = Integer.parseInt(line.substring("playlist: ".length()));
                } else if (line.startsWith("playlistlength:")) {
                    status.playlistLength = Integer.parseInt(line.substring("playlistlength: ".length()));
                } else if (line.startsWith("song:")) {
                    status.song = Integer.parseInt(line.substring("song: ".length()));
                } else if (line.startsWith("songid:")) {
                    status.songId = Integer.parseInt(line.substring("songid: ".length()));
                } else if (line.startsWith("repeat:")) {
                    status.repeat = "1".equals(line.substring("repeat: ".length())) ? true : false;
                } else if (line.startsWith("random:")) {
                    status.random = "1".equals(line.substring("random: ".length())) ? true : false;
                } else if (line.startsWith("state:")) {
                    String state = line.substring("state: ".length());
                    if (MpdConsts.MpdState.MPD_STATE_PAUSED.equals(state)) {
                        status.state = AudioPlayerConst.PlayerState.PAUSE;
                    } else if (MpdConsts.MpdState.MPD_STATE_PLAYING.equals(state)) {
                        status.state = AudioPlayerConst.PlayerState.PLAYING;
                    } else if (MpdConsts.MpdState.MPD_STATE_STOPPED.equals(state)) {
                        status.state = AudioPlayerConst.PlayerState.STOP;
                    } else {
                        status.state = AudioPlayerConst.PlayerState.PAUSE;
                    }
                } else if (line.startsWith("error:")) {
                    status.error = line.substring("error: ".length());
                } else if (line.startsWith("time:")) {
                    String[] time = line.substring("time: ".length()).split(":");
                    status.elapsedTime = Long.parseLong(time[0]);
                    status.totalTime = Long.parseLong(time[1]);
                } else if (line.startsWith("audio:")) {
                    String[] audio = line.substring("audio: ".length()).split(":");
                    try {
                        status.sampleRate = Integer.parseInt(audio[0]);
                        status.bitsPerSample = Integer.parseInt(audio[1]);
                        status.channels = Integer.parseInt(audio[2]);
                    } catch (NumberFormatException e) {
                        // Sometimes mpd sends "?" as a sampleRate or bitsPerSample, etc ... hotfix for a bugreport I had.
                    }
                } else if (line.startsWith("xfade:")) {
                    status.crossfade = Integer.parseInt(line.substring("xfade: ".length()));
                } else if (line.startsWith("updating_db:")) {
                    status.updating = true;
                } else if (line.startsWith("nextsong:")) {
                    status.nextSong = Integer.parseInt(line.substring("nextsong: ".length()));
                } else if (line.startsWith("nextsongid:")) {
                    status.nextSongId = Integer.parseInt(line.substring("nextsongid: ".length()));
                } else if (line.startsWith("consume:")) {
                    status.consume = "1".equals(line.substring("consume: ".length())) ? true : false;
                } else if (line.startsWith("single:")) {
                    status.single = "1".equals(line.substring("single: ".length())) ? true : false;
                } else {
                    // TODO : This floods logcat too much, will fix later
                    // (new InvalidResponseException("unknown response: " + line)).printStackTrace();
                }
            } catch (RuntimeException e) {
                // Do nothing, these should be harmless
                e.printStackTrace();
            }
        }//end of for

        if (status.single && status.repeat) {
            status.mode = AudioPlayerConst.Mode.REPEAT_ONE;
        } else if (!status.single && status.repeat) {
            status.mode = AudioPlayerConst.Mode.REPEAT_ALL;
        } else if (status.random && !status.single && !status.repeat) {
            status.mode = AudioPlayerConst.Mode.RANDOM;
        } else {
            status.mode = AudioPlayerConst.Mode.NORMAL;
        }
        return status;
    }

    /**歌单名字列表，只包含playlist和最近修改时间*/
    public static List<String> parsePlaylistNames (List<String> playlistNames, List<String> recieveStr) {
        /**解析所有歌单名字*/
        for (String line: recieveStr) {
            if (line.startsWith(MpdConsts.MpdKeys.PLAYLIST)) {
                playlistNames.add(line.substring(MpdConsts.MpdKeys.PLAYLIST.length() + 1).trim());
            }
        }
        return playlistNames;
    }

    public static Playlist parsePlaylist (List<String> receiveStr) {
        Playlist playlist = new Playlist();
        Album album = new Album();
        List<Track> tracks = new ArrayList<>();
        ;
        for (String line: receiveStr) {
            if (line.startsWith(MpdConsts.MpdKeys.FILE)) {
                tracks.add(new Track());
                tracks.get(tracks.size() - 1).file = line.substring(MpdConsts.MpdKeys.FILE.length() + 1).trim();
            } else if (line.startsWith(MpdConsts.MpdKeys.ARTIST)) {
                tracks.get(tracks.size() - 1).singer = line.substring(MpdConsts.MpdKeys.ARTIST.length() + 1).trim();
            } else if (line.startsWith(MpdConsts.MpdKeys.TIME)) {
                tracks.get(tracks.size() - 1).duration = Integer.parseInt(line.substring(MpdConsts.MpdKeys.TIME.length() + 1).trim());
            }else if (line.startsWith(MpdConsts.MpdKeys.ALBUM)) {
                album.name = line.substring(MpdConsts.MpdKeys.ALBUM.length() + 1).trim();
            }//TODO 还有其他字段，目前用不到，用到再加吧
        }

        playlist.setAlbum(album);
        playlist.setTracks(tracks);

        return playlist;
    }
}
