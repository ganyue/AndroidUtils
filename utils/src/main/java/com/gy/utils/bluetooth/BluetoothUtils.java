package com.gy.utils.bluetooth;

import android.app.Application;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganyu on 2016/7/25.
 *
 */
public class BluetoothUtils {
    private static BluetoothUtils mInstance;
    private WeakReference<Application> mApp;
    private BluetoothReceiver bluetoothReceiver;
    private BluetoothAdapter bluetoothAdapter;
    private List<OnBluetoothListener> listeners;

    public static BluetoothUtils getInstance (Application application) {
        if (mInstance == null) {
            mInstance = new BluetoothUtils(application);
        }

        return mInstance;
    }

    private BluetoothUtils(Application application) {
        mApp = new WeakReference<>(application);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothReceiver = new BluetoothReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);//发现设备
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mApp.get().registerReceiver(bluetoothReceiver, intentFilter);
    }

    public boolean isBluetoothEnabled () {
        return bluetoothAdapter.isEnabled();
    }

    /**
     * 开启或关闭蓝牙
     */
    public void enableBluetooth (boolean enable) {
        if (enable && !isBluetoothEnabled()) {
            bluetoothAdapter.cancelDiscovery();
            if (!bluetoothAdapter.enable()) {
                for (OnBluetoothListener listener: listeners) {
                    listener.onOpenError();
                }
            }
        } else {
            bluetoothAdapter.disable();
        }
    }

    private BluetoothHeadset bh;
    private BluetoothA2dp a2dp;

    /**
     * 获取蓝牙音箱/耳机连接服务
     */
    public void connectAudioRemoteService() {
        if (isServiceConnected()) {
            for (OnBluetoothListener listener: listeners) {
                listener.onBluetoothProxyServiceConnected();
            }
        } else {
            bluetoothAdapter.getProfileProxy(mApp.get(), bs, BluetoothProfile.A2DP);
            bluetoothAdapter.getProfileProxy(mApp.get(), bs, BluetoothProfile.HEADSET);
        }
    }

    private BluetoothProfile.ServiceListener bs = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            try {
                if (profile == BluetoothProfile.HEADSET) {
                    bh = (BluetoothHeadset) proxy;
                } else if (profile == BluetoothProfile.A2DP) {
                    a2dp = (BluetoothA2dp) proxy;
                }

                for (OnBluetoothListener listener: listeners) {
                    listener.onBluetoothProxyServiceConnected();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(int profile) {
            for (OnBluetoothListener listener: listeners) {
                listener.onBluetoothProxyServiceDisConnected();
            }
        }
    };

    /** 返回蓝牙媒体设备连接服务是否开启 */
    public boolean isServiceConnected () {
        return !(bh == null && a2dp == null);
    }

    public void search(){
        // 如果正在搜索，就先取消搜索
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        // 开始搜索蓝牙设备,搜索到的蓝牙设备通过广播返回
        bluetoothAdapter.startDiscovery();
    }

    public void bondDevice (BluetoothDevice device) {
        int bondState = device.getBondState();
        if (bondState == BluetoothDevice.BOND_NONE) {
            try {
                Method bondMethod = BluetoothDevice.class.getMethod("createBond");
                bondMethod.invoke(device);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /** 连接蓝牙媒体设备 */
    public void connectAudioDevice (BluetoothDevice device) {

        try {
            if (a2dp != null && a2dp.getConnectionState(device) != BluetoothProfile.STATE_CONNECTED){
                a2dp.getClass()
                        .getMethod("connect", BluetoothDevice.class)
                        .invoke(a2dp, device);
            } else if (bh != null && bh.getConnectionState(device) != BluetoothProfile.STATE_CONNECTED){
                bh.getClass()
                        .getMethod("connect", BluetoothDevice.class)
                        .invoke(bh, device);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 获取包含相应名称的a2dp设备的连接状态 */
    public int isA2dpConnectedOrConnectingDevice (String deviceName) {
        if (a2dp == null) {
            connectAudioRemoteService();
            return BluetoothA2dp.STATE_DISCONNECTED;
        }

        List<BluetoothDevice> devices = a2dp.getDevicesMatchingConnectionStates(
                new int[]{BluetoothA2dp.STATE_CONNECTED, BluetoothA2dp.STATE_CONNECTING});
        if (devices.size() <= 0) {
            return BluetoothA2dp.STATE_DISCONNECTED;
        }

        for (BluetoothDevice device : devices) {
            if ((""+device.getName()).toLowerCase().contains(deviceName)) {
                return a2dp.getConnectionState(device);
            }
        }

        return BluetoothA2dp.STATE_DISCONNECTED;
    }

    /** 获取已经连接的包含相应名称的a2dp设备 */
    public BluetoothDevice getConnectedDevice (String name) {
        if (a2dp == null) {
            connectAudioRemoteService();
            return null;
        }

        List<BluetoothDevice> devices = a2dp.getDevicesMatchingConnectionStates(
                new int[]{BluetoothA2dp.STATE_CONNECTED, BluetoothA2dp.STATE_CONNECTING});
        if (devices.size() <= 0) {
            return null;
        }

        for (BluetoothDevice device : devices) {
            if ((""+device.getName()).toLowerCase().contains(name)) {
                return device;
            }
        }

        return null;
    }

    public void addOnBluetoothListener (OnBluetoothListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }

        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeOnBluetoothListener (OnBluetoothListener listener) {
        if (listeners != null && listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    public void release () {
        mApp.get().unregisterReceiver(bluetoothReceiver);
    }

    class BluetoothReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (listeners == null || listeners.size() <= 0) return;

            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                for (OnBluetoothListener listener: listeners) {
                    listener.onFoundDevice(device);
                }
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                for (OnBluetoothListener listener: listeners) {
                    listener.onDiscoveryFinished();
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int currentBondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
                for (OnBluetoothListener listener: listeners) {
                    listener.onBondStateChanged(bluetoothDevice, currentBondState);
                }
            } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                for (OnBluetoothListener listener: listeners) {
                    listener.onDeviceConnected(bluetoothDevice);
                }
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                for (OnBluetoothListener listener: listeners) {
                    listener.onDeviceDisConnected(bluetoothDevice);
                }
            }

        }
    }

    public interface OnBluetoothListener {
        void onOpenError ();
        void onBluetoothProxyServiceConnected ();
        void onBluetoothProxyServiceDisConnected ();
        void onFoundDevice (BluetoothDevice device);
        void onDiscoveryFinished ();
        void onBondStateChanged (BluetoothDevice device, int state);
        void onDeviceConnected (BluetoothDevice device);
        void onDeviceDisConnected (BluetoothDevice device);
    }
}
