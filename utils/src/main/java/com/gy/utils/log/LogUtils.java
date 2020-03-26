package com.gy.utils.log;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.util.Log;

import com.gy.utils.file.FileUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * <p>Created by yue.gan on 2016/5/23.</p>
 * <p>1、在/sdcard下创建/tmp/logs文件夹下次启动时会自动启用日志并写入/tmp/logs下</p>
 * <p>2、使用广播打开关闭日志：am broadcast -a log.enable(log.disable、log2file.enable、log2file.disable)</p>
 * <p>3、代码中直接使用
 * <p>{@link LogUtils#enableLog(boolean)}</p>
 * <p>{@link LogUtils#enableLogToFile(boolean)}</p>
 * <p>{@link LogUtils#enableLogToFile(Context, boolean)} </p>
 * <p>打开/关闭日志</p>
 */
public class LogUtils {
    private static final String DEFAULT_TAG = "devlog";
    private static boolean isLogEnabled = false;
    private static boolean isLogToFileEnabled = false;
    private static LogThread logThread;
    private static final String DEFAULT_LOG_DIR = "" + Environment.getExternalStorageDirectory() + "/tmp/logs";

    static {
        //如果日志文件夹存在，直接打开日志
        File file = new File(DEFAULT_LOG_DIR);
        if (file.exists() && file.isDirectory()) {
            enableLogToFile(true);
        }
    }

    public static void enableLog (boolean enable) {
        isLogEnabled = enable;
        i("******************↓ ↓ ↓ log begin ↓ ↓ ↓******************");
        i("******************- - - log only  - - -******************");
        i("******************↑ ↑ ↑ log begin ↑ ↑ ↑******************");
    }

    public static void enableLogToFile (boolean enable) {
        enableLogToFile(null, enable);
    }

    public static void enableLogToFile (Context context, boolean enable) {
        isLogEnabled = enable;
        isLogToFileEnabled = enable;
        if (enable && logThread == null) {
            if (logQueue == null) logQueue = new ArrayBlockingQueue<>(128);
            if (context == null) logThread = new LogThread();
            else logThread = new LogThread(context.getExternalCacheDir());
            logThread.start();
            i("******************↓ ↓ ↓ log begin ↓ ↓ ↓******************");
            i("***  logFileDir: " + logThread.getLogFileDir().getPath());
            i("******************↑ ↑ ↑ log begin ↑ ↑ ↑******************");
        } else if (!enable && logThread != null) {
            if (logThread.isAlive() && logQueue.size() <= 0) {
                logThread.interrupt();
            }
            logThread = null;
        }
    }

    public static void d (String tag, String log) {
        if (isLogEnabled) {
            dd(tag, log);
            if (isLogToFileEnabled) d2f(tag + "&nbsp--&nbsp" + log);
        }
    }

    public static void e (String tag, String log) {
        if (isLogEnabled) {
            ee(tag, log);
            if (isLogToFileEnabled) e2f(tag + "&nbsp--&nbsp" + log);
        }
    }

    public static void e (String tag, Throwable t) {
        if (isLogEnabled) {
            String logStr = ee(tag, t);
            if (isLogToFileEnabled) e2f(tag + "&nbsp--&nbsp" + logStr.replaceAll("\n\t", "\n\t<br\\>"));
        }
    }

    public static void i (String tag, String log) {
        if (isLogEnabled) {
            ii(tag, log);
            if (isLogToFileEnabled) i2f(tag + "&nbsp--&nbsp" + log);
        }
    }

    public static void v (String tag, String log) {
        if (isLogEnabled) {
            vv(tag, log);
            if (isLogToFileEnabled) v2f(tag + "&nbsp--&nbsp" + log);
        }
    }

    public static void dd (String tag, String log) {
        Log.d(tag, log);
    }

    public static void ee (String tag, String log) {
        Log.e(tag, log);
    }

    public static String ee (String tag, Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        String logStr = sw.toString();
        Log.e(tag, logStr);
        return logStr;
    }

    public static void ii (String tag, String log) {
        Log.i(tag, log);
    }

    public static void vv (String tag, String log) {
        Log.v(tag, log);
    }

    private static ArrayBlockingQueue<LogItem> logQueue;
    private static void l2f (String str, int logLevel) {
        if (!isLogToFileEnabled) return;
        logQueue.offer(new LogItem(System.currentTimeMillis(), str, logLevel));
    }

    public static void d (String log) {d(DEFAULT_TAG, log);}
    public static void e (String log) {e(DEFAULT_TAG, log);}
    public static void e (Throwable t) {e(DEFAULT_TAG, t);}
    public static void i (String log) {i(DEFAULT_TAG, log);}
    public static void v (String log) {v(DEFAULT_TAG, log);}
    public static void dd (String log) {dd(DEFAULT_TAG, log);}
    public static void ee (String log) {ee(DEFAULT_TAG, log);}
    public static void ee (Throwable t) {ee(DEFAULT_TAG, t);}
    public static void ii (String log) {ii(DEFAULT_TAG, log);}
    public static void vv (String log) {vv(DEFAULT_TAG, log);}
    private static void d2f(String str) {l2f(str, Log.DEBUG); }
    private static void e2f(String str) {l2f(str, Log.ERROR); }
    private static void i2f(String str) {l2f(str, Log.INFO); }
    private static void v2f(String str) {l2f(str, Log.VERBOSE); }

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

    public static void crashLog (Throwable t) {
        if (logThread != null) logThread.writeCrashLogSync(t);
    }

    /**
     * 文件日志线程
     */
    private static class LogThread extends Thread {
        private File logFileDir = new File (DEFAULT_LOG_DIR);
        private String logFileName = "log";
        private int writeCount = 0;
        private SimpleDateFormat logDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        private final long MAX_LOG_SIZE = 10 * 1024 * 1024;

        File getLogFileDir () {
            return logFileDir;
        }

        LogThread() {
        }

        LogThread(File logFileDir) {
            if (logFileDir != null) {
                this.logFileDir = logFileDir;
            }
        }

        private void writeCrashLogSync (Throwable t) {
            logFileName = "log_" + new SimpleDateFormat("yyyy_MM_dd", Locale.getDefault()).format(new Date()) + "_crash";
            logFileName += ".html";
            if (!logFileDir.exists() && !logFileDir.mkdirs()) return;

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            String logStr = sw.toString();
            logStr = logStr.replaceAll("\n\t", "\n\t<br\\>");
            try {
                FileOutputStream fOut = new FileOutputStream(new File(logFileDir, logFileName), true);

                String logHtmlStr = "<p style='color:red'>" +
                        "[time-->&nbsp" +
                        logDateFormat.format(new Date()) +
                        "&nbsp]&nbsp" +
                        "Throwable : " + logStr + "<br\\>" +
                        "</p><br\\>\n\t\n\t";
                fOut.write(logHtmlStr.getBytes());
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void checkLogFileSize () {
            if (!logFileDir.exists() && !logFileDir.mkdirs()) return;
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
                File logFile = new File(logFileDir, logFileName);
                FileOutputStream fOut = new FileOutputStream(logFile, true);
                StringBuilder stringBuilder = new StringBuilder();
                while (isLogToFileEnabled) {
                    LogItem logItem = logQueue.take();
                    if (!logFile.exists()) {
                        if (!logFileDir.exists() && !logFileDir.mkdirs()) continue;
                        closeClosable(fOut);
                        fOut = new FileOutputStream(new File(logFileDir, logFileName), true);
                    }
                    stringBuilder.append("<p");
                    switch (logItem.level) {
                        case Log.INFO:
                            stringBuilder.append(" style='color:green'>");
                            break;
                        case Log.WARN:
                            stringBuilder.append(" style='color:yellow'>");
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
                    if (writeCount >= 3000) {
                        checkLogFileSize();
                        writeCount = 0;
                    }
                }
                closeClosable(fOut);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void closeClosable (Closeable closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static LogEnableReciver logEnableReciver;
    public static void registReceiverForRuntime (Context context) {
        if (logEnableReciver != null) return;
        logEnableReciver = new LogEnableReciver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("log.enable");
        intentFilter.addAction("log.disable");
        intentFilter.addAction("log2file.enable");
        intentFilter.addAction("log2file.disable");
        context.registerReceiver(logEnableReciver, intentFilter);
    }

    public static void unRegistReceiverForRuntime (Context context) {
        if (logEnableReciver != null) {
            context.unregisterReceiver(logEnableReciver);
            logEnableReciver = null;
        }
    }

    private static class LogEnableReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;
            switch (action) {
                case "log.enable":
                    enableLog(true);
                    break;
                case "log.disable":
                    enableLog(false);
                    break;
                case "log2file.enable":
                    enableLogToFile(null, true);
                    break;
                case "log2file.disable":
                    enableLogToFile(context, false);
                    break;
            }
        }
    }
}
