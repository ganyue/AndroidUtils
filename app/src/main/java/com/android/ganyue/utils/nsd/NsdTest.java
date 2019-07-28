package com.android.ganyue.utils.nsd;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

/**
 * Created by yue.gan on 2016/4/30.
 */
public class NsdTest {
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public NsdTest (final Context context, View regBtn, View discBtn, final TextView mTvLog, final String type, final String name, final int port) {
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                mTvLog.setText("" + mTvLog.getText() + "\n" + msg.obj);
            }
        };

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NsdManager nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
                NsdServiceInfo serviceInfo = new NsdServiceInfo();
                serviceInfo.setServiceName(name);
                serviceInfo.setServiceType(type);
                serviceInfo.setPort(port);
                nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, new NsdManager.RegistrationListener() {
                    @Override
                    public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                        Message msg = Message.obtain();
                        msg.obj = "reg failed " + serviceInfo.getServiceName();
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                        Message msg = Message.obtain();
                        msg.obj = "unreg failed " + serviceInfo.getServiceName();
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                        Message msg = Message.obtain();
                        msg.obj = "reg succeed " + serviceInfo.getServiceName();
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
                        Message msg = Message.obtain();
                        msg.obj = "unreg succeed " + serviceInfo.getServiceName();
                        handler.sendMessage(msg);
                    }
                });
            }
        });

        discBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NsdManager nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
                nsdManager.discoverServices("_NsdTest._tcp", NsdManager.PROTOCOL_DNS_SD, new NsdManager.DiscoveryListener() {
                    @Override
                    public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                        Message msg = Message.obtain();
                        msg.obj = "start failed ";
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                        Message msg = Message.obtain();
                        msg.obj = "stop failed ";
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onDiscoveryStarted(String serviceType) {
                        Message msg = Message.obtain();
                        msg.obj = "start succeed ";
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onDiscoveryStopped(String serviceType) {
                        Message msg = Message.obtain();
                        msg.obj = "stop succeed ";
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onServiceFound(NsdServiceInfo serviceInfo) {
                        Message msg = Message.obtain();
                        msg.obj = "found service"  + serviceInfo.getServiceName();
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onServiceLost(NsdServiceInfo serviceInfo) {
                        Message msg = Message.obtain();
                        msg.obj = "lost service " + serviceInfo.getServiceName();
                        handler.sendMessage(msg);
                    }
                });
            }
        });
    }
}
