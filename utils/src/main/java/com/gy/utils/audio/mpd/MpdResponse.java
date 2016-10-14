package com.gy.utils.audio.mpd;

import java.util.List;

/**
 * Created by ganyu on 2016/10/12.
 *
 */
public class MpdResponse {

    public List<String> msg;
    public String cmd;

    public MpdResponse (String cmd, List<String> msg) {
        this.cmd = cmd;
        this.msg = msg;
    }
}
