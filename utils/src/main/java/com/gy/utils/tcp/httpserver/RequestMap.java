package com.gy.utils.tcp.httpserver;

public abstract class RequestMap {
    public String path;
    public RequestMap(String p) {
        path = p;
    }
}
