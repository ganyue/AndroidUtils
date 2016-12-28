package com.gy.utils.img;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by ganyu on 2016/12/14.
 *
 */
public interface IImageLoader {
    void displayImage (String url, ImageView imageView);
    void displayImage (String url, ImageView imageView, int imgHolderResId);
    void displayImage (String url, ImageView imageView, int width, int height);
    void displayImageWithNoneDefaultImg(String url, ImageView imageView);
    void displayRoundImage (String url, ImageView imageView);
    void loadImage (Context context, final String url, final OnImageloadListener listener);
    void clearMemCache ();
}
