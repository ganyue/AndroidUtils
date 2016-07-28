package com.gy.utils.download.mine;

/**
 * Created by ganyu on 2016/7/27.
 *
 */
public interface OnDownloadListener {
    void onDownloadStart (final DownloadBean bean);
    void onDownloadFinished (final DownloadBean bean);
    void onDownloadPause (final DownloadBean bean);
    void onDownloadError (final DownloadBean bean);
    void onDownloadDelete (final DownloadBean bean);
    void onDownloadProgress (final DownloadBean bean);
}
