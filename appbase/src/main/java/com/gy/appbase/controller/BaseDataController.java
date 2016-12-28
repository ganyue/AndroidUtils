package com.gy.appbase.controller;

import android.support.v4.app.FragmentActivity;

import com.gy.utils.log.LogUtils;

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

    protected void onLoadSuccess (String key, Object data, Object extra){//load success
        /** removed by yue.gan to save some memory */
//        mDatas.put(getKey(key, extra), data);
        IDataLoadCallback dataLoadCallback = getDataLoadCallback(getKey(key, extra));
        if (dataLoadCallback != null) {
            try{
                dataLoadCallback.onLoadSuccess(key, data, extra);
            } catch (Exception e) {
                // 页面不在了，fragment没有释放，然后这时候数据刚好拉到
            }
        }
        removeDataLoadCallback(getKey(key, extra));
    }

    protected void onLoadFail (String key, Object extra) {//data is integrity
        IDataLoadCallback dataLoadCallback = getDataLoadCallback(getKey(key, extra));
        if (dataLoadCallback != null) {
           try{
               dataLoadCallback.onLoadFail(key, extra);
           } catch (Exception e) {
               // 页面不在了，fragment没有释放，然后这时候数据刚好拉到
           }
        }
        removeDataLoadCallback(getKey(key, extra));
    }

    protected void onLoadError (String key, Object extra) {//network not available or server is busy
        IDataLoadCallback dataLoadCallback = getDataLoadCallback(getKey(key, extra));
        if (dataLoadCallback != null) {
            try {
                dataLoadCallback.onLoadError(key, extra);
            } catch (Exception e) {
                // 页面不在了，fragment没有释放，然后这时候数据刚好拉到
            }
        }
        removeDataLoadCallback(getKey(key, extra));
    }

    /**
     * <p>拉取页面数据
     * <p>方法供页面使用无需重写,具体拉取数据的方法在页面对应的子类的loadPageData方法中实现
     * @param key 键值，用于区分callback，
     * @param callback 回调，数据拉取后回调对应key的接口中的方法,callback被在ui线程中回调，可以修改ui
     */
    public final void loadData (String key, Object extra, IDataLoadCallback callback, boolean forceUpdate) {
        if (mDatas == null) {
            mDatas = new HashMap<>();
        }

        if (mDataLoadCallbacks == null) {
            mDataLoadCallbacks = new HashMap<>();
        }

        if (callback != null) {
            mDataLoadCallbacks.put(getKey(key, extra), callback);
        }

        Object data = mDatas.get(getKey(key, extra));
        LogUtils.d("yue.gan", "data size : " + mDatas.size() + "  -- callback size : " +  mDataLoadCallbacks.size());
        if (forceUpdate || data == null) {
            loadPageData(key, extra);
        } else {
            onLoadSuccess(key, data, extra);
        }
    }

    protected String getKey (String key, Object extra) {
        return extra == null? key: key+extra.toString();
    }

    protected IDataLoadCallback getDataLoadCallback (String key) {
        return mDataLoadCallbacks == null? null: mDataLoadCallbacks.get(key);
    }

    protected IDataLoadCallback removeDataLoadCallback (String key) {
        return mDataLoadCallbacks == null? null: mDataLoadCallbacks.remove(key);
    }

    public Object getData (String key) {
        return mDatas == null? null: mDatas.get(key);
    }

}
