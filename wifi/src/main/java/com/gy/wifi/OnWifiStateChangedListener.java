package com.gy.wifi;

import java.util.List;

public interface OnWifiStateChangedListener {
    void onWifiStateChanged (boolean enabled);
    void onScanResultUpdate ();
    void onScanFailed (List<AccessPoint> results);
}
