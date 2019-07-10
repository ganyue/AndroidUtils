package com.gy.appbase.application;

import android.content.Context;
import android.os.Looper;
import android.os.Process;
import android.util.Log;

import com.gy.utils.log.LogUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;

/**
 * Created by ganyu on 2016/7/19.
 *
 */
public class DefaultCrashHandler implements Thread.UncaughtExceptionHandler{

    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private WeakReference<Context> mContext;

    public DefaultCrashHandler (Context context) {
        mContext = new WeakReference<>(context);
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        LogUtils.enableLogToFile(true);
        LogUtils.e("********** ↓ ↓ ↓ ↓ ↓ ↓ crash log ↓ ↓ ↓ ↓ ↓ ↓ **********");
        LogUtils.e(""+getStackTrace(ex));
        LogUtils.e("********** ↑ ↑ ↑ ↑ ↑ ↑ crash log ↑ ↑ ↑ ↑ ↑ ↑ **********");
        LogUtils.crashLog(ex);
        LogUtils.enableLogToFile(false);
        Log.e("yue.gan", "unCaughtException : " + ex.toString());
        ex.printStackTrace();

        //其他线程出现异常直接返回，只要不影响主线程就ok
        if (Looper.myLooper() != Looper.getMainLooper()) {
            return;
        }

        if (!handleException(thread, ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            Process.killProcess(Process.myPid());
            System.exit(1);
        }
    }

    public boolean handleException (Thread thread, Throwable ex) {
        ex.printStackTrace();
        return false;
    }

    public String getStackTrace (Throwable ex) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        ex.printStackTrace(printWriter);
        return  result.toString();
    }
}
