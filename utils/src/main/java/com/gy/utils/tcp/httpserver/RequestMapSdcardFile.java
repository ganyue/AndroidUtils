package com.gy.utils.tcp.httpserver;

public class RequestMapSdcardFile extends RequestMap {
    public String filePath;
    public String fileName;
    public RequestMapSdcardFile(String path, String fPath) {
        super(path);
        filePath = fPath;
        int index = fPath.lastIndexOf('/');
        if (index < 0) fileName = fPath;
        else fileName = fPath.substring(index);
    }

    public String getResponseHead() {
        return "HTTP/1.1 " + 200 + "\r\n" +
                "Content-Disposition: attachment; filename=" + fileName + "\r\n" +
                "\r\n";
    }

    @Override
    protected void release() {
    }
}
