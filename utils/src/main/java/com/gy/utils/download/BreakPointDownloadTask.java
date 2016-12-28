package com.gy.utils.download;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.gy.utils.security.MD5Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ganyu on 2016/7/27.
 * <p>only support download file smaller than 1GB
 */
public class BreakPointDownloadTask extends AsyncTask <Void, Integer, Boolean>{

    private DownloadBean bean;
    private OnDownloadListener listener;

    public BreakPointDownloadTask(DownloadBean bean, OnDownloadListener listener) {
        this.bean = bean;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        if (TextUtils.isEmpty(bean.url)
                || TextUtils.isEmpty(bean.storePath)
                || TextUtils.isEmpty(bean.fileName)) {
            bean.extraMsg = DownloadErrorType.PARAM_ERROR;
            return false;
        }

        try {
            File outDir = new File(bean.storePath);
            if (!outDir.exists() || !outDir.isDirectory()) {
                if (!outDir.mkdirs()) {
                    bean.extraMsg = DownloadErrorType.FILE_CREATE_FAIL;
                    return false;
                }
            }

            URL url = new URL(bean.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            File outFile = new File(outDir, bean.fileName);
            if (outFile.exists() && !outFile.isDirectory()) {
                //file already exists
                bean.storedLen = (int) outFile.length();
            } else {
                //first download
                bean.storedLen = 0;
            }
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Range", "bytes=" + bean.storedLen + "-");
            bean.contentLen = connection.getContentLength() + bean.storedLen;

            int responsCode = connection.getResponseCode();
            if (responsCode < 400) {
                bean.lastMordify = "" + System.currentTimeMillis();
                bean.state = DownloadState.DOWNLOADING;

                //直接使用append，比使用RandomAccessFile方便
                FileOutputStream fOut = new FileOutputStream(outFile, true);
                InputStream in = connection.getInputStream();
                byte[] buff = new byte[1024];   //buffer不宜过大，过大可能会导致阻塞
                int len;
                long lastUpdateTime = 0;
                int lastUpdateLen = bean.storedLen;

                if (listener != null) listener.onDownloadStart(bean);
                while ((len = in.read(buff)) > 0) {
                    fOut.write(buff, 0, len);
                    bean.storedLen += len;
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastUpdateTime > 1000) {
                        //每1秒更新一次进度，防止更新过于频繁拖慢下载速度
                        bean.downSpeed = (int) ((bean.storedLen - lastUpdateLen)
                                /((currentTime - lastUpdateTime)*1f/1000));
                        lastUpdateLen = bean.storedLen;
                        lastUpdateTime = currentTime;
                        publishProgress(bean.storedLen);
                    }
                }
                publishProgress(bean.storedLen);
                in.close();
                fOut.close();
                connection.disconnect();

                //校验文件是否正确
                if (!TextUtils.isEmpty(bean.md5) && !"null".equals(bean.md5)) {
                    String md5Str = MD5Utils.getFileMD5(outFile);
                    if (!bean.md5.equals(md5Str)) {
                        bean.extraMsg = DownloadErrorType.MD5_NOT_MATCH;
                        return false;
                    }
                }

                return true;
            }
            bean.extraMsg = ""+DownloadErrorType.CONNECT_FAIL;
            return false;
        } catch (IOException e) {
            bean.extraMsg = ""+DownloadErrorType.CONNECT_FAIL;
            e.printStackTrace();
            return false;
        }

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (listener != null) listener.onDownloadProgress(bean);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean) {
            bean.state = DownloadState.DOWNLOADED;
            if (listener != null) listener.onDownloadFinished(bean);
        } else {
            bean.state = DownloadState.ERROR;
            if (listener != null) listener.onDownloadError(bean);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (listener != null) listener.onDownloadPause(bean);
    }
}
