package com.gy.utils.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.gy.utils.ref.ComparableWeakRef;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yue.gan on 2016/7/25.
 *
 */
public class WifiUtils {
    private static WifiUtils mInstance;
    private WeakReference<Context> mCxt;
    private NetStateReceiver netStateReceiver;
    private ConnectivityManager connectivityManager;
    private List<ComparableWeakRef<OnNetworkChangedListener>> listeners;

    public static WifiUtils getInstance (Context context) {
        if (mInstance == null) {
            mInstance = new WifiUtils(context);
        }

        return mInstance;
    }

    private WifiUtils (Context context) {
        mCxt = new WeakReference<>(context.getApplicationContext());
        connectivityManager = (ConnectivityManager) mCxt.get().getSystemService(Context.CONNECTIVITY_SERVICE);
        netStateReceiver = new NetStateReceiver();

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mCxt.get().registerReceiver(netStateReceiver, intentFilter);
    }

    public void addOnNetworkChangedListener (OnNetworkChangedListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<>(8);
        }

        ComparableWeakRef<OnNetworkChangedListener> ref = new ComparableWeakRef<>(listener);
        if (!listeners.contains(ref)) {
            listeners.add(ref);
        }
    }

    /**
     * @param listener .
     * @param callOnAdd .添加后会主动回调一次
     */
    public void addOnNetworkChangedListener (OnNetworkChangedListener listener, boolean callOnAdd) {
        if (listeners == null) {
            listeners = new ArrayList<>(8);
        }

        ComparableWeakRef<OnNetworkChangedListener> ref = new ComparableWeakRef<>(listener);
        if (!listeners.contains(ref)) {
            listeners.add(ref);

            if (callOnAdd) {
                listener.onNetworkChanged(isNetworkConnected(), getConnectedNetworkType());
            }
        }
    }

    public void removeOnNetworkChangedListener (OnNetworkChangedListener listener) {
        if (listeners != null) {
            ComparableWeakRef<OnNetworkChangedListener> ref = new ComparableWeakRef<>(listener);
            listeners.remove(ref);
        }
    }

    public boolean isNetworkConnected () {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public boolean isUseMobileNet () {
        return isNetworkConnected() && (getConnectedNetworkType() == ConnectivityManager.TYPE_MOBILE);
    }

    public int getConnectedNetworkType () {
        if (isNetworkConnected()) {
            return connectivityManager.getActiveNetworkInfo().getType();
        }
        return -1;
    }

    /**
     * 获取当前连接的wifi的ssid,如果不是链接的wifi返回 “”
     */
    public String getConnectedWifiSSid () {
        try {
            WifiManager wifiManager = (WifiManager) mCxt.get().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            String wifiSSID = wifiManager.getConnectionInfo().getSSID();
            if (!TextUtils.isEmpty(wifiSSID)) {
                wifiSSID = wifiSSID.replace("\"", "");
            }
            return wifiSSID;
        } catch (Exception e) {
            return "";
        }
    }


    /**
     * 获取当前连接的wifi的本机ip,如果不是链接的wifi返回 “”
     */
    public String getIp () {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            WifiManager wifiManager = (WifiManager) mCxt.get().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifiManager.getConnectionInfo();
            int ipAddr = info.getIpAddress();
            return int2IpAddr(ipAddr);
        }
        return "";
    }

    private String int2IpAddr(int ipAddr) {
        return ((ipAddr & 0xff) + "." + (ipAddr >> 8 & 0xff) + "."
                + (ipAddr >> 16 & 0xff) + "." + (ipAddr >> 24 & 0xff));
    }

    /**
     * 用ping命令检查外网是否可用
     * 这是个耗时操作
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
        mCxt.get().unregisterReceiver(netStateReceiver);
    }

    class NetStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (listeners == null || listeners.size() <= 0) return;
            for (ComparableWeakRef<OnNetworkChangedListener> listener : listeners) {
                if (listener.get() != null) {
                    listener.get().onNetworkChanged(isNetworkConnected(), getConnectedNetworkType());
                }
            }
        }
    }

    public interface OnNetworkChangedListener {
        /**
         * @param isConnected .当前网络是否可用
         * @param type .网络类型 ConnectivityManager.TYPE_MOBILE
         */
        void onNetworkChanged (boolean isConnected, int type);
    }
}
