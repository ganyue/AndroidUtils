package com.gy.utils.log;

import android.util.Log;

import com.gy.utils.file.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

/**
 * Created by ganyu on 2016/5/23.
 *
 */
public class LogUtils {
    private static boolean isDebug = true;
    private static boolean isLogToFileEnabled = true;
    private static String logFilePath = "/sdcard/temp/log/";
    private static String logFileName = "log";

    static {
        //日志文件总大小不超4M
        Calendar calendar = Calendar.getInstance();
        long fileSize = FileUtils.getFileSize(logFilePath);
        if (fileSize > 4 * 1024 * 1024) {
            FileUtils.deleteFiles(logFilePath, null);
        }
        logFileName = "log_" + calendar.get(Calendar.YEAR) +
                "_" + calendar.get(Calendar.MONTH) +
                "_" + calendar.get(Calendar.DAY_OF_MONTH);
        logFileName += ".txt";
    }

    public static void writeToLogFile (String str) {
//        if (!isLogToFileEnabled) return;
//        d("yue.gan", str);
//        File pathF = new File (logFilePath);
//        if (!pathF.exists() && !pathF.mkdirs()) return;
//        File file = new File(logFilePath, logFileName);
//
//        try {
//            Calendar calendar = Calendar.getInstance();
//            StringBuffer stringBuffer = new StringBuffer();
//            stringBuffer.append("\r\n");
//            stringBuffer.append("[time-->  ");
//            stringBuffer.append(calendar.get(Calendar.HOUR_OF_DAY));
//            stringBuffer.append(":");
//            stringBuffer.append(calendar.get(Calendar.MINUTE));
//            stringBuffer.append(":");
//            stringBuffer.append(calendar.get(Calendar.SECOND));
//            stringBuffer.append("  ]\t");
//            stringBuffer.append(str);
//            FileOutputStream fOut = new FileOutputStream(file, true);
//            fOut.write(stringBuffer.toString().getBytes());
//            fOut.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public static void enable (boolean enable) {
//        isDebug = enable;
        isDebug = true;
    }

    public static void d (String tag, String log) {
        if (isDebug) {
            Log.d(tag, log);
        }
    }

    public static void e (String tag, String log) {
        if (isDebug) {
            Log.e(tag, log);
        }
    }

    public static void i (String tag, String log) {
        if (isDebug) {
            Log.i(tag, log);
        }
    }

    public static void v (String tag, String log) {
        if (isDebug) {
            Log.v(tag, log);
        }
    }
}
