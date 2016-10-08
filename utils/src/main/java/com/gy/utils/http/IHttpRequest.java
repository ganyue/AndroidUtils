package com.gy.utils.http;

/**
 * Created by ganyu on 2016/7/19.
 *
 */
public interface IHttpRequest {
    void getJson (String url, OnRequestListener listener);
    void getString (String url, OnRequestListener listener);
    void getObject (String url, Class clazz, OnRequestListener listener);
}
