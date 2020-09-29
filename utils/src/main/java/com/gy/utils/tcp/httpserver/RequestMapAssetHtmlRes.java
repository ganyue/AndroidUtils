package com.gy.utils.tcp.httpserver;

public class RequestMapAssetHtmlRes extends RequestMap {
    public String filePath;
    public RequestMapAssetHtmlRes(String path, String fPath) {
        super(path);
        filePath = fPath;
    }

    public String getResponseHead(String fileName) {
        return "HTTP/1.1 " + 200 + "\r\n" +
                "Content-Disposition: attachment; filename=" + fileName + "\r\n" +
                "\r\n";
    }
}
