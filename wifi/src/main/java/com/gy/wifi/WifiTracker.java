package com.gy.wifi;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/***
 * 类似Framework中的SettingsLib，WifiTracker用来做Wifi事件接收等处理工作
 *
 * TODO NetworkInfo 被标记@deprecated了，可能需要对Android28做处理
 */
public class WifiTracker {
    // CONFIGURED_NETWORKS_CHANGED_ACTION,LINK_CONFIGURATION_CHANGED_ACTION这两个通知是隐藏的(@hide标记的)
    // 主要是在WifiConfiguration发生变化时发出通知，此时需要重新扫描并更新热点列表
    private final String CONFIGURED_NETWORKS_CHANGED_ACTION = "android.net.wifi.CONFIGURED_NETWORKS_CHANGE";
    private final String LINK_CONFIGURATION_CHANGED_ACTION = "android.net.wifi.LINK_CONFIGURATION_CHANGED";

    private Context mCxt;
    private WifiManager mWm;
    private Scanner mScanner;
    private TrackerListener mListener;
    private boolean mIsTracking = false;
    private WorkHandler mWorkHandler;
    private HandlerThread mWorkThread;
    private final Object mLock = new Object();                      // 主要还是防止mScanResult等多线程操作引起的问题
    private int mScanId = 0;                                        // 作为收到扫描结果（SCAN_RESULTS_AVAILABLE_ACTION）的Id
    private final int NUM_SCANS_TO_CONFIRM_AP_LOSS = 3;             // 如果3次扫描结果都没有某个ScanResult说明已经不可用(即AP对应的mScanId距离本次mScanId大于3)
    private List<AccessPoint> mSortedAccessPoints = new ArrayList<>();  // 缓存扫描结果
    private Map<String, AccessPoint> mAccessPoints = new HashMap<>();   // 缓存扫描结果
    private Map<String, Integer> mAccessPointIds = new HashMap<>();     // 缓存扫描结果及收到该扫描结果时对应的mScanId
    WifiTracker(Context cxt, TrackerListener l) {
        mListener = l;
        mCxt = cxt.getApplicationContext();
        mScanner = new Scanner();
        mWm = (WifiManager) cxt.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        mWorkThread = new HandlerThread("MWifiTracker");
        mWorkThread.start();
        mWorkHandler = new WorkHandler(mWorkThread.getLooper());

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        filter.addAction(CONFIGURED_NETWORKS_CHANGED_ACTION);
        filter.addAction(LINK_CONFIGURATION_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        mCxt.registerReceiver(mReceiver, filter);
    }

    void startTrack () {
        mIsTracking = true;
        resumeScanning();
    }

    void stopTrack () {
        mIsTracking = false;
        pauseScanning();
    }

    void release () {
        try {
            mWm = null;
            mListener = null;
            mScanner.pause();
            mScanner = null;
            mWorkHandler.removeCallbacksAndMessages(null);
            mWorkThread.quit();
            mWorkHandler = null;
            mWorkThread = null;
            mCxt.unregisterReceiver(mReceiver);
            mCxt = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 通知处理相关代码 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ //
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null) return;
            switch (intent.getAction()) {
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                    int wState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                    mWorkHandler.sendMessage(Message.obtain(mWorkHandler, MSG_WIFI_STATE_CHANGED, wState, 0));
                    break;
                case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                    if (mIsTracking) mWorkHandler.sendEmptyMessage(MSG_SCAN_RESULT_AVAILABLE);
                    break;
                case CONFIGURED_NETWORKS_CHANGED_ACTION:
                case LINK_CONFIGURATION_CHANGED_ACTION:
                    if (mIsTracking && mScanner.isScanning()) mWorkHandler.sendEmptyMessage(MSG_SCAN_RESULT_AVAILABLE);
                    break;
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                    if (!mWm.isWifiEnabled()) {
                        clearScanCaches();
                        break;
                    }
                    NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    mWorkHandler.sendMessage(Message.obtain(mWorkHandler, MSG_NETWORK_STATE_CHANGED, 0, 0, networkInfo));
                    break;
                case WifiManager.RSSI_CHANGED_ACTION:
                    break;
            }
        }
    };

    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 所有处理逻辑所用的线程 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ //
    final int MSG_WIFI_STATE_CHANGED = 0;
    final int MSG_SCAN_FAILED = 10;
    final int MSG_SCAN_RESULT_AVAILABLE = 20;
    final int MSG_NETWORK_STATE_CHANGED = 30;
    private class WorkHandler extends Handler {
        public WorkHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SCAN_FAILED:
                    if (mListener != null) mListener.onScanFailed();
                    break;
                case MSG_WIFI_STATE_CHANGED:
                    if (mListener != null) mListener.onWifiStateChanged(msg.arg1);
                    if (mIsTracking && msg.arg1 == WifiManager.WIFI_STATE_ENABLED) resumeScanning();
                    else pauseScanning();
                    break;
                case MSG_SCAN_RESULT_AVAILABLE:
                    if (!mIsTracking) break;
                    updateScanResult(mWm.getScanResults());
                    updateWifiConfiguration();
                    if (mListener != null) mListener.onScanResultUpdate(new ArrayList<>(mSortedAccessPoints));
                    break;
                case MSG_NETWORK_STATE_CHANGED:
                    if (mIsTracking) {
                        NetworkInfo info = (NetworkInfo) msg.obj;
                        updateWifiConfiguration();
                        boolean changed = updateNetworkInfo(info);
                        if(mListener != null && changed) mListener.onScanResultUpdate(new ArrayList<>(mSortedAccessPoints));
                    }
                    break;
            }
        }
    }

    private boolean updateNetworkInfo (NetworkInfo networkInfo) {
        WifiInfo wifiInfo = null;
        try {
            wifiInfo = mWm.getConnectionInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean changed = false;
        for (AccessPoint accessPoint: mSortedAccessPoints) {
            if (accessPoint.updateNetworkInfo(wifiInfo, networkInfo)) {
                changed = true;
            }
        }

        if (changed) {
            synchronized (mLock) {
                Collections.sort(mSortedAccessPoints, new Comparator<AccessPoint>() {
                    @Override
                    public int compare(AccessPoint o1, AccessPoint o2) {
                        return o1.priority > o2.priority? 1: (int) (o1.timestamp - o2.timestamp);
                    }
                });
            }
        }
        return changed;
    }

    @SuppressLint("MissingPermission")
    private void updateWifiConfiguration () {
        synchronized (mLock) {
            try {
                /*需要权限ACCESS_FINE_LOCATION, ACCESS_WIFI_STATE，如果没有的话会报错，
                 * 由于是要做系统应用，这个不用考虑，不过如果用在上线App，这俩必须有*/
                WifiInfo wifiInfo = mWm.getConnectionInfo();
                List<WifiConfiguration> configs = mWm.getConfiguredNetworks();

                for (AccessPoint accessPoint: mSortedAccessPoints) {
                    accessPoint.priority = 10;
                }

                for (WifiConfiguration config: configs) {
                    AccessPoint accessPoint = mAccessPoints.get(config.BSSID);
                    if (accessPoint == null) continue;
                    accessPoint.priority = 20;
                    accessPoint.loadConfig(config);
                    if (wifiInfo != null && accessPoint.bssid.equals(wifiInfo.getBSSID())) accessPoint.priority = 30;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            List<AccessPoint> points = new ArrayList<>(mAccessPoints.values());
            Collections.sort(points, new Comparator<AccessPoint>() {
                @Override
                public int compare(AccessPoint o1, AccessPoint o2) {
                    return o1.priority > o2.priority? 1: (int) (o1.timestamp - o2.timestamp);
                }
            });
            mSortedAccessPoints = points;
        }
    }

    /** 把ScanResult转换成AccessPoint然后更新对应的ScanId,如果存在多次扫描都未扫描到的节点（可能超出范围或已关闭等）删除掉*/
    private void updateScanResult (List<ScanResult> newScanResults) {
        synchronized (mLock) {
            mScanId++;
            for (ScanResult newResult : newScanResults) {
                if (newResult.SSID == null || newResult.SSID.isEmpty()) {
                    continue;
                }
                AccessPoint accessPoint = mAccessPoints.get(newResult.BSSID);
                if (accessPoint == null) accessPoint = new AccessPoint(mCxt, newResult);
                else accessPoint.loadResult(newResult);
                mAccessPoints.put(newResult.BSSID, accessPoint);
                mAccessPointIds.put(newResult.BSSID, mScanId);
            }

            if (mScanId <= NUM_SCANS_TO_CONFIRM_AP_LOSS) return;
            Integer threshold = mScanId - NUM_SCANS_TO_CONFIRM_AP_LOSS;
            Iterator<Map.Entry<String, Integer>> idsIterator = mAccessPointIds.entrySet().iterator();
            while (idsIterator.hasNext()) {
                Map.Entry<String, Integer> entry = idsIterator.next();
                if (entry.getValue() < threshold) {
                    mAccessPoints.remove(entry.getKey());
                    idsIterator.remove();
                }
            }
        }
    }

    private void clearScanCaches () {
        synchronized (mLock) {
            mScanId = 0;
            mAccessPoints.clear();
            mAccessPointIds.clear();
        }
    }

    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 扫描热点相关代码 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ //
    public void forceScan() {
        if (mWm.isWifiEnabled()) mScanner.forceScan();
    }

    public void pauseScanning() {
        mScanner.pause();
    }

    public void resumeScanning() {
        clearScanCaches();
        if (mWm.isWifiEnabled()) mScanner.resume();
    }

    private class Scanner extends Handler {
        final int MSG_SCAN = 0;
        final int WIFI_RESCAN_INTERVAL_MS = 10_000;
        private int mRetry = 0;

        void resume() {
            if (!hasMessages(MSG_SCAN)) sendEmptyMessage(MSG_SCAN);
        }

        void forceScan() {
            removeMessages(MSG_SCAN);
            sendEmptyMessage(MSG_SCAN);
        }

        void pause() {
            mRetry = 0;
            removeMessages(MSG_SCAN);
        }

        boolean isScanning() {
            return hasMessages(MSG_SCAN);
        }

        @Override
        public void handleMessage(Message message) {
            if (message.what != MSG_SCAN) return;
            /* 需要权限ACCESS_FINE_LOCATION 否则会获取不到搜索结果 */
            if (mWm.startScan()) {
                mRetry = 0;
            } else if (++mRetry >= 3) {
                mRetry = 0;
                mWorkHandler.sendEmptyMessage(MSG_SCAN_FAILED);
                return;
            }
            sendEmptyMessageDelayed(MSG_SCAN, WIFI_RESCAN_INTERVAL_MS);
        }
    }

    interface TrackerListener {
        void onWifiStateChanged (int state);
        void onScanFailed ();
        void onScanResultUpdate (List<AccessPoint> results);
    }
}

