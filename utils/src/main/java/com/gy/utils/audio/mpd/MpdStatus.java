package com.gy.utils.audio.mpd;

import com.gy.utils.audio.AudioPlayerConst;

import java.io.StringReader;

/**
 * Created by ganyu on 2016/8/12.
 *
 */
public class MpdStatus {

    public int volume;
    public boolean repeat;
    public boolean random;
    public boolean single;
    public int mode;
    public boolean consume;
    public int playlist;
    public int playlistLength;
    public AudioPlayerConst.PlayerState state;
    public int song;
    public int songId;
    public long elapsedTime;
    public long totalTime;
    public long bitrate;
    public int nextSong;
    public int nextSongId;

    public int sampleRate;
    public int bitsPerSample;
    public int channels;

    public String error;
    public int crossfade;
    public boolean updating;

    public MpdStatus () {
    }
}
