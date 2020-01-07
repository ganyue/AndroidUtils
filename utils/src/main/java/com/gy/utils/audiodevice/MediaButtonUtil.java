package com.gy.utils.audiodevice;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.view.KeyEvent;

import com.gy.utils.ref.ComparableWeakRef;

import java.util.ArrayList;
import java.util.List;

public class MediaButtonUtil {

    private static MediaButtonUtil mInstance;
    private ComparableWeakRef<Context> mCxt;
    private List<ComparableWeakRef<MediaButtonListener>> mMediaDeviceListeners;
    private BtStateReceiver mBtStateReceiver;

    public static MediaButtonUtil getInstance (Context context) {
        if (mInstance == null) {
            mInstance = new MediaButtonUtil(context.getApplicationContext());
        }
        return mInstance;
    }

    private MediaButtonUtil (Context context) {
        mCxt = new ComparableWeakRef<>(context);
    }

    public void start () {
        AudioManager am = (AudioManager) mCxt.get().getSystemService(Context.AUDIO_SERVICE);
        am.registerMediaButtonEventReceiver(new ComponentName(mCxt.get(), MediaButtonReceiver.class));
    }

    public void stop () {
        AudioManager am = (AudioManager) mCxt.get().getSystemService(Context.AUDIO_SERVICE);
        am.unregisterMediaButtonEventReceiver(new ComponentName(mCxt.get(), MediaButtonReceiver.class));
    }

    public void addMediaButtonListener (MediaButtonListener listener) {
        if (mMediaDeviceListeners == null) {
            mMediaDeviceListeners = new ArrayList<>();
        }
        ComparableWeakRef<MediaButtonListener> ref = new ComparableWeakRef<>(listener);
        if (!mMediaDeviceListeners.contains(ref)) {
            mMediaDeviceListeners.add(ref);
        }
    }

    public void removeMediaButtonListener (MediaButtonListener listener) {
        if (mMediaDeviceListeners != null) {
            mMediaDeviceListeners.remove(new ComparableWeakRef<>(listener));
        }
    }

    public interface MediaButtonListener {
        void onMediaButtonDown (int keyCode, KeyEvent event);
        void onMediaButtonUp (int keyCode, KeyEvent event);
    }

    private class BtStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
}
