package com.gy.utils.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.enums.Opcode;
import org.java_websocket.framing.Framedata;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Collection;

public class WebSocketWrapper {
    private WebSocket mWebSocket;

    public WebSocketWrapper(WebSocket socket) {
        mWebSocket = socket;
    }

    public void close(int i, String s) {
        mWebSocket.close(i, s);
    }

    public void close(int i) {
        mWebSocket.close(i);
    }

    public void close() {
        mWebSocket.close();
    }

    public void closeConnection(int i, String s) {
        mWebSocket.closeConnection(i, s);
    }

    public void send(String s) {
        mWebSocket.send(s);
    }

    public void send(ByteBuffer byteBuffer) {
        mWebSocket.send(byteBuffer);
    }

    public void send(byte[] bytes) {
        mWebSocket.send(bytes);
    }

    public void sendFrame(Framedata framedata) {
        mWebSocket.sendFrame(framedata);
    }

    public void sendFrame(Collection<Framedata> collection) {
        mWebSocket.sendFrame(collection);
    }

    public void sendPing() {
        mWebSocket.sendPing();
    }

    public void sendFragmentedFrame(Opcode opcode, ByteBuffer byteBuffer, boolean b) {
        mWebSocket.sendFragmentedFrame(opcode, byteBuffer, b);
    }

    public boolean hasBufferedData() {
        return mWebSocket.hasBufferedData();
    }

    public InetSocketAddress getRemoteSocketAddress() {
        return mWebSocket.getRemoteSocketAddress();
    }

    public InetSocketAddress getLocalSocketAddress() {
        return mWebSocket.getLocalSocketAddress();
    }

    public boolean isOpen() {
        return mWebSocket.isOpen();
    }

    public boolean isClosing() {
        return mWebSocket.isClosing();
    }

    public boolean isClosed() {
        return mWebSocket.isClosed();
    }

}
