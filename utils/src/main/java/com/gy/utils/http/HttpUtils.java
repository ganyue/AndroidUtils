package com.gy.utils.http;

import android.app.Application;

/**
 * Created by ganyu on 2016/7/19.
 *
 */
public class HttpUtils implements IHttpRequest{

    private static HttpUtils mInstance;
    private VolleyHttpUtils volleyHttpUtils;

    public static HttpUtils getInstance (Application application) {
        if (mInstance == null) {
            mInstance = new HttpUtils(application);
        }

        return mInstance;
    }

    private HttpUtils(){}

    private HttpUtils(Application application) {
        volleyHttpUtils = new VolleyHttpUtils(application);
    }

    @Override
    public void getJson(String url, OnRequestListener listener) {
        volleyHttpUtils.getJson(url, listener);
    }

    @Override
    public void getString(String url, OnRequestListener listener) {
        volleyHttpUtils.getString(url, listener);
    }

    @Override
    public void getObject(String url, Class clazz, OnRequestListener listener) {
        volleyHttpUtils.getObject(url, clazz, listener);
    }
}
