package com.gy.utils.audio.mpdplayer.helpers;


import com.gy.utils.audio.mpdplayer.mpd.AlbumInfo;

public interface CoverDownloadListener {

    public void onCoverDownloaded(CoverInfo cover);

    public void onCoverNotFound(CoverInfo coverInfo);

    public void onCoverDownloadStarted(CoverInfo cover);

    public void tagAlbumCover(AlbumInfo albumInfo);

}
