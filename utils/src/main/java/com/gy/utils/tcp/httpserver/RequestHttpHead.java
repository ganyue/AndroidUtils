package com.gy.utils.tcp.httpserver;

import android.util.Log;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class RequestHttpHead {
    private static final String TAG = "RequestHttpHead";

    public static final String METHOD_GET = "GET";
    public static final String KEY_REFERER = "Referer";
    public static final String KEY_HOST = "Host";

    public Map<String, String> content = new HashMap<>();
    public Map<String, String> params = new HashMap<>();
    public String method;
    public String path;
    public String referPath;
    public String version;

    public RequestHttpHead() {
    }

    public static RequestHttpHead parseHead (String headStr) {
        try {
            RequestHttpHead head = new RequestHttpHead();
            BufferedReader reader = new BufferedReader(new StringReader(headStr));
            String line = reader.readLine();
            String[] infos = line.split(" ");
            infos[1] = infos[1].trim();
            head.method = infos[0].trim();
            head.path = infos[1].trim();
            head.version = infos[2].trim();

            int index;
            /// 解析Head头中的Params
            if (head.method.toUpperCase().contains(METHOD_GET)) {
                index = head.path.indexOf('?');
                if (index > 0) {
                    head.path = head.path.substring(0, index);
                    String paramStr = infos[1].substring(index + 1);
                    String[] kvStrs = paramStr.split("&");
                    for (String kvStr: kvStrs) {
                        String[] sp = kvStr.split("=");
                        head.params.put(sp[0], sp[1]);
                    }
                }
            }

            // 解析Head
            while ((line = reader.readLine()) != null) {
                index = line.indexOf(':');
                if (index <= 0) continue;
                head.content.put(line.substring(0, index), line.substring(index + 1).trim());
            }

            // 解析Referer的Path
            String refererStr = head.content.get(KEY_REFERER);
            if (refererStr != null) {
                index = refererStr.lastIndexOf('/');
                head.referPath = refererStr.substring(index);
                index = head.referPath.indexOf('?');
                if (index > 0) {
                    head.referPath = head.referPath.substring(0, index);
                }
            }

            return head;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "parse failed : e="+e);
        }
        return null;
    }

    public String getHost () {
        return content.get(KEY_HOST);
    }
}
