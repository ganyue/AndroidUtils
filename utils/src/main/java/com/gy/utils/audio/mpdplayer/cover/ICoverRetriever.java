package com.gy.utils.audio.mpdplayer.cover;


import com.gy.utils.audio.mpdplayer.mpd.AlbumInfo;

public interface ICoverRetriever {

    public String[] getCoverUrl(AlbumInfo albumInfo) throws Exception;

    public boolean isCoverLocal();

    public String getName();
}
