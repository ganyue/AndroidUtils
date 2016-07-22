package com.lsj.mapapp.activity;

import android.os.Bundle;

import com.gy.appbase.activity.BaseFragmentActivity;
import com.gy.appbase.controller.BaseFragmentActivityController;
import com.lsj.mapapp.fragment.MapFrag;
import com.lsj.mapapp.R;
import com.lsj.mapapp.controller.MapActivityCtrl;

public class MapActivity extends BaseFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
    }

    @Override
    protected void setContent(Bundle savedInstanceState) {
        mController.replaceFragment(R.id.flyt_content, MapFrag.class, null, null);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
    }

    @Override
    protected BaseFragmentActivityController instanceController() {
        return new MapActivityCtrl(this);
    }
}
