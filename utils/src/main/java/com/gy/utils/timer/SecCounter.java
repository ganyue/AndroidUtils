package com.gy.utils.timer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ganyu on 2016/11/8.
 *
 */
public class SecCounter extends Thread {

    private static SecCounter mInstance;
    public static SecCounter getInstance () {
        if (mInstance == null) {
            mInstance = new SecCounter();
        }
        return mInstance;
    }

    private Map<String, List<OnTimeListener>> listeners;
    private Map<String, Long> couters;
    private SecCounter () {
        listeners = new HashMap<>();
        couters = new HashMap<>();
        start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (listeners == null || listeners.size() <= 0) {
                    synchronized (this) {
                        wait();
                    }
                }
                sleep(1000);
                Set<String> keys = listeners.keySet();
                for (String key: keys) {
                    long time = couters.get(key) + 1;
                    couters.put(key, time);
                    List<OnTimeListener> onTimeListeners = listeners.get(key);
                    for (OnTimeListener listener: onTimeListeners) {
                        listener.onTime(time);
                    }
                }
            } catch (Exception e){}
        }
    }

    public void removeAllListeners () {
        listeners.clear();
        couters.clear();
    }

    public void addOnTimeListener (String key, OnTimeListener listener) {
        List<OnTimeListener> onTimeListeners;
        if (listeners.containsKey(key)) {
            onTimeListeners = listeners.get(key);
            if (onTimeListeners.contains(listener)) return;
            onTimeListeners.add(listener);
        } else {
            onTimeListeners = new ArrayList<>();
            onTimeListeners.add(listener);
            couters.put(key, 0L);
        }
        listeners.put(key, onTimeListeners);
        synchronized (this) {
            notify();
        }
    }

    public void removeOnTimeListener (String key, OnTimeListener listener) {
        List<OnTimeListener> onTimeListeners= listeners.get(key);
        if (onTimeListeners != null) {
            if (onTimeListeners.contains(listener)) onTimeListeners.remove(listener);
            if (onTimeListeners.size() <= 0) {
                removeOnTimeListener(key);
            } else {
                listeners.put(key, onTimeListeners);
            }
        }
    }

    public void removeOnTimeListener (String key) {
        listeners.remove(key);
        couters.remove(key);
    }

    public interface OnTimeListener {
        void onTime (long second);
    }
}
