package com.gy.utils.wifi;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganyu on 2016/7/25.
 *
 */
public class WifiUtils {
    private static WifiUtils mInstance;
    private WeakReference<Application> mApp;
    private NetStateReceiver netStateReceiver;
    private ConnectivityManager connectivityManager;
    private List<OnNetworkChangedListener> listeners;

    public static WifiUtils getInstance (Application application) {
        if (mInstance == null) {
            mInstance = new WifiUtils(application);
        }

        return mInstance;
    }

    private WifiUtils (Application application) {
        mApp = new WeakReference<>(application);
        connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        netStateReceiver = new NetStateReceiver();

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mApp.get().registerReceiver(netStateReceiver, intentFilter);
    }

    public void addOnNetworkChangedListener (OnNetworkChangedListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }

        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeOnNetworkChangedListener (OnNetworkChangedListener listener) {
        if (listeners != null && listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    public boolean isNetworkConnected () {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public int getConnectedNetworkType () {
        if (isNetworkConnected()) {
            return connectivityManager.getActiveNetworkInfo().getType();
        }
        return -1;
    }

    public String getIp () {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            WifiManager wifiManager = (WifiManager) mApp.get().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifiManager.getConnectionInfo();
            int ipAddr = info.getIpAddress();
            return int2IpAddr(ipAddr);
        }
        return "";
    }

    public String int2IpAddr (int ipAddr) {
        String ip = ((ipAddr & 0xff) + "." + (ipAddr >> 8 & 0xff) + "."
                + (ipAddr >> 16 & 0xff) + "." + (ipAddr >> 24 & 0xff));
        return ip;
    }

    /**
     * 用ping命令检查外网是否可用
     * 这是个耗时操作，最好放到handler里头去做
     */
    public static boolean isInternetConnected() {
        try {
            String ip = "www.baidu.com";
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);
            int status = p.waitFor();
            if (status == 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void release () {
        mApp.get().unregisterReceiver(netStateReceiver);
    }

    class NetStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (listeners == null || listeners.size() <= 0) return;
            for (OnNetworkChangedListener listener : listeners) {
                listener.onNetworkChanged(isNetworkConnected(), getConnectedNetworkType());
            }
        }
    }

    public interface OnNetworkChangedListener {
        void onNetworkChanged (boolean isConnected, int type);
    }
}
