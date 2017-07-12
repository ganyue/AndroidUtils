package com.gy.appbase.application;

import android.content.Context;
import android.os.Looper;
import android.os.Process;
import android.util.Log;

import com.gy.utils.log.LogUtils;

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
        LogUtils.writeToLogFile("******* crash log ******* : "+getStackTrace(ex));
        Log.e("yue.gan", "unCaughtException : " + ex.toString());
        ex.printStackTrace();

//        //其他线程出现异常直接返回，只要不影响主线程就ok
//        if (Looper.myLooper() != Looper.getMainLooper()) {
//            return;
//        }

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
        StringBuffer err = new StringBuffer();
        err.append(toString());
        err.append("\n");

        StackTraceElement[] stack = ex.getStackTrace();
        if (stack != null) {
            for (int i = 0; i < stack.length; i++) {
                err.append("\tat ");
                err.append(stack[i].toString());
                err.append("\n");
            }
        }

        Throwable cause = ex.getCause();
        if (cause != null) {
            err.append("Caused by: ");
            err.append(cause.toString());
            stack = cause.getStackTrace();
            if (stack != null) {
                for (int i = 0; i < stack.length; i++) {
                    err.append("\tat ");
                    err.append(stack[i].toString());
                    err.append("\n");
                }
            }
        }
        return err.toString();
    }
}
