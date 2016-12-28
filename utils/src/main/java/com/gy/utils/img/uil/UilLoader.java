package com.gy.utils.img.uil;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.gy.utils.R;
import com.gy.utils.file.SdcardUtils;
import com.gy.utils.img.IImageLoader;
import com.gy.utils.img.OnImageloadListener;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by ganyu on 2016/12/14.
 *
 */
public class UilLoader implements IImageLoader {

    private ImageLoader imageLoader;

    public UilLoader(Context context) {
        initImageLoader(context);
    }

    private ImageLoader initImageLoader (Context context) {
        if (imageLoader == null) {
            DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
                    .cacheOnDisk(true)
                    .cacheInMemory(false)
                    .considerExifParams(true)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .showImageOnLoading(R.drawable.bg_imageloader_default)
                    .showImageForEmptyUri(R.drawable.bg_imageloader_default)
                    .showImageOnFail(R.drawable.bg_imageloader_default)
                    .build();

            ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(context)
                    .memoryCacheExtraOptions(400, 600)
                    .diskCacheExtraOptions(400, 600, null)
                    .threadPoolSize(3)
                    .denyCacheImageMultipleSizesInMemory()
                    .memoryCache(new WeakMemoryCache())//可以使用WeakMemoryCache;更省内存
                    .memoryCacheSize(2 * 1024 * 1024)
                    .memoryCacheSizePercentage(13)
                    .diskCache(new UnlimitedDiskCache(SdcardUtils.getUsableCacheDir(context))) // 优先放到external
                    .diskCacheSize(64 * 1024 * 1024)
                    .diskCacheFileCount(200)
                    .defaultDisplayImageOptions(displayImageOptions) // default
                    .build();
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(configuration);


//            DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
//                    .showImageOnLoading(R.mipmap.bg_imageloader_default)
//                    .showImageOnFail(R.mipmap.bg_imageloader_default)
//                    .cacheInMemory(false)
//                    .cacheOnDisk(true)
//                    .build();
//            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
//                    .defaultDisplayImageOptions(displayImageOptions)
//                    .diskCacheSize(100*1024*1024)
//                    .memoryCacheSize((int) (Runtime.getRuntime().maxMemory()/4))
//                    .memoryCache(new WeakMemoryCache())
//                    .build();
//            imageLoader = ImageLoader.getInstance();
//            imageLoader.init(config);
        }

        return imageLoader;
    }

    @Override
    public void displayImage(String url, ImageView imageView) {
        imageLoader.displayImage(url, imageView);
    }

    @Override
    public void displayImage(String url, ImageView imageView, int imgHolderResId) {
        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(false)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(imgHolderResId)
                .showImageForEmptyUri(imgHolderResId)
                .showImageOnFail(imgHolderResId)
                .build();
        imageLoader.displayImage(url, imageView, displayImageOptions);
    }

    @Override
    public void displayImage(String url, ImageView imageView, int width, int height) {
        imageLoader.displayImage(url, imageView, new ImageSize(width, height));
    }

    @Override
    public void displayImageWithNoneDefaultImg(String url, ImageView imageView) {
        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(false)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader.displayImage(url, imageView, displayImageOptions);
    }

    @Override
    public void displayRoundImage(String url, ImageView imageView) {
        imageLoader.displayImage(url, imageView);
    }

    @Override
    public void loadImage(Context context, String url, final OnImageloadListener listener) {
        imageLoader.loadImage(url, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                listener.onImageLoadError(imageUri, failReason);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                listener.onImageLoadSuccess(imageUri, loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                listener.onImageLoadError(imageUri, "canceled by user");
            }
        });
    }

    @Override
    public void clearMemCache() {
        imageLoader.clearMemoryCache();
    }
}
