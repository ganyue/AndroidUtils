package com.gy.utils.http;

/**
 * Created by ganyu on 2016/7/19.
 *
 */
public interface OnRequestListener {
    void onResponse (String url, Object responseData);
    void onError (String msg);
}
