package com.gy.appbase.controller;

import android.support.v4.app.FragmentActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sam_gan on 2016/5/13.
 *
 */
public abstract class BaseDataController extends BaseFragmentActivityController{

    public BaseDataController(FragmentActivity activity) {
        super(activity);
    }

    //数据获取和数据获取结果的处理方法
    protected Map<String, Object> mDatas;
    protected Map<String, IDataLoadCallback> mDataLoadCallbacks;
    protected abstract void loadPageData (String key, Object extra);   //load data

    protected void onLoadSuccess (String key, Object data){//load success
        mDatas.put(key, data);
        IDataLoadCallback dataLoadCallback = getDataLoadCallback(key);
        if (dataLoadCallback != null) {
            dataLoadCallback.onLoadSuccess(key, data);
        }
    }

    protected void onLoadFail (String key) {//data is integrity
        IDataLoadCallback dataLoadCallback = getDataLoadCallback(key);
        if (dataLoadCallback != null) {
            dataLoadCallback.onLoadFail(key);
        }
    }

    protected void onLoadError (String key) {//network not available or server is busy
        IDataLoadCallback dataLoadCallback = getDataLoadCallback(key);
        if (dataLoadCallback != null) {
            dataLoadCallback.onLoadError(key);
        }
    }

    /**
     * <p>拉取页面数据
     * <p>方法供页面使用无需重写,具体拉取数据的方法在页面对应的子类的loadPageData方法中实现
     * @param key 键值，用于区分callback，
     * @param callback 回调，数据拉取后回调对应key的接口中的方法,callback被在ui线程中回调，可以修改ui
     */
    public final void loadData (String key, Object extra, IDataLoadCallback callback, boolean forceUpdate) {
        if (mDatas == null) {
            mDatas = new HashMap<String, Object>();
        }

        if (mDataLoadCallbacks == null) {
            mDataLoadCallbacks = new HashMap<>();
        }

        if (callback != null) {
            mDataLoadCallbacks.put(key, callback);
        }

        Object data = mDatas.get(key);

        if (forceUpdate || data == null) {
            loadPageData(key, extra);
        } else {
            onLoadSuccess(key, data);
        }
    }

    protected IDataLoadCallback getDataLoadCallback (String key) {
        return mDataLoadCallbacks == null? null: mDataLoadCallbacks.get(key);
    }

    public Object getData (String key) {
        return mDatas == null? null: mDatas.get(key);
    }

}
