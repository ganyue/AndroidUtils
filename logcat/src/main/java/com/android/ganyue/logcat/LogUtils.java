package com.android.ganyue.logcat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import com.gy.utils.tcp.httpserver.HttpServer;
import com.gy.utils.tcp.httpserver.IHttpClient;
import com.gy.utils.tcp.httpserver.RequestHttpHead;
import com.gy.utils.tcp.httpserver.RequestMapCustomHtmlResFromAssets;
import com.gy.utils.tcp.httpserver.RequestMapWebSocket;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * <p>Created by yue.gan on 2016/5/23.</p>
 * <p>1、在/sdcard下创建/tmp/logs文件夹下次启动时会自动启用日志并写入/tmp/logs下</p>
 * <p>2、使用广播打开关闭日志：am broadcast -a log.enable(log.disable、log2file.enable、log2file.disable)</p>
 * <p>3、代码中直接使用
 * <p>{@link LogUtils#enableLog(boolean)}</p>
 * <p>{@link LogUtils#enableLogToFile(Context, boolean)} </p>
 * <p>打开/关闭日志</p>
 */
public class LogUtils {
    private static final String DEFAULT_TAG = "devlog";
    private static boolean isLogEnabled = false;
    private static boolean isLogToFileEnabled = false;
    private static LogThread logThread;

    private static HttpServer httpServer;
    private static List<IHttpClient> httpClients = new ArrayList<>();
    public static void enableLogServer (final Context cxt, boolean enable, int port) {
        enableLogToFile(cxt, true);
        if (!enable) {
            if (httpServer != null) httpServer.release();
            return;
        }
        if (httpServer == null) {
            httpServer = new HttpServer(cxt, port)
                    .addAssetHtml("/asset_log_html", "logcat_html_res/index.html")
                    .addCustomHtmlResFromAssets("/log", "logcat_html_res", htmlSupplier)
                    .addSdcardFile("/download_log", logThread.getLogFile().getPath())
                    .addSdcardFile("/download_crashLog", logThread.getCrashLogFile().getPath())
                    .addWebSocket("/log_web_socket", onWebSocketCallback)
                    .setServerCallback(new HttpServer.HttpServerCallback() {
                        @Override
                        public void onStartFailed(Exception e) {
                            dd("log server start failed e -->\n" + e.toString());
                        }

                        @Override
                        public void onStartSuccess(String ip, int port) {
                            dd(""+httpServer.getHostAddress());
                        }
                    }).start();
        } else if (!TextUtils.isEmpty(httpServer.getHostAddress())) {
            dd(httpServer.getHostAddress());
        }
    }

    public static void enableLog (boolean enable) {
        isLogEnabled = enable;
    }

    public static void enableLogToFile (Context context, boolean enable) {
        isLogEnabled = enable;
        isLogToFileEnabled = enable;
        if (enable && logThread == null) {
            if (logQueue == null) logQueue = new ArrayBlockingQueue<>(128);
            logThread = new LogThread(new File (context.getExternalCacheDir(), "logs"));
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
            if (isLogToFileEnabled) e2f(tag + "&nbsp--&nbsp" +
                    logStr.replaceAll("\n\t", "\n\t<br\\>"));
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
        static SimpleDateFormat logDateFormat =
                new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        long time;
        String msg;
        int level;
        LogItem(long time, String msg, int level) {
            this.time = time;
            this.msg = msg;
            this.level = level;
        }

        public String toHtmlString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<p");
            switch (level) {
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
            stringBuilder.append(logDateFormat.format(new Date(time)));
            stringBuilder.append("&nbsp]&nbsp");
            stringBuilder.append(msg);
            stringBuilder.append("</p>");
            return stringBuilder.toString();
        }

        @Override
        public String toString() {
            return "\r\n[time--> " + logDateFormat.format(new Date(time)) + " ] " + msg;
        }
    }

    public static void crashLog (Throwable t) {
        if (logThread != null) logThread.writeCrashLogSync(t);
    }

    /**
     * 文件日志线程
     */
    private static class LogThread extends Thread {
        private File logFileDir;
        private String logFileName = "log";
        private String crashLogFileName = "logCrash";
        private int writeCount = 0;
        private SimpleDateFormat logFileNameDateFormat =
                new SimpleDateFormat("yyyy_MM_dd", Locale.getDefault());
        private final long MAX_LOG_SIZE = 10 * 1024 * 1024;

        File getLogFileDir () {
            return logFileDir;
        }

        File getLogFile () {
            return new File(logFileDir, logFileName);
        }

        File getCrashLogFile () {
            return new File(logFileDir, crashLogFileName);
        }

        LogThread(File logFileDir) {
            if (logFileDir != null) {
                this.logFileDir = logFileDir;
                logFileName = "log_" + logFileNameDateFormat.format(new Date()) + ".html";
                crashLogFileName = "log_" + logFileNameDateFormat.format(new Date()) + "_crash.html";
            }
        }

        private void writeCrashLogSync (Throwable t) {
            if (!logFileDir.exists() && !logFileDir.mkdirs()) return;

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            String logStr = sw.toString();
            logStr = logStr.replaceAll("\n\t", "\n\t<br\\>");
            LogItem logItem = new LogItem(System.currentTimeMillis(), logStr, Log.ERROR);
            try {
                FileOutputStream fOut = new FileOutputStream(getCrashLogFile(), true);
                String logHtmlStr = logItem.toHtmlString() + "<br\\>\n\t\n\t";
                fOut.write(logHtmlStr.getBytes());
                fOut.close();

                if (httpClients.size() > 0) {
                    List<IHttpClient> tmp = new ArrayList<>(httpClients);
                    for (IHttpClient client: tmp) {
                        client.sendMsg(logItem.toString());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void checkLogFileSize () {
            if (!logFileDir.exists() && !logFileDir.mkdirs()) return;
            long fileSize = getFileSize(logFileDir);
            if (fileSize > MAX_LOG_SIZE) {
                deleteFiles(logFileDir);
            }
        }

        @Override
        public void run() {
            checkLogFileSize();
            try {
                File logFile = getLogFile();
                FileOutputStream fOut = new FileOutputStream(logFile, true);
                while (isLogToFileEnabled) {
                    LogItem logItem = logQueue.take();
                    if (!logFile.exists()) {
                        if (!logFileDir.exists() && !logFileDir.mkdirs()) break;
                        closeClosable(fOut);
                        fOut = new FileOutputStream(logFile, true);
                    }
                    fOut.write(logItem.toHtmlString().getBytes());

                    if (httpClients.size() > 0) {
                        List<IHttpClient> tmp = new ArrayList<>(httpClients);
                        for (IHttpClient client: tmp) {
                            client.sendMsg(logItem.toString());
                        }
                    }

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

        private long getFileSize (File file) {
            if (!file.exists()) {
                return 0;
            }
            long size = 0;
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files == null) {
                    return 0;
                }
                for (File f: files) {
                    size += getFileSize(f);
                }
            } else {
                try {
                    FileInputStream fin = new FileInputStream(file);
                    FileChannel fileChannel = fin.getChannel();
                    size = fileChannel.size();
                    fileChannel.close();
                    fin.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return size;
        }

        public static void deleteFiles(File file) {
            if (!file.exists()) return;
            if (!file.isDirectory()) {
                safeDeleteFile(file);
                return;
            }
            File[] files = file.listFiles();
            if (files == null || files.length <= 0) {
                safeDeleteFile(file);
                return;
            }
            for (File f : files) {
                deleteFiles(f);
            }
            safeDeleteFile(file);
        }

        public static void safeDeleteFile (File file) {
            if (file == null || !file.exists()) return;
            boolean result = file.delete();
            if (result || file.isDirectory()) return;
            final File to = new File(file.getParentFile(), "tmp_" + System.currentTimeMillis());
            //重命名是为了防止文件名字超过系统限制导致的无法删除的情形
            result = file.renameTo(to);
            if (!result) return;
            if (!to.delete()) d("File Delete Failed path -> " + file.getPath());
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

    private static RequestMapCustomHtmlResFromAssets.HtmlSupplier htmlSupplier =
            new RequestMapCustomHtmlResFromAssets.HtmlSupplier() {
        @Override
        public String getHtml(RequestHttpHead head) {
            return "redirect:/asset_log_html";
        }

        @Override
        public String getReferHtml(String refer, RequestHttpHead head) {
            return httpServer.hasPath(refer)? "redirect:" + refer: null;
        }
    };

    private static RequestMapWebSocket.OnWebSocketCallback onWebSocketCallback =
            new RequestMapWebSocket.OnWebSocketCallback() {
                @Override
                public void onWebSocketConnect(IHttpClient client) {
                    if (!httpClients.contains(client)) httpClients.add(client);
                }

                @Override
                public void onWebSocketReceive(IHttpClient client, String msg) {
                    if (!httpClients.contains(client)) httpClients.add(client);
                    dd(msg);
                    //TODO handle msg
                }

                @Override
                public void onWebSocketClose(IHttpClient client) {
                    synchronized (this) {
                        httpClients.remove(client);
                    }
                }
            };
}
