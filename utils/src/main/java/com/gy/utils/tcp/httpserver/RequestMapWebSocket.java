package com.gy.utils.tcp.httpserver;

public class RequestMapWebSocket extends RequestMap {

    private OnWebSocketCallback mCallback;
    public RequestMapWebSocket(String path, OnWebSocketCallback callback) {
        super(path);
        mCallback = callback;
    }

    public OnWebSocketCallback getCallback () {
        return mCallback;
    }

    @Override
    protected void release() {
        mCallback = null;
    }

    public interface OnWebSocketCallback {
        void onWebSocketConnect (IHttpClient client);
        void onWebSocketReceive (IHttpClient client, String msg);
        void onWebSocketClose (IHttpClient client);
    }
}
