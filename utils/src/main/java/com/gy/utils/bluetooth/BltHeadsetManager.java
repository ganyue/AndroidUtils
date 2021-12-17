package com.gy.utils.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import com.gy.utils.log.LogUtils;

import java.util.List;

public class BltHeadsetManager {
    private Context mCxt;
    private BluetoothHeadset mBltHeadset;
    private boolean mIsReady = false;

    public BltHeadsetManager(Context cxt) {
        mCxt = cxt.getApplicationContext();
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        adapter.getProfileProxy(cxt, mProfileServiceListener, BluetoothProfile.HEADSET);

        IntentFilter filter = new IntentFilter(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED);
        mCxt.registerReceiver(mScoReceiver, filter);
    }

    public void release () {
        mCallback = null;
        mBltHeadset = null;
        mIsReady = false;
        mCxt.unregisterReceiver(mScoReceiver);
    }

    private BluetoothProfile.ServiceListener mProfileServiceListener =
            new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothProfile.HEADSET) {
                mBltHeadset = (BluetoothHeadset) proxy;
                mIsReady = true;
                if (mCallback != null) mCallback.onServiceConnected();
            }
        }

        @Override
        public void onServiceDisconnected(int profile) {
            mIsReady = false;
            mBltHeadset = null;
            if (mCallback != null) mCallback.onServiceDisconnected();
        }
    };

    public void startSco () {
        if (mIsReady) {
            List<BluetoothDevice> devices = mBltHeadset.getConnectedDevices();
            for (BluetoothDevice device: devices) {
                mBltHeadset.startVoiceRecognition(device);
            }
        }
    }

    public void stopSco () {
        if (mIsReady) {
            List<BluetoothDevice> devices = mBltHeadset.getConnectedDevices();
            for (BluetoothDevice device: devices) {
                mBltHeadset.stopVoiceRecognition(device);
            }
        }
    }

    private BroadcastReceiver mScoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED.equals(intent.getAction())
                    && mIsReady && mCallback != null) {
                int state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE,
                        BluetoothHeadset.STATE_AUDIO_DISCONNECTED);
                if (state == BluetoothHeadset.STATE_AUDIO_CONNECTED ) mCallback.onScoOn();
                else if (state == BluetoothHeadset.STATE_AUDIO_DISCONNECTED) mCallback.onScoOff();
            }
        }
    };

    private OnHeadSetManagerCallback mCallback = null;
    public void setOnHeadSetManagerCallback (OnHeadSetManagerCallback callback) {
        mCallback = callback;
        if (mCallback != null && mIsReady) {
            mCallback.onServiceConnected();
            List<BluetoothDevice> devices = mBltHeadset.getConnectedDevices();
            for (BluetoothDevice device: devices) {
                if (mBltHeadset.isAudioConnected(device)) mCallback.onScoOn();
            }
        }
    }
    public interface OnHeadSetManagerCallback {
        void onServiceConnected ();
        void onServiceDisconnected ();
        void onScoOn ();
        void onScoOff();
    }
}
