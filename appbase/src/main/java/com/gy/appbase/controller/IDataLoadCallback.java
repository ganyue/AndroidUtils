package com.gy.appbase.controller;

/**
 * Created by sam_gan on 2016/6/20.
 */
public interface IDataLoadCallback {
    void onLoadSuccess (String key, Object data);
    void onLoadFail (String key);
    void onLoadError (String key);
}
