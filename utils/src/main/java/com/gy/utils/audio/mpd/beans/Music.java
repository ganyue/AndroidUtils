package com.gy.utils.audio.mpd.beans;

import android.text.TextUtils;

import com.gy.utils.http.HttpUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Music {
    public String file;
    public long time;
    public int mpdId;
    public int pos;

    public Music () {}

    Music(List<String> response) {
        for (String line : response) {
            if (line.startsWith("file:")) {
                file = line.substring("file: ".length());
            } else if (line.startsWith("Time:")) {
                try {
                    time = Long.parseLong(line.substring("Time: ".length()));
                } catch (NumberFormatException e) {
                }
            } else if (line.startsWith("Id:")) {
                try {
                    mpdId = Integer.parseInt(line.substring("Id: ".length()));
                } catch (NumberFormatException e) {
                }
            } else if (line.startsWith("Pos:")) {
                try {
                    pos = Integer.parseInt(line.substring("Pos: ".length()));
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    private String getStreamName() {
        if (null != file && !file.isEmpty()) {
            int pos = file.indexOf("#");
            if (pos > 1) {
                return file.substring(pos + 1, file.length());
            }
        }
        return null;
    }

    public static String getId (String file) {
        if (!TextUtils.isEmpty(file)) {
            if (isStream(file)) {
                Map<String, String> params = HttpUtils.getUrlParams(file);
                if (params != null && params.containsKey("id")) return params.get("id");
            } else {
                String fileName = file.toLowerCase();
                if (fileName.startsWith("a") && fileName.endsWith(".mp3")) {
                    return fileName.substring(1, fileName.length() - 4);
                }
            }
        }
        return null;
    }

    public static String id2FileName (String id) {
        if (TextUtils.isEmpty(id)) return null;
        String tmp = id.toLowerCase();
        if (!tmp.startsWith("a")) {
            tmp = "A" + tmp;
        } else {
            tmp = "A" + tmp.substring(1);
        }
        if (!tmp.endsWith(".mp3")) tmp = tmp + ".mp3";
        return tmp;
    }

    public static List<Music> getMusicFromList(List<String> response) {
        ArrayList<Music> result = new ArrayList<Music>();
        LinkedList<String> lineCache = new LinkedList<String>();

        for (String line : response) {
            if (line.startsWith("file: ")) {
                if (lineCache.size() != 0) {
                    result.add(new Music(lineCache));
                    lineCache.clear();
                }
            }
            lineCache.add(line);
        }

        if (lineCache.size() != 0) {
            result.add(new Music(lineCache));
        }

        return result;
    }

    public boolean isStream() {
        return null != file && file.contains("://");
    }
    public static boolean isStream(String file) {
        return null != file && file.contains("://");
    }

}