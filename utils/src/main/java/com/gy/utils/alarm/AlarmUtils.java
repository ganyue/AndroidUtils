package com.gy.utils.alarm;

import android.app.AlarmManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yue.gan on 2016/10/10.
 *
 */
public class AlarmUtils {

    private final String[] AlarmAlertActions = {
            "com.android.deskclock.ALARM_ALERT",//原生闹钟广播
            "com.android.alarmclock.ALARM_ALERT",//魅族
            "com.lge.clock.alarmclock.ALARM_ALERT",//LG
             "com.samsung.sec.android.clockpackage.alarm.ALARM_ALERT",//三星
             "com.sonyericsson.alarm.ALARM_ALERT",//索尼
             "com.htc.android.worldclock.ALARM_ALERT",//htc
             "com.htc.worldclock.ALARM_ALERT",//htc
             "com.lenovomobile.deskclock.ALARM_ALERT",//联想
             "com.cn.google.AlertClock.ALARM_ALERT",//vivo
             "com.htc.android.worldclock.intent.action.ALARM_ALERT",
             "com.lenovo.deskclock.ALARM_ALERT",//联想
             "com.oppo.alarmclock.alarmclock.ALARM_ALERT",//oppo
             "com.zdworks.android.zdclock.ACTION_ALARM_ALERT"//中兴
    };

    private final String[] AlarmDoneActions = {
            "com.Android.deskclock.ALARM_DONE",
            "com.android.alarmclock.ALARM_DONE",
            "com.lge.clock.alarmclock.ALARM_DONE",
            "com.samsung.sec.android.clockpackage.alarm.ALARM_DONE",
            "com.sonyericsson.alarm.ALARM_DONE",
            "com.htc.android.worldclock.ALARM_DONE",
            "com.htc.worldclock.ALARM_DONE",
            "com.lenovomobile.deskclock.ALARM_DONE",
            "com.cn.google.AlertClock.ALARM_DONE",
            "com.htc.android.worldclock.intent.action.ALARM_DONE",
            "com.lenovo.deskclock.ALARM_DONE",
            "com.oppo.alarmclock.alarmclock.ALARM_DONE",
            "com.android.alarmclock.alarm_killed",
            "alarm_killed"
    };

    private static AlarmUtils mInstance;
    private WeakReference<Application> mApp;
    private AlarmManager alarmManager;
    private List<OnAlarmStateListener> listeners;
    private AlarmStateReciever alarmStateReciever;

    public static AlarmUtils getInstance (Application application) {
        if (mInstance == null) {
            mInstance = new AlarmUtils(application);
        }

        return mInstance;
    }

    private AlarmUtils(Application application) {
        mApp = new WeakReference<>(application);
        alarmManager = (AlarmManager) application.getSystemService(Context.ALARM_SERVICE);

        alarmStateReciever = new AlarmStateReciever();
        IntentFilter intentFilter = new IntentFilter();
        for (String alertAction: AlarmAlertActions) {
            intentFilter.addAction(alertAction);
        }
        for (String doneAction: AlarmDoneActions) {
            intentFilter.addAction(doneAction);
        }
        application.registerReceiver(alarmStateReciever, intentFilter);
    }

    public void addOnAlarmStateListener (OnAlarmStateListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }

        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeOnAlarmStateListener (OnAlarmStateListener listener) {
        if (listeners != null && listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    class AlarmStateReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (listeners == null || listeners.size() <= 0) return;
            String action = intent.getAction();
            for (String alertAction : AlarmAlertActions) {
                if (alertAction.equals(action)) {
                    if (listeners != null) {
                        for (OnAlarmStateListener listener : listeners) {
                            listener.onAlert();
                        }
                    }
                    return;
                }
            }

            for (String doneAction : AlarmDoneActions) {
                if (doneAction.equals(action)) {
                    if (listeners != null) {
                        for (OnAlarmStateListener listener : listeners) {
                            listener.onDone();
                        }
                    }
                    return;
                }
            }
        }
    }

    public void release () {
        if (listeners != null) listeners.clear();
        mApp.get().unregisterReceiver(alarmStateReciever);
    }

    public interface OnAlarmStateListener {
        /** 闹铃 */
        void onAlert();
        /** 停止 */
        void onDone();
    }
}
