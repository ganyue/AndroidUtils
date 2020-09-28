package com.gy.utils.tcp.httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class RequestHttpHead {
    public static final String METHOD_GET = "GET";

    public Map<String, String> content = new HashMap<>();
    public Map<String, String> params = new HashMap<>();
    public String method;
    public String path;
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
            head.path = infos[1];
            head.version = infos[2].trim();

            if (head.method.toUpperCase().contains(METHOD_GET)) {
                int index = infos[1].indexOf('?');
                if (index > 0) {
                    head.path = infos[1].substring(0, index);
                    String paramStr = infos[1].substring(index + 1);
                    String[] kvStrs = paramStr.split("&");
                    for (String kvStr: kvStrs) {
                        String[] sp = kvStr.split("=");
                        head.params.put(sp[0], sp[1]);
                    }
                }
            }

            while ((line = reader.readLine()) != null) {
                if (!line.contains(":")) continue;
                String[] item = line.split(":");
                head.content.put(item[0], item[1]);
            }
            return head;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
