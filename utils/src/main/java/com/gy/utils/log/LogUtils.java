package com.gy.utils.log;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.util.Log;

import com.gy.utils.file.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by ganyu on 2016/5/23.
 *
 */
public class LogUtils {
    private static boolean isDebug = false;
    private static boolean isLogToFileEnabled = false;
    private static LogThread logThread;

    public static void enableLog (boolean enable) {
        isDebug = enable;
    }

    public static void enableLogToFile (boolean enable) {
        isLogToFileEnabled = enable;
        if (enable && logThread == null) {
            logThread = new LogThread();
            logThread.start();
        } else if (!enable && logThread != null) {
            if (logThread.isAlive() && logQueue.size() <= 0) {
                logThread.interrupt();
            }
            logThread = null;
        }
    }

    public static void d (String tag, String log) {
        if (isDebug) {
            Log.d(tag, log);
            if (isLogToFileEnabled) d2f(tag + "&nbsp--&nbsp" + log);
        }
    }

    public static void e (String tag, String log) {
        if (isDebug) {
            Log.e(tag, log);
            if (isLogToFileEnabled) e2f(tag + "&nbsp--&nbsp" + log);
        }
    }

    public static void i (String tag, String log) {
        if (isDebug) {
            Log.i(tag, log);
            if (isLogToFileEnabled) i2f(tag + "&nbsp--&nbsp" + log);
        }
    }

    public static void v (String tag, String log) {
        if (isDebug) {
            Log.v(tag, log);
            if (isLogToFileEnabled) v2f(tag + "&nbsp--&nbsp" + log);
        }
    }

    private static ArrayBlockingQueue<LogItem> logQueue;
    private static void l2f (String str, int logLevel) {
        if (!isLogToFileEnabled) return;
        if (logQueue == null) {
            logQueue = new ArrayBlockingQueue<>(128);
        }
        logQueue.offer(new LogItem(System.currentTimeMillis(), str, logLevel));
    }

    public static final String DEFAULT_TAG = "developer";
    public static void d (String log) {d(DEFAULT_TAG, log);}
    public static void e (String log) {e(DEFAULT_TAG, log);}
    public static void i (String log) {i(DEFAULT_TAG, log);}
    public static void v (String log) {v(DEFAULT_TAG, log);}
    public static void d2f (String str) {l2f(str, Log.DEBUG); }
    public static void e2f (String str) {l2f(str, Log.ERROR); }
    public static void i2f (String str) {l2f(str, Log.INFO); }
    public static void v2f (String str) {l2f(str, Log.VERBOSE); }

    private static class LogItem {
        long time;
        String msg;
        int level;
        LogItem(long time, String msg, int level) {
            this.time = time;
            this.msg = msg;
            this.level = level;
        }
    }

    /**
     * 文件日志线程
     */
    private static class LogThread extends Thread {
        private File logFileDir = new File (Environment.getExternalStorageDirectory(), "tmp/log");
        private String logFileName = "log";
        private int writeCount = 0;
        private SimpleDateFormat logDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        private final long MAX_LOG_SIZE = 10 * 1024 * 1024;

        private void checkLogFileSize () {
            long fileSize = FileUtils.getFileSize(logFileDir.getAbsolutePath());
            if (fileSize > MAX_LOG_SIZE) {
                FileUtils.deleteFiles(logFileDir.getPath(), null);
            }
        }

        @Override
        public void run() {

            checkLogFileSize();
            logFileName = "log_" + new SimpleDateFormat("yyyy_MM_dd", Locale.getDefault()).format(new Date());
            logFileName += ".html";

            try {
                FileOutputStream fOut = new FileOutputStream(new File(logFileDir, logFileName), true);
                StringBuilder stringBuilder = new StringBuilder();
                while (isLogToFileEnabled || logQueue.size() > 0) {
                    LogItem logItem = logQueue.take();
                    stringBuilder.append("<p");
                    switch (logItem.level) {
                        case Log.INFO:
                            stringBuilder.append(" style='color:green'>");
                            break;
                        case Log.ERROR:
                            stringBuilder.append(" style='color:red'>");
                            break;
                        default:
                            stringBuilder.append(">");
                    }
                    stringBuilder.append("[time-->&nbsp");
                    stringBuilder.append(logDateFormat.format(new Date()));
                    stringBuilder.append("&nbsp]&nbsp");
                    stringBuilder.append(logItem.msg);
                    stringBuilder.append("</p>");
                    fOut.write(stringBuilder.toString().getBytes());
                    stringBuilder.setLength(0);

                    writeCount++;
                    if (writeCount >= 1000) {
                        checkLogFileSize();
                        writeCount = 0;
                    }
                }
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static LogEnableReciver logEnableReciver;
    private static void registReceiverForRuntime (Context context) {
        if (logEnableReciver != null) return;
        logEnableReciver = new LogEnableReciver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("log.enable");
        intentFilter.addAction("log.disable");
        intentFilter.addAction("log2file.enable");
        intentFilter.addAction("log2file.disable");
        context.registerReceiver(logEnableReciver, intentFilter);
    }

    private static void unRegistReceiverForRuntime (Context context) {
        if (logEnableReciver != null) {
            context.unregisterReceiver(logEnableReciver);
            logEnableReciver = null;
        }
    }

    private static class LogEnableReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case "log.enable":
                    enableLog(true);
                    break;
                case "log.disable":
                    enableLog(false);
                    break;
                case "log2file.enable":
                    enableLogToFile(true);
                    break;
                case "log2file.disable":
                    enableLogToFile(false);
                    break;
            }
        }
    }
}
