package com.gy.utils.audio.mpd;

/**
 * Created by ganyu on 2016/8/12.
 *
 */
public class MpdConsts {

    public static String getCommandStr (String cmd, String... args) {
        StringBuffer outBuf = new StringBuffer();
        outBuf.append(cmd);
        if (args != null) {
            for (String arg : args) {
                if(arg == null)
                    continue;
                arg = arg.replaceAll("\"", "\\\\\"");
                outBuf.append(" \"" + arg + "\"");
            }
        }
        outBuf.append("\n");
        final String outString = outBuf.toString();
        return outString;
    }

    public static class Cmd {
        public static final String MPD_CMD_CLEARERROR = "clearerror";
        public static final String MPD_CMD_CLOSE = "close";
        public static final String MPD_CMD_COUNT = "count";
        public static final String MPD_CMD_CROSSFADE = "crossfade";
        public static final String MPD_CMD_FIND = "find";
        public static final String MPD_CMD_KILL = "kill";
        public static final String MPD_CMD_LIST_TAG = "list";
        public static final String MPD_CMD_LISTALL = "listall";
        public static final String MPD_CMD_LISTALLINFO = "listallinfo";
        public static final String MPD_CMD_LISTPLAYLISTS = "listplaylists";
        public static final String MPD_CMD_LSDIR = "lsinfo";
        public static final String MPD_CMD_NEXT = "next";
        public static final String MPD_CMD_PAUSE = "pause";
        public static final String MPD_CMD_PASSWORD = "password";
        public static final String MPD_CMD_PLAY = "play";
        public static final String MPD_CMD_PLAY_ID = "playid";
        public static final String MPD_CMD_PREV = "previous";
        public static final String MPD_CMD_REFRESH = "update";
        public static final String MPD_CMD_REPEAT = "repeat";
        public static final String MPD_CMD_CONSUME = "consume";
        public static final String MPD_CMD_SINGLE = "single";
        public static final String MPD_CMD_RANDOM = "random";
        public static final String MPD_CMD_SEARCH = "search";
        public static final String MPD_CMD_SEEK = "seek";
        public static final String MPD_CMD_SEEK_ID = "seekid";
        public static final String MPD_CMD_STATISTICS = "stats";
        public static final String MPD_CMD_STATUS = "status";
        public static final String MPD_CMD_STOP = "stop";
        public static final String MPD_CMD_SET_VOLUME = "setvol";
        public static final String MPD_CMD_OUTPUTS = "outputs";
        public static final String MPD_CMD_OUTPUTENABLE = "enableoutput";
        public static final String MPD_CMD_OUTPUTDISABLE = "disableoutput";
        public static final String MPD_CMD_PLAYLIST_INFO = "listplaylistinfo";
        public static final String  MPD_CMD_PLAYLIST_ADD = "playlistadd";
        public static final String MPD_CMD_PLAYLIST_MOVE = "playlistmove";
        public static final String MPD_CMD_PLAYLIST_DEL = "playlistdelete";


        public static final String MPD_CMD_ADD = "add";
        public static final String MPD_CMD_CLEAR = "clear";
        public static final String MPD_CMD_DELETE = "rm";
        public static final String MPD_CMD_LIST = "playlistid";
        public static final String MPD_CMD_CHANGES = "plchanges";
        public static final String MPD_CMD_LOAD = "load";
        public static final String MPD_CMD_MOVE = "move";
        public static final String MPD_CMD_MOVE_ID = "moveid";
        public static final String MPD_CMD_REMOVE = "delete";
        public static final String MPD_CMD_REMOVE_ID = "deleteid";
        public static final String MPD_CMD_SAVE = "save";
        public static final String MPD_CMD_SHUFFLE = "shuffle";
        public static final String MPD_CMD_SWAP = "swap";
        public static final String MPD_CMD_SWAP_ID = "swapid";

        // deprecated commands
        public static final String MPD_CMD_VOLUME = "volume";
        public static final String MPD_FIND_ALBUM = "album";
        public static final String MPD_FIND_ARTIST = "artist";

        public static final String MPD_SEARCH_ALBUM = "album";
        public static final String MPD_SEARCH_ARTIST = "artist";
        public static final String MPD_SEARCH_FILENAME = "filename";
        public static final String MPD_SEARCH_TITLE = "title";
        public static final String MPD_SEARCH_GENRE = "genre";

        public static final String MPD_TAG_ALBUM = "album";
        public static final String MPD_TAG_ARTIST = "artist";
        public static final String MPD_TAG_ALBUM_ARTIST = "albumartist";
        public static final String MPD_TAG_GENRE = "genre";
    }

    public static class MpdState {
        public static final String MPD_STATE_PLAYING = "play";
        public static final String MPD_STATE_STOPPED = "stop";
        public static final String MPD_STATE_PAUSED = "pause";
        public static final String MPD_STATE_UNKNOWN = "unknown";
    }

    public static class MpdKeys {
        public static final String PLAYLIST = "playlist";
        public static final String LAST_MODIFIED = "Last-Modified";
        public static final String FILE = "file";
        public static final String ARTIST = "Artist";
        public static final String ALBUM = "Album";
        public static final String GENRE = "Genre";
        public static final String TIME = "Time";
        public static final String ID = "Id";
    }

}
