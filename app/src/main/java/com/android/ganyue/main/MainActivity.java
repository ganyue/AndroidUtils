package com.android.ganyue.main;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.ganyue.frg.DaoTestFrg;
import com.android.ganyue.frg.FileUtilTestFrg;
import com.android.ganyue.frg.TcpTestFrg;
import com.android.ganyue.frg.UdpTestFrg;
import com.android.ganyue.frg.WifiConnectTestFrg;
import com.gy.utils.constants.WindowConstants;
import com.android.ganyue.R;
import com.android.ganyue.frg.BevaTtAlbumDetailFrg;
import com.android.ganyue.frg.HorizontalIndicatorFrg;
import com.android.ganyue.frg.NsdTestFrg;
import com.gy.utils.file.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ganyu on 2016/4/30.
 *
 */
public class MainActivity extends Activity{

    private final Class[] types = {
            NsdTestFrg.class,
            HorizontalIndicatorFrg.class,
            BevaTtAlbumDetailFrg.class,
            WifiConnectTestFrg.class,
            TcpTestFrg.class,
            UdpTestFrg.class,
            DaoTestFrg.class,
            FileUtilTestFrg.class,
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
    }

    private void addData (String dataKey, String dataVal, List<Map<String, ?>> container) {
        Map<String, String> data = new HashMap<>();
        data.put(dataKey, dataVal);
        container.add(data);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        WindowConstants.getInstance(this);
    }
}
