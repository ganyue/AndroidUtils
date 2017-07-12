package com.gy.utils.screenlocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganyu on 2017/4/21.
 *
 */

public class ScreenLockerUtils {
    private ScreenLockReciever screenLockReciever;
    private WeakReference<Context> context;

    public ScreenLockerUtils (Context context) {
        this.context = new WeakReference<>(context);
        screenLockReciever = new ScreenLockReciever();
        //注册屏幕关闭开启广播监听
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        context.registerReceiver(screenLockReciever, intentFilter);
    }

    private class ScreenLockReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_ON)) {
                if (listeners != null) {
                    for (OnScreenListener listener: listeners) {
                        listener.onScreenOn();
                    }
                }
            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                if (listeners != null) {
                    for (OnScreenListener listener: listeners) {
                        listener.onScreenOff();
                    }
                }
            }
        }
    }

    public void release () {
        if (context != null && context.get() != null)
            context.get().unregisterReceiver(screenLockReciever);
    }

    private List<OnScreenListener> listeners;

    public void addOnScreenListener (OnScreenListener listener) {
        if (listeners == null) listeners = new ArrayList<>();
        if (!listeners.contains(listener)) listeners.add(listener);
    }

    public void removeOnScreenListener (OnScreenListener listener) {
        if (listeners != null && listeners.contains(listener))
        listeners.remove(listener);
    }

    public interface OnScreenListener {
        void onScreenOn ();
        void onScreenOff ();
    }
}
