package com.gy.utils.tcp.httpserver;

import android.text.TextUtils;

public class RequestMapAssetHtml  extends RequestMap {
    public String filePath;
    public RequestMapAssetHtml(String path, String fPath) {
        super(path);
        filePath = fPath;
    }

    public String getRelativeRootPath () {
        int index = filePath.lastIndexOf('/');
        if (index < 0) return "";
        return filePath.substring(0, index);
    }

    public String getResponseHead(String contentType) {
        return "HTTP/1.1 " + 200 + "\r\n" +
                "Content-Type: " + (TextUtils.isEmpty(contentType)? "text/html" : contentType) + "\r\n" +
                "\r\n";
    }

    @Override
    protected void release() {
    }
}
