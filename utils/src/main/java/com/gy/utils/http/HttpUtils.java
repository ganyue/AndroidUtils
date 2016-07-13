package com.gy.utils.http;

/**
 * Created by sam_gan on 2016/7/6.
 *
 */
public class HttpUtils {

    private static HttpUtils mInstance;

    public static HttpUtils getInstance () {
        if (mInstance == null) {
            mInstance = new HttpUtils();
        }

        return mInstance;
    }

    private HttpUtils () {}


}
