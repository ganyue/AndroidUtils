package com.gy.utils.download;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.gy.utils.database.annotation.DBTable;

/**
 * Created by ganyu on 2016/7/27.
 *
 */
@DBTable (primaryKey = "url")
public class DownloadBean implements Parcelable{

    public String url = "";
    public String fileName = "";
    public int contentLen;
    public int storedLen;
    public int downSpeed;
    public String storePath = "";
    public String lastMordify = "";
    public int state;
    public String md5 = "";
    public String extraMsg = "";

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


    protected DownloadBean(Parcel in) {
        url = in.readString();
        fileName = in.readString();
        contentLen = in.readInt();
        storedLen = in.readInt();
        storePath = in.readString();
        lastMordify = in.readString();
        state = in.readInt();
        md5 = in.readString();
        extraMsg = in.readString();
    }

    public static final Creator<DownloadBean> CREATOR = new Creator<DownloadBean>() {
        @Override
        public DownloadBean createFromParcel(Parcel in) {
            return new DownloadBean(in);
        }

        @Override
        public DownloadBean[] newArray(int size) {
            return new DownloadBean[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(fileName);
        dest.writeInt(contentLen);
        dest.writeInt(storedLen);
        dest.writeString(storePath);
        dest.writeString(lastMordify);
        dest.writeInt(state);
        dest.writeString(md5);
        dest.writeString(extraMsg);
    }
}
