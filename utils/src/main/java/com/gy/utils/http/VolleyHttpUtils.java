package com.gy.utils.http;

import android.app.Application;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by sam_gan on 2016/7/6.
 *
 * <p>must be inited in application
 */
public class VolleyHttpUtils implements IHttpRequest{

    private RequestQueue requestQueue;
    private Gson gson;

    public VolleyHttpUtils (Application application) {
        requestQueue = Volley.newRequestQueue(application);
        gson = new Gson();
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

    public void getObject (final String url, final Class clazz, final OnRequestListener listener) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            String data = jsonObject.getString("data");
                            Object object = gson.fromJson(data, clazz);
                            listener.onResponse(url, object);
                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.onError(e.toString());
                        }
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

    public void postObject (final String url, Map<String, String> params,
                            final Class clazz, final OnRequestListener listener) {

        JsonObjectPostRequest jsonObjectRequest = new JsonObjectPostRequest(url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            String data = jsonObject.getString("data");
                            Object object = gson.fromJson(data, clazz);
                            listener.onResponse(url, object);
                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.onError(e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.onError(volleyError.getMessage());
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public Object jsonStr2Object (Class clazz, String jsonStr) {
        return gson.fromJson(jsonStr, clazz);
    }

    @Override
    public String object2JsonStr(Object object) {
        return gson.toJson(object);
    }

    @Override
    public void loadImageBitmap(final String url, final OnRequestListener listener, int maxWidth, int maxHeight) {
        ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                listener.onResponse(url, bitmap);
            }
        }, maxWidth, maxHeight, ImageView.ScaleType.FIT_XY, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.onError(volleyError.toString());
            }
        });
        requestQueue.add(request);
    }

    private class JsonObjectPostRequest extends Request<JSONObject> {
        private Map<String, String> params;
        private Response.Listener<JSONObject> listener;
        public JsonObjectPostRequest(String url, Map<String, String> params, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(Method.POST, url, errorListener);
            this.listener = listener;
            this.params = params;
        }

        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            return params;
        }

        @Override
        protected Response<JSONObject> parseNetworkResponse(NetworkResponse networkResponse) {
            try {
                String je = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers, "utf-8"));
                return Response.success(new JSONObject(je), HttpHeaderParser.parseCacheHeaders(networkResponse));
            } catch (UnsupportedEncodingException var3) {
                return Response.error(new ParseError(var3));
            } catch (JSONException var4) {
                return Response.error(new ParseError(var4));
            }
        }

        @Override
        protected void deliverResponse(JSONObject jsonObject) {
            if (listener != null) listener.onResponse(jsonObject);
        }
    }
}
