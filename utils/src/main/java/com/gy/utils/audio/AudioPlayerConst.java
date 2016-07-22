package com.gy.utils.audio;

/**
 * Created by ganyu on 2016/7/20.
 *
 */

public class AudioPlayerConst {

    public enum  PlayerState {
        UNINITED,
        PREPARING,
        PREPARED,
        PLAYING,
        PAUSE,
        STOPED
    }

    public static final class Mode {
        public static final int NORMAL = 0;
        public static final int RANDOM = 1;
        public static final int REPEAT_ONE = 2;
        public static final int REPEAT_ALL = 3;
    }

    public static final class PlayerConsts {
        public static final class Keys {
            public static final String KEY_CMD_I = "cmd";
            public static final String KEY_PLAYLIST_O = "playlist";
            public static final String KEY_SEEK_I = "seek";
            public static final String KEY_MODE_I = "mode";

            public static final String KEY_IS_PLAYING_B = "isPlaying";
            public static final String KEY_SENDER_S = "sender";
            public static final String KEY_POSITION_I = "position";
            public static final String KEY_TYPE_I = "type";
            public static final String KEY_EXTRA_I = "extra_i";
            public static final String KEY_OPERATION_I = "operation";
        }

        public static final class BCastType {
            public static final int STATE = 1;
            public static final int COMPLETE = 2;
            public static final int ERROR = 3;
        }

        public static final class Operation {
            public static final int NONE = 0;
            public static final int START = 1;
            public static final int STOP = 2;
            public static final int PLAY = 3;
            public static final int PAUSE = 4;
            public static final int PREV = 5;
            public static final int NEXT = 6;
            public static final int SEEK = 7;
            public static final int MODE_CHANGED = 8;
            public static final int COMPLETE_AUTO_NEXT = 9;
            public static final int ERROR_AUTO_NEXT = 10;
        }

        public static final class Cmds {
            public static final int CMD_UNKNOWN = 0;
            public static final int CMD_PLAY = 1;
            public static final int CMD_STOP = 2;
            public static final int CMD_PLAY_OR_PAUSE = 3;
            public static final int CMD_SEEK = 4;
            public static final int CMD_MODE = 8;
            public static final int CMD_GET_STATE = 7;
        }
    }
}
