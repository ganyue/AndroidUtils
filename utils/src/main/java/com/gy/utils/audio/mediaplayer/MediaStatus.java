package com.gy.utils.audio.mediaplayer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ganyu on 2016/10/10.
 *
 */
public class MediaStatus implements Parcelable {

    public int isPlaying;
    public int duration;
    public int currentTime;
    public int volume;
    public String sourcePath;

    public MediaStatus (){}

    protected MediaStatus(Parcel in) {
        isPlaying = in.readInt();
        duration = in.readInt();
        currentTime = in.readInt();
        volume = in.readInt();
        sourcePath = in.readString();
    }

    public static final Creator<MediaStatus> CREATOR = new Creator<MediaStatus>() {
        @Override
        public MediaStatus createFromParcel(Parcel in) {
            return new MediaStatus(in);
        }

        @Override
        public MediaStatus[] newArray(int size) {
            return new MediaStatus[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(isPlaying);
        dest.writeInt(duration);
        dest.writeInt(currentTime);
        dest.writeInt(volume);
        dest.writeString(sourcePath);
    }
}
