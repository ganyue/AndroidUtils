package com.gy.utils.img;

import android.graphics.Bitmap;

/**
 * Created by ganyu on 2016/11/24.
 *
 */
public interface OnImageloadListener {
    void onImageLoadSuccess (String url, Bitmap bitmap);
    void onImageLoadError (String url, Object extra);
}
