package com.gy.utils.file;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import com.gy.utils.constants.AppConstants;
import com.gy.utils.log.LogUtils;

import java.io.File;

/**
 * Created by sam_gan on 2016/6/17.
 *
 */
public class SdcardUtils {
    /**
     * <p>检查sd卡是否可读写
     * <p>需要的时候，知道外存能否读写就行了， 何必再搞个外存设备的监听呢
     *
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

    public static long getUsableCacheStorage () {
        if (!isExternalStorageUsable()) {
            return getSystemUsableStorage();
        }
        return getExternalUsableStorage();
    }

    public static long getExternalUsableStorage () {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs statFs = new StatFs(sdcardDir.getPath());
            long bolckSize = statFs.getBlockSize();
            long avaiBlocks = statFs.getAvailableBlocks();
            return avaiBlocks * bolckSize;
        }
        return 0;
    }

    public static long getSystemUsableStorage () {
        File root = Environment.getRootDirectory();
        StatFs statFs = new StatFs(root.getPath());
        long bolckSize = statFs.getBlockSize();
        long avaiBlocks = statFs.getAvailableBlocks();
        return avaiBlocks * bolckSize;
    }

    public static String getUsableDownloadDir(Context context) {
        File root = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (root == null) {
            if (isExternalStorageUsable() && getExternalRootDir() != null) {
                String externalRoot = getExternalRootDir().getAbsolutePath();
                return externalRoot + File.separator + "beva" + File.separator + "tingting";
            }
            return getFileDir(context).getAbsolutePath()+File.separator+"Download";
        }
        return root.getAbsolutePath();
    }

    public static String getUsableDownloadMp3Dir (Context context) {
        File root = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (root == null) {
            if (isExternalStorageUsable() && getExternalRootDir() != null) {
                String externalRoot = getExternalRootDir().getAbsolutePath();
                String appExternalPath = externalRoot + File.separator + "Android" + File.separator + "data"
                        + File.separator + AppConstants.getPackageName(context) + File.separator + "files"
                        + File.separator + "Music";
                File file = new File(appExternalPath);
                if (file.mkdirs()) return appExternalPath;
                return externalRoot + File.separator + "beva" + File.separator + "tingting" + File.separator + "Music";
            }
            return getFileDir(context).getAbsolutePath()+File.separator+"Music";
        }
        LogUtils.d("yue.gan", "mp3 download path : " + root.getAbsolutePath());
        return root.getAbsolutePath();
    }
}
