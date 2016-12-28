package com.gy.appbase.application;

import android.content.Context;
import android.os.Process;

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
}
