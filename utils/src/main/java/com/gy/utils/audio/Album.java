package com.gy.utils.audio;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ganyu on 2016/7/19.
 *
 */
public class Album implements Parcelable{

    public int id;
    public int duration;
    public int playNum;
    public int shareNum;
    public int favorNum;
    public String name = "";
    public String picUrl = "";
    public String creator = "";

    public Album(){};

    protected Album(Parcel in) {
        id = in.readInt();
        duration = in.readInt();
        playNum = in.readInt();
        shareNum = in.readInt();
        favorNum = in.readInt();
        name = in.readString();
        picUrl = in.readString();
        creator = in.readString();
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            Album album = new Album(in);
            return album;
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(duration);
        dest.writeInt(playNum);
        dest.writeInt(shareNum);
        dest.writeInt(favorNum);
        dest.writeString(name);
        dest.writeString(picUrl);
        dest.writeString(creator);
    }
}
