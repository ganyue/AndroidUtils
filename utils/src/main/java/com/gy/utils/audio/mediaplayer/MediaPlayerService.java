package com.gy.utils.audio.mediaplayer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.gy.utils.audio.Track;

/**
 * Created by ganyu on 2016/7/19.
 *
 */
public class MediaPlayerService extends Service{

    public static class Keys {
        public static final String KEY_CMD = "cmd";
        public static final String KEY_TRACK = "track";
    }

    public static class CMD {
        public static final int CMD_UNKNOWN = 0;
        public static final int CMD_PLAY = 1;
        public static final int CMD_STOP = 2;
        public static final int CMD_PLAY_OR_PAUSE = 3;
        public static final int CMD_SEEK = 4;
        public static final int CMD_GET_POS = 5;
    }

    private Track track;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int cmd = intent.getIntExtra(Keys.KEY_CMD, CMD.CMD_UNKNOWN);

        switch (cmd) {
            case CMD.CMD_PLAY:
                track = intent.getParcelableExtra(Keys.KEY_TRACK);
                break;
            case CMD.CMD_STOP:
                break;
            case CMD.CMD_PLAY_OR_PAUSE:
                break;
            case CMD.CMD_SEEK:
                break;
            case CMD.CMD_GET_POS:
                break;
            default:
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
