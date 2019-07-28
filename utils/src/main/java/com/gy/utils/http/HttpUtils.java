package com.gy.utils.http;

import android.app.Application;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yue.gan on 2016/7/19.
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

    @Override
    public void postObject(String url, Map<String, String> params, Class clazz, OnRequestListener listener) {
        volleyHttpUtils.postObject(url, params, clazz, listener);
    }

    @Override
    public Object jsonStr2Object(Class clazz, String jsonStr) {
        return volleyHttpUtils.jsonStr2Object(clazz, jsonStr);
    }

    @Override
    public String object2JsonStr(Object object) {
        return volleyHttpUtils.object2JsonStr(object);
    }

    @Override
    public void loadImageBitmap(String url, OnRequestListener listener, int maxWidth, int maxHeight) {
        volleyHttpUtils.loadImageBitmap(url, listener, maxWidth, maxHeight);
    }

    //get 方式url参数获取
    public static Map<String, String> getUrlParams (String url) {
        int paramStartIndex = url.indexOf('?');
        if (paramStartIndex <= 0 || paramStartIndex + 1 >= url.length()) return null;
        url = url.substring(paramStartIndex + 1);
        HashMap<String, String> params = new HashMap<>();
        String[] paramsStr = url.split("&");
        for (String param: paramsStr) {
            String[] result = param.split("=");
            if (result.length == 2) {
                params.put(result[0], result[1]);
            }
        }
        return params;
    }

    public static String removeGetParams (String url) {
        int paramStartIndex = url.indexOf('?');
        if (paramStartIndex <= 0 || paramStartIndex + 1 >= url.length()) return url;
        return url.substring(0, paramStartIndex);
    }
}
