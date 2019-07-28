package com.android.ganyue.frg;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ganyue.R;
import com.android.ganyue.application.MApplication;
import com.android.ganyue.controller.FuncCtrl;
import com.android.ganyue.utils.nsd.NsdTest;
import com.gy.appbase.controller.BaseFragmentActivityController;
import com.gy.appbase.fragment.BaseFragment;
import com.gy.utils.wifi.WiFi;
import com.gy.utils.wifi.WiFiConnecter;
import com.gy.utils.wifi.WifiUtils;

import java.util.List;

/**
 * Created by yue.gan on 2016/4/30.
 * Nsd test
 */
public class WifiConnectTestFrg extends BaseFragment {

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wifi_connect, null);
    }

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        view.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String networkSSID = "BEVA_1F5ACC";
                String networkPass = "admin888";
                WifiManager wifiManager = (WifiManager)mActivity.getSystemService(mActivity.WIFI_SERVICE);
                List<ScanResult> results = wifiManager.getScanResults();
                for (ScanResult scanResult: results) {
                    if (scanResult.SSID.contains("BEVA_")) {
                        WiFi.connectToNewNetwork(wifiManager, scanResult, networkPass);
                        Log.d("yue.gan", "connect to : " + scanResult.SSID);
                    }
                }
            }
        });

        view.findViewById(R.id.btn_remove_config).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiManager wifiManager = (WifiManager)mActivity.getSystemService(mActivity.WIFI_SERVICE);
                List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
                for (WifiConfiguration configuration : configurations) {
                    if (configuration.SSID.contains("BEVA_")) {
                        wifiManager.removeNetwork(configuration.networkId);
                    }
                }
                wifiManager.saveConfiguration();
            }
        });

        MApplication.getWifiUtils().addOnNetworkChangedListener(new WifiUtils.OnNetworkChangedListener() {
            @Override
            public void onNetworkChanged(boolean isConnected, int type) {
                Toast.makeText(mActivity, "wifi connected : " + isConnected + " type: " + type, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected BaseFragmentActivityController instanceController() {
        return new FuncCtrl(mActivity);
    }
}
