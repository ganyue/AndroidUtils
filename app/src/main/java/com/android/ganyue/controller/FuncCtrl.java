package com.android.ganyue.controller;

import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.gy.appbase.controller.BaseDataController;


/**
 * Created by sam_gan on 2016/5/13.
 *
 */
public class FuncCtrl extends BaseDataController {
    public FuncCtrl(FragmentActivity activity) {
        super(activity);
    }

    @Override
    protected void loadPageData(String key, Object extra) {
        Log.d("yue.gan", "loadData : " + key);
    }
}
