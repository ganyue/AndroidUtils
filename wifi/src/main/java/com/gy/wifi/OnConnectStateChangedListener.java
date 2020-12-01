package com.gy.wifi;

public interface OnConnectStateChangedListener {
    void onConnectStateChanged (boolean isConnected, ConnectType type);
}
