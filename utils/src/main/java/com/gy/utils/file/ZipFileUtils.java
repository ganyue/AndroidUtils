package com.gy.utils.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by sam_gan on 2016/6/17.
 *
 */
public class ZipFileUtils {

    /**
     * zipfile will be unpacked at the given path
     * if param deleteSource is true, the zip file will be deleted after
     * the zip unpacked;
     */
    public static void unpackZip (
            final String zipPath,
            final String dstPath,
            final boolean runInNewThread,
            final boolean deleteSource,
            final OnZipFileCallback callback) {

        if (!runInNewThread) {
            unzip(zipPath, dstPath, deleteSource, callback);
            return;
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                unzip(zipPath, dstPath, deleteSource, callback);
            }
        }).start();

    }

    private static void unzip (
            final String zipPath,
            final String dstPath,
            final boolean deleteSource,
            final OnZipFileCallback callback) {

        try {
            ZipFile zipFile = new ZipFile(zipPath);

            File dstFile = new File(dstPath);
            if (!dstFile.exists()) {
                if (!dstFile.mkdirs()) {
                    callback.onUnpackFail(new IOException("can not create dir : " + dstPath));
                    return;
                }
            }

            Enumeration<ZipEntry> entrys = (Enumeration<ZipEntry>) zipFile.entries();

            while (entrys.hasMoreElements()) {
                ZipEntry entry = entrys.nextElement();
                String entryName = entry.getName();
                InputStream in = zipFile.getInputStream(entry);
                String outPath = (dstPath + File.separator + entryName).replaceAll("\\*", "/");

                File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));

                if (!file.exists()) {
                    if (!file.mkdirs()) {
                        callback.onUnpackFail(new IOException("can not create dir : " + file.getPath()));
                    }
                }

                if (new File(outPath).isDirectory()) {
                    continue;
                }

                OutputStream out = new FileOutputStream(outPath);

                byte[] buf = new byte[1024];
                int len = 0;

                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                in.close();
                out.close();
            }

            if (deleteSource) {
                File file = new File(zipPath);
                file.delete();
            }

            callback.onUnpackSuccess();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnZipFileCallback {
        void onUnpackSuccess ();
        void onUnpackFail (Exception e);
    }
}
