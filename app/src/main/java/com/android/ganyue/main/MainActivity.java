package com.android.ganyue.main;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.ganyue.R;
import com.android.ganyue.application.MApplication;
import com.android.ganyue.frg.ARecordTestFrg;
import com.android.ganyue.frg.BannerFrag;
import com.android.ganyue.frg.DaoTestFrg;
import com.android.ganyue.frg.HorizontalIndicatorFrg;
import com.android.ganyue.frg.MediaTestFrg;
import com.android.ganyue.frg.ScrollObservListFrag;
import com.android.ganyue.frg.TestFrg;
import com.android.ganyue.frg.Udp17000Frg;
import com.android.ganyue.frg.Udp18000Frg;
import com.android.ganyue.frg.UdpTestFrg;
import com.android.ganyue.frg.WifiConnectTestFrg;
import com.gy.utils.constants.WindowConstants;
import com.gy.utils.log.LogUtils;
import com.gy.utils.wifi.WifiUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yue.gan on 2016/4/30.
 *
 */
public class MainActivity extends Activity{

    private final Class[] types = {
            TestFrg.class,
            ARecordTestFrg.class,
            HorizontalIndicatorFrg.class,
            WifiConnectTestFrg.class,
            UdpTestFrg.class,
            Udp17000Frg.class,
            Udp18000Frg.class,
            DaoTestFrg.class,
            MediaTestFrg.class,
            ScrollObservListFrag.class,
            BannerFrag.class,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ListView mLvContent = new ListView(this);
        List<Map<String, ?>> datas = new ArrayList<>();

        for (Class clazz: types) {
            addData("text", clazz.getSimpleName(), datas);
        }

        final SimpleAdapter adapter = new SimpleAdapter(this, datas, R.layout.item_single_text,
                new String[]{"text"}, new int[]{R.id.tv_item_name});
        mLvContent.setAdapter(adapter);

        setContentView(mLvContent);

        mLvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, ?> data = (Map<String, ?>) parent.getAdapter().getItem(position);
                FuncActivity.startSelf(MainActivity.this, (String) data.get("text"));
            }
        });

        MApplication.getWifiUtils().addOnNetworkChangedListener(onNetworkChangedListener);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
    }

    private void addData (String dataKey, String dataVal, List<Map<String, ?>> container) {
        Map<String, String> data = new HashMap<>();
        data.put(dataKey, dataVal);
        container.add(data);
    }

    private WifiUtils.OnNetworkChangedListener onNetworkChangedListener = new WifiUtils.OnNetworkChangedListener() {
        @Override
        public void onNetworkChanged(boolean isConnected, int type) {
            LogUtils.d("yue.gan", "net connected : " + isConnected + " type : " + type);
        }
    };

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        WindowConstants.getInstance(this);
    }
}
