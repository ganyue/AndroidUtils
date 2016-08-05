package com.gy.utils.download;

import android.text.TextUtils;

import com.gy.utils.database.annotation.DBTable;

/**
 * Created by ganyu on 2016/7/27.
 *
 */
@DBTable (primaryKey = "url")
public class DownloadBean {

    public String url;
    public String fileName;
    public int contentLen;
    public int storedLen;
    public String storePath;
    public String lastMordify;
    public int state;
    public String md5;
    public String extraMsg;

    public DownloadBean() {
    }

    public DownloadBean (String url, String storePath) {
        this.url = url;
        this.storePath = storePath;
        this.lastMordify = ""+System.currentTimeMillis();
        this.state = DownloadState.WAITEING;
        initFileName();
    }

    public DownloadBean (String url, String storePath, String md5) {
        this(url, storePath);
        this.md5 = md5;
    }


    //文件名后带上url的hashcode, 方便将来希望看看是否已下载过的时候拿它来做比较
    private void initFileName () {
        if (TextUtils.isEmpty(url)) {
            fileName = "";
            return;
        }

        int lastPathIndex = url.lastIndexOf('/');
        if (lastPathIndex == -1) {
            return;
        }
        fileName = url.substring(lastPathIndex + 1);
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            fileName += "_" + url.hashCode();
        } else {
            fileName = fileName.substring(0, lastDotIndex) + "_" + url.hashCode() + fileName.substring(lastDotIndex);
        }
    }
}
