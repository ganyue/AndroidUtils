package com.gy.utils.http;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by sam_gan on 2016/7/6.
 *
 * <p>must be inited in application
 */
public class VolleyHttpUtils implements IHttpRequest{

    private RequestQueue requestQueue;

    public VolleyHttpUtils (Application application) {
        requestQueue = Volley.newRequestQueue(application);
    }

    public void getJson (final String url, final OnRequestListener listener) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        listener.onResponse(url, jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        listener.onError(volleyError.getMessage());
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    public void getString (final String url, final OnRequestListener listener) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String string) {
                        listener.onResponse(url, string);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        listener.onError(volleyError.getMessage());
                    }
                });
        requestQueue.add(stringRequest);
    }
}
