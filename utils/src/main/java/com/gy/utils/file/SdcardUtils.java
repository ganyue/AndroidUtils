package com.gy.utils.file;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by sam_gan on 2016/6/17.
 *
 */
public class SdcardUtils {
    /**
     * 检查sd卡是否可读写
     */
    public static boolean isExternalStorageUsable () {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            //可读可写
            return true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            //只读
            return false;
        }
        return false;
    }

    /**
     * 获取sdcard根路径
     */
    public static File getExternalRootDir () {
        if (!isExternalStorageUsable()) {
            return null;
        }

        return Environment.getExternalStorageDirectory();
    }

    /**
     * 获取公共文件夹路径
     */
    public static File getExternalPublicDir (String type) {
        if (!isExternalStorageUsable()) {
            return null;
        }

        File file = Environment.getExternalStoragePublicDirectory(type);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return null;
            }
        }
        return file;
    }

    /**
     * app在sdcard上缓存目录
     */
    public static File getExternalCacheDir (Context context) {
        if (!isExternalStorageUsable()) {
            return null;
        }
        return context.getExternalCacheDir();
    }

    /**
     * app在sdcard上文件存储目录, type为null则返回sdcard上android/data/xxx/file的目录
     */
    public static File getExternalFileDir (Context context, String type) {
        if (!isExternalStorageUsable()) {
            return null;
        }

        return context.getExternalFilesDir(type);
    }

    /**
     * app缓存internal目录
     */
    public static File getCacheDir (Context context) {
        return context.getCacheDir();
    }

    /**
     * app的文件存储internal目录
     */
    public static File getFileDir (Context context) {
        return context.getFilesDir();
    }

    public static File getUsableCacheDir (Context context) {
        File file = getExternalCacheDir(context);
        if (file == null) {
            file = getCacheDir(context);
        }

        return file;
    }
}
