package com.gy.utils.img;

import android.content.Context;
import android.widget.ImageView;

import com.gy.utils.img.glide.GlideLoader;

/**
 *<p> Created by sam_gan on 2016/7/6.
 *<p> 获取当前正在使用的ImageLoader的实例
 *
 *<p> e.g:  ImageLoader loader = ImageLoaderUtils.getImageLoader(context);
 *<p>       ... ...
 */
public class ImageLoaderUtils implements IImageLoader {

    public enum Type {
        Glide,
    }

    private static ImageLoaderUtils mInstance;
    private IImageLoader iImageLoader;

    private Type loader_type = Type.Glide;

    public static ImageLoaderUtils getInstance (Context context) {
        if (mInstance == null) {
            mInstance = new ImageLoaderUtils(context);
        }
        return mInstance;
    }

    private ImageLoaderUtils (Context context) {
        switch (loader_type) {
            case Glide:
                iImageLoader = new GlideLoader();
                break;
        }
    }

    public void displayImage (String url, ImageView imageView) {
        iImageLoader.displayImage(url, imageView);
    }

    @Override
    public void displayImage(int id, ImageView imageView) {
        iImageLoader.displayImage(id, imageView);
    }

    @Override
    public void displayImage(String url, ImageView imageView, int imgHolderResId) {
        iImageLoader.displayImage(url, imageView, imgHolderResId);
    }

    public void displayImageWithNoneDefaultImg(String url, ImageView imageView) {
        iImageLoader.displayImageWithNoneDefaultImg(url, imageView);
    }

    public void displayImage (String url, ImageView imageView, int width, int height) {
        iImageLoader.displayImage(url, imageView, width, height);
    }

    public void displayRoundImage (String url, ImageView imageView) {
        iImageLoader.displayRoundImage(url, imageView);
    }

    @Override
    public void displayRoundRectImage(String url, ImageView imageView) {
        iImageLoader.displayRoundRectImage(url, imageView);
    }

    public void loadImage (Context context, final String url, final OnImageloadListener listener) {
        iImageLoader.loadImage(context, url, listener);
    }

    @Override
    public void clearMemCache() {
        iImageLoader.clearMemCache();
    }
}
