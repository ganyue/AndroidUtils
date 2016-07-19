package com.gy.utils.audio;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ganyu on 2016/7/19.
 *
 */
public class Track implements Parcelable{

    public String localPath = "";

    public int id;
    public int duration;
    public int size;
    public double rating;
    public String name = "";
    public String mp3Url = "";
    public String picUrl = "";
    public String singer = "";

    public Track (){};

    protected Track(Parcel in) {
        localPath = in.readString();
        id = in.readInt();
        duration = in.readInt();
        size = in.readInt();
        rating = in.readDouble();
        name = in.readString();
        mp3Url = in.readString();
        picUrl = in.readString();
        singer = in.readString();
    }

    public static final Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) {
            Track track = new Track(in);
            return track;
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(localPath);
        dest.writeInt(id);
        dest.writeInt(duration);
        dest.writeInt(size);
        dest.writeDouble(rating);
        dest.writeString(name);
        dest.writeString(mp3Url);
        dest.writeString(picUrl);
        dest.writeString(singer);
    }
}
