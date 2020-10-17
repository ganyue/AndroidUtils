package com.gy.utils.tcp.httpserver;

public interface IHttpClient {
    void sendMsg (String msg);
    void close();
}
