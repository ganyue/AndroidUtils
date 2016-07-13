package com.android.ganyue.frg;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.ganyue.R;
import com.android.ganyue.controller.FuncCtrl;
import com.gy.appbase.controller.BaseFragmentActivityController;
import com.gy.appbase.fragment.BaseFragment;
import com.gy.utils.file.SdcardUtils;

/**
 * Created by ganyu on 2016/4/30.
 * Nsd test
 */
public class FileUtilTestFrg extends BaseFragment {

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dao_test, null);
    }

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        //test copy
//        FileUtils.copyFiles("/mnt/sdcard/test", SdcardUtils.getFileDir(mActivity).getPath(), new FileUtils.OnCopyCallback() {
//            @Override
//            public void onCopyFile(File file) {
//                Log.d("yue.gan", "copy ： " + file.getPath());
//            }
//
//            @Override
//            public void onCopying(File file, long copiedSize, long fileSize) {
//                Log.d("yue.gan", "copyed ： " + copiedSize * 1.0 / fileSize);
//            }
//
//            @Override
//            public void onCopyFinished() {
//                Log.d("yue.gan", "copy finished");
//            }
//        });

//        Log.d("yue.gan", "sina size : " + FileUtils.getFileSize("/mnt/sdcard/sina/"));
//        Log.d("yue.gan", "sinaCopy size : " + FileUtils.getFileSize("/mnt/sdcard/sinaCopy/"));

//        Log.d("yue.gan", "sina count : " + FileUtils.count("/mnt/sdcard/sina/"));
//        Log.d("yue.gan", "sinaCopy count : " + FileUtils.count("/mnt/sdcard/sinaCopy/"));

//        FileUtils.deleteFiles(SdcardUtils.getFileDir(mActivity).getPath(), new FileUtils.OnDeleteCallback() {
//            @Override
//            public void onDeleteFile(File file) {
//            }
//
//            @Override
//            public void onDeleteFinished() {
//                Log.d("yue.gan", "Delete Finished");
//            }
//        });

        Log.d("yue.gan", "ex root : " + SdcardUtils.getExternalRootDir());
        Log.d("yue.gan", "ex public down : " + SdcardUtils.getExternalPublicDir(Environment.DIRECTORY_DOWNLOADS));
        Log.d("yue.gan", "ex cache : " + SdcardUtils.getExternalCacheDir(mActivity));
        Log.d("yue.gan", "ap cache : " + SdcardUtils.getCacheDir(mActivity));
        Log.d("yue.gan", "ap file : " + SdcardUtils.getFileDir(mActivity));

    }

    @Override
    protected BaseFragmentActivityController instanceController() {
        return new FuncCtrl(mActivity);
    }
}
