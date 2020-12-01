package com.gy.wifi;

import android.content.Context;

import com.gy.utils.ref.ComparableWeakRef;

import java.util.ArrayList;
import java.util.List;

public class WifiSettingsManager {

    private static WifiSettingsManager mInstance;
    public static WifiSettingsManager getInstance(Context cxt) {
        if (mInstance == null) mInstance = new WifiSettingsManager(cxt);
        return mInstance;
    }

    private Context mCxt;
    private WifiTracker mWifiTracker;
    private List<ComparableWeakRef<OnWifiStateChangedListener>> mStateListener = new ArrayList<>();
    private List<ComparableWeakRef<OnConnectStateChangedListener>> mCStateListener = new ArrayList<>();
    private WifiSettingsManager(Context cxt) {
        mCxt = cxt.getApplicationContext();
        mWifiTracker = new WifiTracker(mCxt, trackerListener);
    }

    private WifiTracker.TrackerListener trackerListener = new WifiTracker.TrackerListener() {
        @Override
        public void onWifiStateChanged(int state) {

        }

        @Override
        public void onScanFailed() {

        }

        @Override
        public void onScanResultUpdate(List<AccessPoint> results) {

        }
    };

    public void addOnWifiStateChangedListener (OnWifiStateChangedListener l) {
        if (!mStateListener.contains(l)) mStateListener.add(new ComparableWeakRef<>(l));
    }

    public void removeOnWifiStateChangedListener (OnWifiStateChangedListener l) {
        mStateListener.remove(l);
    }

    public void addOnConnectStateChangedListener (OnConnectStateChangedListener l) {
        if (!mCStateListener.contains(l)) mCStateListener.add(new ComparableWeakRef<>(l));
    }

    public void removeOnConnectStateChangedListener (OnConnectStateChangedListener l) {
        mCStateListener.remove(l);
    }
}
