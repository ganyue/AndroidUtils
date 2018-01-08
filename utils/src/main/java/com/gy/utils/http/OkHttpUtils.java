package com.gy.utils.http;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ganyu on 2017/8/9.
 */

public class OkHttpUtils implements IHttpRequest {

    private OkHttpClient okHttpClient;
    private Gson gson;

    public OkHttpUtils () {
        okHttpClient = new OkHttpClient();
        gson = new Gson();
    }

    @Override
    public void getJson(String url, OnRequestListener listener) {

    }

    @Override
    public void getString(final String url, final OnRequestListener listener) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onError(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(url, response.body().string());
            }
        });
    }

    @Override
    public void getObject(String url, Class clazz, OnRequestListener listener) {

    }

    @Override
    public void postObject(String url, Map<String, String> params, Class clazz, OnRequestListener listener) {

    }

    @Override
    public Object jsonStr2Object(Class clazz, String jsonStr) {
        return null;
    }

    @Override
    public String object2JsonStr(Object object) {
        return null;
    }

    @Override
    public void loadImageBitmap(String url, OnRequestListener listener, int maxWidth, int maxHeight) {

    }
}
