package com.android.ganyue.frg;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.ganyue.R;
import com.android.ganyue.controller.FuncCtrl;
import com.gy.appbase.controller.BaseFragmentActivityController;
import com.gy.appbase.fragment.BaseFragment;
import com.gy.appbase.inject.ViewInject;
import com.gy.utils.database.DBHelper;
import com.gy.utils.download.DownloadBean;
import com.gy.utils.download.DownloadManager;
import com.gy.utils.download.OnDownloadListener;
import com.gy.utils.file.SdcardUtils;
import com.gy.utils.log.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yue.gan on 2016/4/30.
 * Nsd test
 */
public class FileUtilTestFrg extends BaseFragment {

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dao_test, null);
    }

    @ViewInject (R.id.list)     private ListView listView;
    private DownloadManager downloadManager;
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

        LogUtils.d("yue.gan", "ex root : " + SdcardUtils.getExternalRootDir());
        LogUtils.d("yue.gan", "ex public down : " + SdcardUtils.getExternalPublicDir(Environment.DIRECTORY_DOWNLOADS));
        LogUtils.d("yue.gan", "ex cache : " + SdcardUtils.getExternalCacheDir(mActivity));
        LogUtils.d("yue.gan", "ap cache : " + SdcardUtils.getCacheDir(mActivity));
        LogUtils.d("yue.gan", "ap file : " + SdcardUtils.getFileDir(mActivity));

        DBHelper dbHelper = new DBHelper(mActivity, "test", 1, null);
        downloadManager = DownloadManager.getInstance(dbHelper);
        DownloadBean bean1 = new DownloadBean("", SdcardUtils.getUsableCacheDir(mActivity).getAbsolutePath());
        DownloadBean bean2 = new DownloadBean("http://zzya.beva.cn/dq/lh7khCFsQnboyQvF5-TCcv04Y-bH.mp3", SdcardUtils.getUsableCacheDir(mActivity).getAbsolutePath());
        DownloadBean bean3 = new DownloadBean("http://zzya.beva.cn/dq/lsIQzDgUq_R2sCRyzByVKI57HXpO.mp3", SdcardUtils.getUsableCacheDir(mActivity).getAbsolutePath());
        DownloadBean bean4 = new DownloadBean("http://zzya.beva.cn/dq/Fq-OR6uRlNaj6zfZkIJf0tki3HMa.mp3", SdcardUtils.getUsableCacheDir(mActivity).getAbsolutePath());
        DownloadBean bean5 = new DownloadBean("http://zzya.beva.cn/dq/FiEehAPEp4UZPEmdBQqWlZWWlRpE.mp3", SdcardUtils.getUsableCacheDir(mActivity).getAbsolutePath());
        List<DownloadBean> beans = new ArrayList<>();
        listView.setAdapter(new MAdapter(beans));
        beans.add(bean1);
        beans.add(bean2);
        beans.add(bean3);
        beans.add(bean4);
        beans.add(bean5);

        downloadManager.addOnDownloadListener(new OnDownloadListener() {
            @Override
            public void onDownloadStart(DownloadBean bean) {
                LogUtils.d("yue.gan", "start : " + bean.url);
            }

            @Override
            public void onDownloadFinished(DownloadBean bean) {
                LogUtils.d("yue.gan", "finished : " + bean.url + " - fileName : " + bean.fileName);
            }

            @Override
            public void onDownloadPause(DownloadBean bean) {
                LogUtils.d("yue.gan", "pause : " + bean.url + " - contentLen ：" + bean.contentLen + " - storedLen : " + bean.storedLen);
            }

            @Override
            public void onDownloadError(DownloadBean bean) {
                LogUtils.d("yue.gan", "error : " + bean.url + " - reason" + bean.extraMsg);
            }

            @Override
            public void onDownloadDelete(DownloadBean bean) {
                LogUtils.d("yue.gan", "delete ： " + bean.url);
            }

            @Override
            public void onDownloadProgress(DownloadBean bean) {
                LogUtils.d("yue.gan", "progress : " + bean.url + " - " + bean.storedLen);
            }
        });
    }

    class MAdapter extends BaseAdapter {

        private List<DownloadBean> beans;

        public MAdapter(List list) {
            beans = list;
        }

        @Override
        public int getCount() {
            return beans.size();
        }

        @Override
        public Object getItem(int position) {
            return beans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LinearLayout layout = new LinearLayout(mActivity);
            layout.setOrientation(LinearLayout.HORIZONTAL);

            Button buttonAdd = new Button(mActivity);
            buttonAdd.setText("add" + position);
            buttonAdd.setTag(position);
            buttonAdd.setOnClickListener(onAddClick);

            Button buttonPause = new Button(mActivity);
            buttonPause.setText("pause" + position);
            buttonPause.setTag(position);
            buttonPause.setOnClickListener(onPauseClick);

            Button buttonDelete = new Button(mActivity);
            buttonDelete.setText("delete" + position);
            buttonDelete.setTag(position);
            buttonDelete.setOnClickListener(onDeleteClick);

            layout.addView(buttonAdd);
            layout.addView(buttonPause);
            layout.addView(buttonDelete);
            return layout;
        }

        private View.OnClickListener onAddClick  = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager.add((DownloadBean) getItem((Integer) v.getTag()));
            }
        };

        private View.OnClickListener onPauseClick  = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager.pauseOrStart((DownloadBean) getItem((Integer) v.getTag()));
            }
        };

        private View.OnClickListener onDeleteClick  = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager.delete((DownloadBean) getItem((Integer) v.getTag()));
            }
        };
    }

    @Override
    protected BaseFragmentActivityController instanceController() {
        return new FuncCtrl(mActivity);
    }
}
