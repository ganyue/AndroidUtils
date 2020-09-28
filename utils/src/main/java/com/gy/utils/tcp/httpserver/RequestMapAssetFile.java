package com.gy.utils.tcp.httpserver;

public class RequestMapAssetFile extends RequestMap {
    public String assetPath;
    public RequestMapAssetFile(String path, String asPath) {
        super(path);
        assetPath = asPath;
    }

    @Override
    public String getResponseHead() {
        return "HTTP/1.1 " + 200 + "\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n";
    }
}
