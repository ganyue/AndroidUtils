package com.gy.appbase.controller;

/**
 * Created by sam_gan on 2016/6/20.
 */
public interface IDataLoadCallback {
    void onLoadSuccess (String key, Object data, Object extra);
    void onLoadFail (String key, Object extra);
    void onLoadError (String key, Object extra);
}
