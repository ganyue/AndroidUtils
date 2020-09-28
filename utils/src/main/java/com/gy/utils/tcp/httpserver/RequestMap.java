package com.gy.utils.tcp.httpserver;

import com.gy.utils.tcp.TcpClient;

public abstract class RequestMap {
    public String path;
    public RequestMap(String p) {
        path = p;
    }

    public abstract String getResponseHead();
}
