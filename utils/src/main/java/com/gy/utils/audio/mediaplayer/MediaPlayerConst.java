package com.gy.utils.audio.mediaplayer;

/**
 * Created by ganyu on 2016/7/20.
 *
 */

public class MediaPlayerConst {

    public static final class PlayerConsts {
        public static final class Keys {
            public static final String KEY_CMD_I = "cmd";
            public static final String KEY_SEEK_I = "seek";
            public static final String KEY_SOURCE_PATH = "source_path";
            public static final String KEY_VOLUME_I = "vol";
        }

        public static final class Cmds {
            public static final int CMD_UNKNOWN = 0;
            public static final int CMD_PLAY = 1;
            public static final int CMD_STOP = 2;
            public static final int CMD_PLAY_OR_PAUSE = 3;
            public static final int CMD_SEEK = 4;
            public static final int CMD_SET_VOL = 5;
            public static final int CMD_UPDATE_STATUS = 6;
            public static final int CMD_PAUSE_ONLY = 7;
            public static final int CMD_PLAY_ONLY = 8;
        }
    }

    public static final class BroadCastConsts {
        public static final class Keys {
            public static final String KEY_STATE_I = "cmd";
            public static final String KEY_MEDIA_STATUS_O = "seek";
            public static final String KEY_ERROR_MSG = "error_msg";
        }

        public static final class States {
            public static final int PLAY = 1;
            public static final int PAUSE = 2;
            public static final int STOP = 3;
            public static final int SEEK = 4;
            public static final int COMPLETE = 5;
            public static final int ERROR = 6;
        }
    }
}
