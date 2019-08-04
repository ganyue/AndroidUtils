package com.android.ganyue.frg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.ganyue.R;
import com.baidu.mapapi.map.MapView;
import com.gy.appbase.fragment.BaseFragment;
import com.gy.appbase.inject.ViewInject;

/**
 * Created by yue.gan on 2016/4/30.
 * Nsd test
 */
public class MapTestFrg extends BaseFragment {

    @ViewInject (R.id.mv_map)
    private MapView mapView;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map_test, null);
    }

    @Override
    protected void initViews(View view) {
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void activityCall(int type, Object extra) {

    }
}
