package com.gy.utils.constants;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.gy.utils.security.MD5Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by ganyu on 2016/10/10.
 *
 */
public class DeviceConstants {

    /**
     * 主要用于获取设备唯一识别号，在设备没有序列号、mac地址的情况下
     * 使用当前时间做唯一识别，所以最好保存一份到手机指定文件，在没有
     * 该文件的情况下再使用此方法获取以达到设备唯一识别号的目的。
     */
    public static String getUniqueCode(Context context) {
        String result = null;

        result = Build.SERIAL;

        if (TextUtils.isEmpty(result)) {
            result = getMacAddr();
        }

        if(TextUtils.isEmpty(result)){
            result = getRandUniqeCode(context);
        }

        result = MD5Utils.getStringMD5(result);

        return result;
    }

    private static String getMacAddr () {
        String mac = getMacFromNetworkInterface();
        if (TextUtils.isEmpty(mac)) {
            mac = getMacFromConfigFile();
        }

        return mac;
    }

    private static String getMacFromNetworkInterface() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                byte[] addr = networkInterface.getHardwareAddress();
                if (addr == null || addr.length == 0) continue;
                StringBuilder sb = new StringBuilder();
                for (byte b: addr) {
                    sb.append(String.format("%02x:", b));
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                return sb.toString();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @deprecated
     */
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

    private static String getRandUniqeCode (Context context) {
        String result = "";
        File file = context.getExternalFilesDir(null);
        File uniqueCodeFile = new File (file, "unique_code");
        if (uniqueCodeFile.exists() && uniqueCodeFile.isFile()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(uniqueCodeFile));
                String tmp;
                StringBuilder sb = new StringBuilder();
                while ((tmp = reader.readLine()) != null) {
                    sb.append(tmp);
                }
                reader.close();
                result = sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (TextUtils.isEmpty(result)) {
            result = System.currentTimeMillis()+""+System.nanoTime();
            if (file == null || (!file.exists() && !file.mkdirs())) return result;
            try {
                FileOutputStream fOut = new FileOutputStream(uniqueCodeFile);
                fOut.write(result.getBytes());
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
