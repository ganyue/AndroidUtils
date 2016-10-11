package com.gy.utils.constants;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;

import com.gy.utils.security.MD5Utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Random;

/**
 * Created by ganyu on 2016/10/10.
 *
 */
public class DeviceConstants {

    public static String getUniqueCode(Context context) {
        String result = null;

        result = Build.SERIAL;

        if (TextUtils.isEmpty(result)) {
            result = getMacAddr(context);
        }

        if(TextUtils.isEmpty(result)){
            Random random=new Random();
            result = System.currentTimeMillis()+""+random.nextInt(10000);
        }

        result = MD5Utils.getStringMD5(result);
        return result;
    }

    private static String getMacAddr (Context context) {
        String mac = getMacFromWifiManager(context);
        if (TextUtils.isEmpty(mac)) {
            mac = getMacFromConfigFile();
        }

        return mac;
    }

    private static String getMacFromWifiManager (Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            return null;
        }

        return wifiInfo.getMacAddress();
    }

    private static String getMacFromConfigFile () {
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);


            for (; null != str;) {
                str = input.readLine();
                if (str != null) {
                    return str.trim();// 去空格
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return null;
    }
}
