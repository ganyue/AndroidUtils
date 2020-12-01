package com.gy.wifi;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.text.TextUtils;

import com.gy.utils.reflect.ReflectUtil;

public class AccessPoint {
    public static final int SECURITY_NONE = 0;
    public static final int SECURITY_WEP = 1;
    public static final int SECURITY_PSK = 2;
    public static final int SECURITY_EAP = 3;

    private static final int PSK_UNKNOWN = 0;
    private static final int PSK_WPA = 1;
    private static final int PSK_WPA2 = 2;
    private static final int PSK_WPA_WPA2 = 3;

    private Context mCxt;
    public String ssid;
    public String bssid;
    public int security;
    public int pskType;
    public int level;
    public long timestamp;
    public boolean isCarrierAp;
    public int carrierApEapType;
    public String carrierName;
    public int networkId = -1;
    public WifiConfiguration cachedConfig;
    private WifiInfo mWifiInfo = null;
    private NetworkInfo mNetworkInfo = null;
    public int priority = 10;//用来做排序，优先级越高越排前
    public AccessPoint (Context cxt, ScanResult result) {
        mCxt = cxt;
        loadResult(result);
    }

    void loadResult (ScanResult result) {
        ssid = result.SSID;
        bssid = result.BSSID;
        security = getSecurity(result);
        pskType = getPskType(result);
        level = result.level;
        timestamp = result.timestamp;
        isCarrierAp = ReflectUtil.getBoolean(result, "isCarrierAp", false);
        carrierApEapType = ReflectUtil.getInteger(result, "carrierApEapType", -1);
        carrierName = ReflectUtil.getString(result, "carrierName", "");
    }

    void loadConfig (WifiConfiguration wifiConfiguration) {
        ssid = (wifiConfiguration.SSID == null ? "" : removeDoubleQuotes(wifiConfiguration.SSID));
        bssid = wifiConfiguration.BSSID;
        security = getSecurity(wifiConfiguration);
        networkId = wifiConfiguration.networkId;
        cachedConfig = wifiConfiguration;
    }

    boolean updateNetworkInfo (WifiInfo wifiInfo, NetworkInfo networkInfo) {
        boolean ret = false;
        if (wifiInfo == null) {
            ret = mWifiInfo != null;
            mWifiInfo = null;
            mNetworkInfo = null;
        } else if (wifiInfo.getBSSID().equals(bssid)) {
            mWifiInfo = wifiInfo;
            mNetworkInfo = networkInfo;
            ret = true;
        } else {
            ret = mWifiInfo != null;
            mWifiInfo = null;
            mNetworkInfo = null;
        }
        return ret;
    }

    private static int getSecurity(ScanResult result) {
        if (result.capabilities.contains("WEP")) return SECURITY_WEP;
        else if (result.capabilities.contains("PSK")) return SECURITY_PSK;
        else if (result.capabilities.contains("EAP")) return SECURITY_EAP;
        return SECURITY_NONE;
    }

    static int getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) return SECURITY_PSK;
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) ||
                config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) return SECURITY_EAP;
        return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
    }

    private static int getPskType(ScanResult result) {
        boolean wpa = result.capabilities.contains("WPA-PSK");
        boolean wpa2 = result.capabilities.contains("WPA2-PSK");
        if (wpa2 && wpa) return PSK_WPA_WPA2;
        else if (wpa2) return PSK_WPA2;
        else if (wpa) return PSK_WPA;
        return PSK_UNKNOWN;
    }

    static String removeDoubleQuotes(String string) {
        if (TextUtils.isEmpty(string)) return "";
        int length = string.length();
        if ((length > 1) && (string.charAt(0) == '"') && (string.charAt(length - 1) == '"')) return string.substring(1, length - 1);
        return string;
    }

    public static String convertToQuotedString(String string) {
        return "\"" + string + "\"";
    }

    public boolean isSaved() {
        return networkId != -1;
    }

    public boolean isPasswordNeeded () {
        return security != SECURITY_NONE;
    }

    public WifiConfiguration getConfig (String pwd) {
        WifiConfiguration config = new WifiConfiguration();
        if (isSaved()) config.networkId = networkId;
        config.SSID = convertToQuotedString(ssid);
        ReflectUtil.setBoolean(config, "shared", true);
        switch (security) {
            case SECURITY_NONE:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;
            case SECURITY_WEP:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                int l = pwd.length();// WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
                if ((l==10||l==26||l==58) && pwd.matches("[0-9A-Fa-f]*")) config.wepKeys[0] = pwd;
                else config.wepKeys[0] = '"' + pwd + '"';
                break;
            case SECURITY_PSK:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                if (pwd.matches("[0-9A-Fa-f]{64}")) config.preSharedKey = pwd;
                else config.preSharedKey = '"' + pwd + '"';
                break;
        }

        return config;
    }

}
