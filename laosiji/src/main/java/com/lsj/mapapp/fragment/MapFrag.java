package com.lsj.mapapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.gy.appbase.controller.BaseFragmentActivityController;
import com.gy.appbase.fragment.BaseFragment;
import com.lsj.mapapp.R;
import com.lsj.mapapp.controller.MapActivityCtrl;

/**
 * Created by ganyu on 2016/7/22.
 *
 */
public class MapFrag extends BaseFragment {

    private MapView mapView;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        mapView = (MapView) view.findViewById(R.id.mv_map);
        mapView.onCreate(mActivity, savedInstanceState);
        BaiduMap baiduMap = mapView.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL); //普通地图
        baiduMap.setMyLocationEnabled(true); //开启定位
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected BaseFragmentActivityController instanceController() {
        return new MapActivityCtrl(mActivity);
    }
}
