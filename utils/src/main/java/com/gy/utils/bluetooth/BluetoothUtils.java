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
import android.text.TextUtils;

import com.gy.utils.log.LogUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yue.gan on 2016/7/25.
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
        intentFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        mApp.get().registerReceiver(bluetoothReceiver, intentFilter);
    }

    public boolean isBluetoothEnabled () {
        return bluetoothAdapter.isEnabled();
    }

    /**
     * 开启或关闭蓝牙
     */
    public boolean enableBluetooth (boolean enable) {
        if (enable && !isBluetoothEnabled()) {
            bluetoothAdapter.cancelDiscovery();
            if (!bluetoothAdapter.enable()) {
                for (OnBluetoothListener listener: listeners) {
                    listener.onOpenError();
                }
                return false;
            }
            return true;
        } else if (!enable && isBluetoothEnabled()) {
            return bluetoothAdapter.disable();
        }
        return true;
    }

    private BluetoothHeadset bh;
    private BluetoothA2dp a2dp;

    /**
     * 获取蓝牙音箱/耳机连接服务
     */
    public void connectA2dpRemoteService() {
        if (isA2dpServiceConnected()) {
            for (OnBluetoothListener listener: listeners) {
                listener.onBluetoothProxyServiceConnected();
            }
        } else {
            bluetoothAdapter.getProfileProxy(mApp.get(), bs, BluetoothProfile.A2DP);
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
    public boolean isA2dpServiceConnected() {
        return a2dp != null;
    }

    public void search(){
        // 如果正在搜索，就先取消搜索
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        // 开始搜索蓝牙设备,搜索到的蓝牙设备通过广播返回
        bluetoothAdapter.startDiscovery();
    }

    public void cancelSearch(){
        // 如果正在搜索，就先取消搜索
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
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
                LogUtils.d("yue.gan", "connectAudioDevice invoke");
                a2dp.getClass()
                        .getMethod("connect", BluetoothDevice.class)
                        .invoke(a2dp, device);
                LogUtils.d("yue.gan", "connectAudioDevice invoked");
            } else if (bh != null && bh.getConnectionState(device) != BluetoothProfile.STATE_CONNECTED){
                bh.getClass()
                        .getMethod("connect", BluetoothDevice.class)
                        .invoke(bh, device);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d("yue.gan", "connectAudioDevice fail : " + e.toString());
        }
    }

    /** 获取包含相应名称的a2dp设备的连接状态 */
    public int isA2dpConnectedOrConnectingDevice (String deviceName) {
        if (a2dp == null) {
            connectA2dpRemoteService();
            return BluetoothA2dp.STATE_DISCONNECTED;
        }

        List<BluetoothDevice> devices = a2dp.getDevicesMatchingConnectionStates(
                new int[]{BluetoothA2dp.STATE_CONNECTED, BluetoothA2dp.STATE_CONNECTING});
        if (devices == null || devices.size() <= 0) {
            return BluetoothA2dp.STATE_DISCONNECTED;
        }

        for (BluetoothDevice device : devices) {
            if ((""+device.getName()).toLowerCase().contains(deviceName)) {
                return a2dp.getConnectionState(device);
            }
        }

        return BluetoothA2dp.STATE_DISCONNECTED;
    }

    /** 获取包含相应名称的a2dp设备的连接状态 */
    public int isA2dpConnectedOrConnectingDeviceByAddr (String addr) {
        if (a2dp == null) {
            connectA2dpRemoteService();
            return BluetoothA2dp.STATE_DISCONNECTED;
        }

        List<BluetoothDevice> devices = a2dp.getDevicesMatchingConnectionStates(
                new int[]{BluetoothA2dp.STATE_CONNECTED, BluetoothA2dp.STATE_CONNECTING});
        if (devices == null || devices.size() <= 0) {
            return BluetoothA2dp.STATE_DISCONNECTED;
        }

        for (BluetoothDevice device : devices) {
            if ((""+device.getAddress()).equals(addr)) {
                return a2dp.getConnectionState(device);
            }
        }

        return BluetoothA2dp.STATE_DISCONNECTED;
    }

    /** 获取已经连接的包含相应名称的a2dp设备 */
    public BluetoothDevice getConnectedA2dpDevice(String name) {
        if (a2dp == null) {
            connectA2dpRemoteService();
            return null;
        }

        List<BluetoothDevice> devices = a2dp.getDevicesMatchingConnectionStates(
                new int[]{BluetoothA2dp.STATE_CONNECTED, BluetoothA2dp.STATE_CONNECTING});
        if (devices == null || devices.size() <= 0) {
            return null;
        }

        for (BluetoothDevice device : devices) {
            if ((""+device.getName()).toLowerCase().contains(name)) {
                return device;
            }
        }

        return null;
    }

    /** 获取已经连接的a2dp设备 */
    public BluetoothDevice getConnectedA2dpDeviceByAddr(String addr) {
        if (a2dp == null) {
            connectA2dpRemoteService();
            return null;
        }

        List<BluetoothDevice> devices = a2dp.getDevicesMatchingConnectionStates(
                new int[]{BluetoothA2dp.STATE_CONNECTED, BluetoothA2dp.STATE_CONNECTING});
        if (devices == null || devices.size() <= 0) {
            return null;
        }

        for (BluetoothDevice device : devices) {
            if ((""+device.getAddress()).contains(addr)) {
                return device;
            }
        }

        return null;
    }

    /** 获取已经连接的a2dp设备 */
    public BluetoothDevice getConnectedA2dpDevice () {
        if (a2dp == null) {
            connectA2dpRemoteService();
            return null;
        }

        List<BluetoothDevice> devices = a2dp.getDevicesMatchingConnectionStates(
                new int[]{BluetoothA2dp.STATE_CONNECTED, BluetoothA2dp.STATE_CONNECTING});
        if (devices == null || devices.size() <= 0) {
            return null;
        }

        return devices.get(0);
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
                String name = device.getName();
                if (TextUtils.isEmpty(name)) name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
                for (OnBluetoothListener listener: listeners) {
                    listener.onFoundDevice(device, name);
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
            } else if (BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothA2dp.STATE_DISCONNECTED);
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (state == BluetoothA2dp.STATE_CONNECTED) {
                    for (OnBluetoothListener listener: listeners) {
                        listener.onA2dpDeviceConnected(bluetoothDevice);
                    }
                }
            }

        }
    }

    public interface OnBluetoothListener {
        void onOpenError ();
        void onBluetoothProxyServiceConnected ();
        void onBluetoothProxyServiceDisConnected ();
        void onFoundDevice (BluetoothDevice device, String name);
        void onDiscoveryFinished ();
        void onBondStateChanged (BluetoothDevice device, int state);
        void onDeviceConnected (BluetoothDevice device);
        void onA2dpDeviceConnected (BluetoothDevice device);
        void onDeviceDisConnected (BluetoothDevice device);
    }
}
