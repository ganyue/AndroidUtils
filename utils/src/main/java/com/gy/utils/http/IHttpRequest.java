package com.gy.utils.http;

import java.util.Map;

/**
 * Created by yue.gan on 2016/7/19.
 *
 */
public interface IHttpRequest {
    void getJson (String url, OnRequestListener listener);
    void getString (String url, OnRequestListener listener);
    void getObject (String url, Class clazz, OnRequestListener listener);
    void postObject (String url, Map<String, String> params, Class clazz, OnRequestListener listener);
    Object jsonStr2Object (Class clazz, String jsonStr);
    String object2JsonStr(Object object);
    void loadImageBitmap (String url, OnRequestListener listener, int maxWidth, int maxHeight);
}
