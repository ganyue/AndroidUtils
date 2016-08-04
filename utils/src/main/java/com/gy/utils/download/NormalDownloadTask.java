package com.gy.utils.download;

import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ganyu on 2016/7/27.
 *
 */
public class NormalDownloadTask extends AsyncTask <Void, Integer, Boolean>{

    private DownloadBean bean;
    private OnDownloadListener listener;

    public NormalDownloadTask(DownloadBean bean, OnDownloadListener listener) {
        this.bean = bean;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        if (TextUtils.isEmpty(bean.url)
                || TextUtils.isEmpty(bean.storePath)
                || TextUtils.isEmpty(bean.fileName)) {
            return false;
        }

        try {
            File outDir = new File(bean.storePath);
            if (!outDir.exists() || !outDir.isDirectory()) {
                if (!outDir.mkdirs()) {
                    return false;
                }
            }

            URL url = new URL(bean.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responsCode = connection.getResponseCode();
            if (responsCode < 400) {
                bean.contentLen = connection.getContentLength();
                bean.storedLen = 0;
                bean.lastMordify = "" + System.currentTimeMillis();
                bean.state = DownloadState.DOWNLOADING;

                File outFile = new File(outDir, bean.fileName);
                FileOutputStream fOut = new FileOutputStream(outFile);
                InputStream in = connection.getInputStream();
                byte[] buff = new byte[1024];
                int len;
                byte count = 0;

                if (listener != null) listener.onDownloadStart(bean);
                while ((len = in.read(buff)) > 0) {
                    fOut.write(buff, 0, len);
                    bean.storedLen += len;
                    count ++;
                    if (count > 10) {
                        count = 0;
                        publishProgress(bean.storedLen);
                    }
                }
                publishProgress(bean.storedLen);
                in.close();
                fOut.close();
                connection.disconnect();
                return true;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
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
