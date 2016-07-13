package com.gy.utils.udp;

/**
 * Created by ganyu on 2016/5/20.
 *
 */
public class UdpMessage {
    public String message;
    public String ip;
    public int port;

    public UdpMessage (String message, String ip, int port) {
        this.message = message;
        this.ip = ip;
        this.port = port;
    }
}
