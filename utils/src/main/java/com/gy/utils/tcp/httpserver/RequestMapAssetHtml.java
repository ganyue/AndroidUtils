package com.gy.utils.tcp.httpserver;

public class RequestMapAssetHtml  extends RequestMap {
    public String filePath;
    public RequestMapAssetHtml(String path, String fPath) {
        super(path);
        filePath = fPath;
    }

    public String getResponseHead() {
        return "HTTP/1.1 " + 200 + "\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n";
    }
}
