package com.lsj.mapapp.application;

import com.baidu.mapapi.SDKInitializer;
import com.gy.appbase.application.BaseApplication;

/**
 * Created by ganyu on 2016/7/22.
 */
public class MApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
    }
}
