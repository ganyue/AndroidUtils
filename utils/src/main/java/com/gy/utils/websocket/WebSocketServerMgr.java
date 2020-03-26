package com.gy.utils.websocket;

import com.gy.utils.log.LogUtils;
import com.gy.utils.ref.ComparableWeakRef;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebSocketServerMgr {
    private static final String TAG = "WebSocketServerMgr";

    private static WebSocketServerMgr mInstance;
    public static WebSocketServerMgr getInstance() {
        if (mInstance == null) {
            mInstance = new WebSocketServerMgr();
        }
        return mInstance;
    }

    private InetSocketAddress mAddr;
    private WebSocketServer mServer;
    private WebSocketServerMgr() {
    }

    public void start (String hostName, int port) {
        if (mServer != null) {
            try {
                mServer.stop();
                mServer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mAddr = new InetSocketAddress(hostName, port);
        mServer = new MWebSocketServer(mAddr);
        mServer.setReuseAddr(true);
    }

    public void release () {
        if (mServer != null) {
            try {
                mServer.stop();
                mServer = null;
                mAddr = null;
                mOnMessageListeners.clear();
                mInstance = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<ComparableWeakRef<onMessageListener>> mOnMessageListeners = new ArrayList<>();
    public void addOnMessageListener (onMessageListener listener) {
        if (listener != null && !mOnMessageListeners.contains(listener)) {
            mOnMessageListeners.add(new ComparableWeakRef<>(listener));
        }
    }
    public void removeOnMessageListener (onMessageListener listener) {
        mOnMessageListeners.remove(listener);
    }
    public interface onMessageListener {
        void onMessage (String fromAddr, byte[] msg);
    }

    private class MWebSocketServer extends WebSocketServer {
        private Map<String, ComparableWeakRef<WebSocket>> mSockets = new HashMap<>();

        public MWebSocketServer(InetSocketAddress address) {
            super(address);
        }

        @Override
        public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
            String remoteAddr = webSocket.getRemoteSocketAddress().toString();
            LogUtils.d(TAG, "onOpen : " + remoteAddr);
            if (!mSockets.containsKey(remoteAddr)) {
                mSockets.put(remoteAddr, new ComparableWeakRef<>(webSocket));
            }
        }

        @Override
        public void onClose(WebSocket webSocket, int i, String s, boolean b) {
            String remoteAddr = webSocket.getRemoteSocketAddress().toString();
            LogUtils.d(TAG, "onClose : " + remoteAddr);
            mSockets.remove(remoteAddr);
        }

        @Override
        public void onMessage(WebSocket webSocket, String s) {
            String remoteAddr = webSocket.getRemoteSocketAddress().toString();
            byte[] msg = s.getBytes();
            for (ComparableWeakRef<onMessageListener> ref: mOnMessageListeners) {
                if (ref.get() == null) return;
                ref.get().onMessage(remoteAddr, msg);
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteBuffer message) {
            String remoteAddr = webSocket.getRemoteSocketAddress().toString();
            byte[] msg = message.array();
            for (ComparableWeakRef<onMessageListener> ref: mOnMessageListeners) {
                if (ref.get() == null) return;
                ref.get().onMessage(remoteAddr, msg);
            }
        }

        @Override
        public void onError(WebSocket webSocket, Exception e) {
            webSocket.close();
            String remoteAddr = webSocket.getRemoteSocketAddress().toString();
            mSockets.remove(remoteAddr);
            LogUtils.e(TAG, "onError : " + remoteAddr);
            LogUtils.e(TAG, e);
        }

        @Override
        public void onStart() {
            LogUtils.d(TAG, "onStart");
        }
    }
}
