package com.gy.utils.phone;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yue.gan on 2016/10/10.
 *
 */
public class PhoneUtils {
    private static PhoneUtils mInstance;
    private WeakReference<Application> mApp;
    private TelephonyManager telephonyManager;
    private List<OnPhoneStateListener> listeners;
    private PhoneStateReciever phoneStateReciever;

    public static PhoneUtils getInstance (Application application) {
        if (mInstance == null) {
            mInstance = new PhoneUtils(application);
        }

        return mInstance;
    }

    private PhoneUtils (Application application) {
        mApp = new WeakReference<>(application);
        telephonyManager = (TelephonyManager) application.getSystemService(Context.TELEPHONY_SERVICE);

        phoneStateReciever = new PhoneStateReciever();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        application.registerReceiver(phoneStateReciever, intentFilter);
    }

    public void addOnPhoneStateListener (OnPhoneStateListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }

        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeOnPhoneStateListener (OnPhoneStateListener listener) {
        if (listeners != null && listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    class PhoneStateReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (listeners == null || listeners.size() <= 0) return;

            String action = intent.getAction();
            if (TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(action)) {
                int state = telephonyManager.getCallState();
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        for (OnPhoneStateListener listener: listeners) {
                            listener.onRinging();
                        }
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        for (OnPhoneStateListener listener: listeners) {
                            listener.onOffHook();
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        for (OnPhoneStateListener listener: listeners) {
                            listener.onIdle();
                        }
                        break;
                }
            }
        }
    }

    public void release () {
        if (listeners != null) listeners.clear();
        mApp.get().unregisterReceiver(phoneStateReciever);
    }

    public interface OnPhoneStateListener {
        /** 响铃 */
        void onRinging ();
        /** 接听 */
        void onOffHook ();
        /** 挂断 */
        void onIdle ();
    }
}
