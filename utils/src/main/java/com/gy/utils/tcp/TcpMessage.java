package com.gy.utils.tcp;

/**
 * Created by sam_gan on 2016/6/3.
 */
public class TcpMessage {
    public String ip;
    public int port;
    public String message;

    public TcpMessage (String ip, int port, String msg) {
        this.ip = ip;
        this.port = port;
        this.message = msg;
    }
}
