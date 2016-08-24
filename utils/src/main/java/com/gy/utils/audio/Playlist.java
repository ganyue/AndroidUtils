package com.gy.utils.audio;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by ganyu on 2016/7/20.
 *
 */
public class Playlist implements Parcelable{
    private int mode;
    private int currentPos;
    private String name;
    private Album album;
    private List<Track> tracks;

    public Playlist () {
    }

    public Playlist (List<Track> tracks) {
        setTracks(tracks);
    }

    public String getName () {return name;}

    public void setName (String name) {this.name = name;}

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        if (this.tracks == null) {
            this.tracks = Collections.synchronizedList(new ArrayList<Track>());
        }
        currentPos = 0;
        this.tracks.clear();
        this.tracks.addAll(tracks);
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public void setCurrentPos (int pos) {
        currentPos = pos % tracks.size();
    }

    public int getCurrentPos () {
        return currentPos;
    }

    public Track getCurrentTrack () {
        if (currentPos >= tracks.size()) {
            return null;
        }
        return tracks.get(currentPos);
    }

    public Track next () {
        if (tracks == null || tracks.size() <= 0) {
            return null;
        }

        switch (mode) {
            case AudioPlayerConst.Mode.NORMAL:
                if ((currentPos + 1) >= tracks.size()) {
                    currentPos = tracks.size() - 1;
                    return null;
                } else {
                    currentPos = (currentPos + 1) % tracks.size();
                }
                break;
            case AudioPlayerConst.Mode.RANDOM:
                Random random = new Random(System.currentTimeMillis());
                currentPos = Math.abs(random.nextInt()) % tracks.size();
                break;
            case AudioPlayerConst.Mode.REPEAT_ONE:
                if (currentPos >= tracks.size()) {
                    currentPos = 0;
                }
                break;
            case AudioPlayerConst.Mode.REPEAT_ALL:
                currentPos = (currentPos + 1) % tracks.size();
                break;
            default:
                return null;
        }

        return tracks.get(currentPos);
    }

    public Track prev () {
        if (tracks == null || tracks.size() <= 0) {
            return null;
        }

        switch (mode) {
            case AudioPlayerConst.Mode.NORMAL:
                if ((currentPos - 1) < 0) {
                    currentPos = 0;
                    return null;
                } else {
                    currentPos = (currentPos - 1) % tracks.size();
                }
                break;
            case AudioPlayerConst.Mode.RANDOM:
                Random random = new Random(System.currentTimeMillis());
                currentPos = Math.abs(random.nextInt()) % tracks.size();
                break;
            case AudioPlayerConst.Mode.REPEAT_ONE:
                if (currentPos >= tracks.size()) {
                    currentPos = tracks.size() - 1;
                }
                break;
            case AudioPlayerConst.Mode.REPEAT_ALL:
                currentPos -= 1;
                if (currentPos < 0) {
                    currentPos = tracks.size() - 1;
                }
                break;
            default:
                return null;
        }

        return tracks.get(currentPos);
    }


    //parcelable
    protected Playlist(Parcel in) {
        mode = in.readInt();
        currentPos = in.readInt();
        name = in.readString();
        album = in.readParcelable(Album.class.getClassLoader());
        tracks = in.createTypedArrayList(Track.CREATOR);
    }

    public static final Creator<Playlist> CREATOR = new Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mode);
        dest.writeInt(currentPos);
        dest.writeString(name);
        dest.writeParcelable(album, flags);
        dest.writeTypedList(tracks);
    }
}
