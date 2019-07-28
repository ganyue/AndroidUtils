package com.gy.utils.img.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.gy.utils.R;
import com.gy.utils.img.IImageLoader;
import com.gy.utils.img.OnImageloadListener;

/**
 * Created by yue.gan on 2016/12/14.
 *
 */
public class GlideLoader implements IImageLoader{

    public GlideLoader() {
    }

    @Override
    public void displayImage(String url, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .skipMemoryCache(true)
                .placeholder(R.mipmap.bg_imageloader_default)
                .crossFade()
                .into(imageView);
    }

    @Override
    public void displayImage(int id, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(id)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .skipMemoryCache(true)
                .crossFade()
                .into(imageView);
    }

    @Override
    public void displayImage(String url, ImageView imageView, int imgHolderResId) {
        Glide.with(imageView.getContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .skipMemoryCache(true)
                .placeholder(imgHolderResId)
                .crossFade()
                .into(imageView);
    }

    @Override
    public void displayImage(String url, ImageView imageView, int width, int height) {
        Glide.with(imageView.getContext())
                .load(url)
                .override(width, height)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .skipMemoryCache(true)
                .into(imageView);
    }

    @Override
    public void displayImageWithNoneDefaultImg(String url, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .skipMemoryCache(true)
                .crossFade()
                .into(imageView);
    }

    @Override
    public void displayRoundImage(String url, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(url)
                .transform(new GlideRoundImgTransform(imageView.getContext()))
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .skipMemoryCache(true)
                .placeholder(R.mipmap.bg_imageloader_default_round)
                .crossFade()
                .into(imageView);
    }

    @Override
    public void displayRoundRectImage(String url, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(url)
                .transform(new GlideRoundRectImgTransform(imageView.getContext()))
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .skipMemoryCache(true)
                .placeholder(R.mipmap.bg_imageloader_default)
                .crossFade()
                .into(imageView);
    }

    @Override
    public void loadImage(Context context, final String url, final OnImageloadListener listener) {
        Glide.with(context).load(url).asBitmap().skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.RESULT).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                listener.onImageLoadSuccess(url, bitmap);
            }
        });
    }

    @Override
    public void clearMemCache() {
    }
}
