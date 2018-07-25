package com.gy.utils.log;

import android.util.Log;

import com.gy.utils.file.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ganyu on 2016/5/23.
 *
 */
public class LogUtils {
    private static boolean isDebug = false;
    private static boolean isLogToFileEnabled = false;
    private static String logFilePath = "/tmp/log";
    private static String logFileName = "log";
    public static int writeCount = 0;

    static {
        //日志文件总大小不超4M
        Calendar calendar = Calendar.getInstance();
        checkLogFileSize();
        logFileName = "log_" + calendar.get(Calendar.YEAR) +
                "_" + calendar.get(Calendar.MONTH) +
                "_" + calendar.get(Calendar.DAY_OF_MONTH);
        logFileName += ".html";
    }

    /**
     * 检查日志文件大小，大于2M，删除所有日志
     */
    private static void checkLogFileSize () {
        long fileSize = FileUtils.getFileSize(logFilePath);
        if (fileSize > 2100000) {
            FileUtils.deleteFiles(logFilePath, null);
        }
    }

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private static void writeToLogFile (String str, int logLevel) {
        //每写1000条日志检查一次日志文件大小
        if (writeCount >= 1000) {
            checkLogFileSize();
            writeCount = 0;
        }

        File pathF = new File(logFilePath);
        if (!pathF.exists() && !pathF.mkdirs()) return;
        File file = new File(logFilePath, logFileName);

        try {
            Calendar calendar = Calendar.getInstance();
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("<p");

            switch (logLevel) {
                case Log.INFO:
                    stringBuffer.append(" style='color:green'>");
                    break;
                case Log.ERROR:
                    stringBuffer.append(" style='color:red'>");
                    break;
                default:
                    stringBuffer.append(">");
            }

            stringBuffer.append("[time-->&nbsp");
            stringBuffer.append(simpleDateFormat.format(new Date()));
            stringBuffer.append("&nbsp]&nbsp");
            stringBuffer.append(str);
            stringBuffer.append("</p>");
            FileOutputStream fOut = new FileOutputStream(file, true);
            fOut.write(stringBuffer.toString().getBytes());
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        writeCount++;
    }

    public static void enableLog (boolean enable) {
        isDebug = enable;
    }

    public static void enableLogToFile (boolean enable) {
        isLogToFileEnabled = enable;
    }

    public static void d (String tag, String log) {
        if (isDebug) {
            Log.d(tag, log);
            if (isLogToFileEnabled) writeToLogFile(tag + "&nbsp--&nbsp" + log, Log.DEBUG);
        }
    }

    public static void e (String tag, String log) {
        if (isDebug) {
            Log.e(tag, log);
            if (isLogToFileEnabled) writeToLogFile(tag + "&nbsp--&nbsp" + log, Log.ERROR);
        }
    }

    public static void i (String tag, String log) {
        if (isDebug) {
            Log.i(tag, log);
            if (isLogToFileEnabled) writeToLogFile(tag + "&nbsp--&nbsp" + log, Log.INFO);
        }
    }

    public static void v (String tag, String log) {
        if (isDebug) {
            Log.v(tag, log);
            if (isLogToFileEnabled) writeToLogFile(tag + "&nbsp--&nbsp" + log, Log.VERBOSE);
        }
    }

    public static final String DEFAULT_TAG = "developer";
    public static void d (String log) {d(DEFAULT_TAG, log);}
    public static void e (String log) {e(DEFAULT_TAG, log);}
    public static void i (String log) {i(DEFAULT_TAG, log);}
    public static void v (String log) {v(DEFAULT_TAG, log);}
}
