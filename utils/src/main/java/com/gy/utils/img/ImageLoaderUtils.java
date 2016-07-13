package com.gy.utils.img;

import android.content.Context;
import android.graphics.Bitmap;

import com.gy.utils.R;
import com.gy.utils.file.SdcardUtils;
import com.gy.utils.log.LogUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 *<p> Created by sam_gan on 2016/7/6.
 *<p> 获取当前正在使用的ImageLoader的实例
 *
 *<p> e.g:  ImageLoader loader = ImageLoaderUtils.getImageLoader(context);
 *<p>       ... ...
 */
public class ImageLoaderUtils {

    private static ImageLoader imageLoader;

    public static ImageLoader getImageLoader (Context context) {
        if (imageLoader == null) {
            LogUtils.i("ImageLoaderUtils", "init image loader");

            DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.bg_default_gray)
                    .showImageForEmptyUri(R.drawable.bg_default_gray)
                    .showImageOnFail(R.drawable.bg_default_gray)
                    .cacheOnDisk(true)
                    .cacheInMemory(true)
                    .considerExifParams(true)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();

            ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(context)
                    .memoryCacheExtraOptions(480, 800)
                    .diskCacheExtraOptions(480, 800, null)
                    .threadPoolSize(3)
                    .denyCacheImageMultipleSizesInMemory()
                    .memoryCache(new LruMemoryCache(2 * 1024 * 1024))//可以使用WeakMemoryCache;更省内存
                    .memoryCacheSize(2 * 1024 * 1024)
                    .memoryCacheSizePercentage(13)
                    .diskCache(new UnlimitedDiskCache(SdcardUtils.getUsableCacheDir(context))) // 优先放到external
                    .diskCacheSize(60 * 1024 * 1024)
                    .diskCacheFileCount(200)
                    .defaultDisplayImageOptions(displayImageOptions) // default
                    .build();
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(configuration);
        }

        return imageLoader;
    }
}
