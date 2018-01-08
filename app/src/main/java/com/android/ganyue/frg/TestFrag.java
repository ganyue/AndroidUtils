package com.android.ganyue.frg;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.ganyue.R;
import com.gy.appbase.controller.BaseFragmentActivityController;
import com.gy.appbase.fragment.BaseFragment;
import com.gy.appbase.inject.ViewInject;
import com.gy.utils.constants.DeviceConstants;
import com.gy.utils.file.SdcardUtils;
import com.gy.utils.http.OnRequestListener;
import com.gy.utils.log.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ganyu on 2016/8/29.
 *
 */
public class TestFrag extends BaseFragment {

    List<Fragment> fragment;
    @ViewInject(R.id.pb_progress)  private ProgressBar pbProgress;
    @ViewInject(R.id.tv_test)      private TextView tvTest;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.d("yue.gan", "mac : " + DeviceConstants.getUniqueCode(mActivity));
        return inflater.inflate(R.layout.fragment_testfrag, container, false);

    }

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        pbProgress.setProgress(20);

        tvTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mActivity, TestActivity.class);
//                mActivity.startActivity(intent);

//                MApplication.getHttpUtils().getString("http://baidu.com", new OnRequestListener() {
//                    @Override
//                    public void onResponse(String url, Object responseData) {
//                        LogUtils.d("yue.gan", "onResponse : " + responseData);
//                    }
//
//                    @Override
//                    public void onError(String msg) {
//                        LogUtils.d("yue.gan", "onError : " + msg);
//                    }
//                });

//                try {
//                    Intent intent = new Intent();
//                    intent.setClassName("com.beva.bevatingting", "com.beva.bevatingting.game.bela.AppActivity");
//                    startActivity(intent);
//                }catch (Exception e) {
//                    try {
//                        String str = "market://details?id=com.beva.bevatingting";
//                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                        intent.setData(Uri.parse(str));
//                        startActivity(intent);
//                    } catch (Exception e1) {
//                        e.printStackTrace();
//
//                        String str = "http://a.app.qq.com/o/simple.jsp?pkgname=com.beva.bevatingting";
//                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                        intent.setData(Uri.parse(str));
//                        startActivity(intent);
//                    }
//                }

                new Thread() {
                    @Override
                    public void run() {
                        downLoadResSync("https://zzya.beva.cn/img/lkrMZhu_U5INMw-2HTMq5v12q2A7.zip",
                                "tmp.zip", SdcardUtils.getExternalFileDir(mActivity, null).getAbsolutePath(),
                                new OnRequestListener() {
                                    @Override
                                    public void onResponse(String url, final Object responseData) {
                                        mActivity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                LogUtils.d("yue.gan", "onResponse : "+responseData);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(String msg) {
                                        LogUtils.d("yue.gan", "onError : "+msg);
                                    }
                                });
                    }
                }.start();

            }
        });
    }

    /**
     * 下载文件,同步
     * @param downUrl               文件下载地址
     * @param fileName              文件名
     * @param fileCachePath         文件缓存位置
     * @param onRequestListener     进度回调
     */
    public boolean downLoadResSync (
            final String downUrl,
            final String fileName,
            final String fileCachePath,
            final OnRequestListener onRequestListener) {
        try {
            Request request = new Request.Builder()
                    .url(downUrl)
                    .build();
            Response response = new OkHttpClient().newCall(request).execute();
            long contentLen = response.body().contentLength();
            File fileCacheDir = new File(fileCachePath);
            File file = new File(fileCacheDir, fileName);
            fileCacheDir.mkdirs();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            InputStream inputStream = response.body().byteStream();
            byte[] buf = new byte[1024];
            int len = 0;
            long totleLen = 0;
            long reportTime = System.currentTimeMillis();
            while ((len = inputStream.read(buf)) != -1) {
                fileOutputStream.write(buf, 0, len);
                long currentTime = System.currentTimeMillis();
                //每1s汇报一次进度
                if (currentTime - reportTime > 1000) {
                    onRequestListener.onResponse(downUrl, totleLen*1f/contentLen);
                    reportTime = currentTime;
                }
                totleLen += len;
            }

            fileOutputStream.close();
            inputStream.close();
            response.body().close();
            onRequestListener.onResponse(downUrl, 1.0f);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            onRequestListener.onError(e.toString());
        }
        return false;
    }
    @Override
    protected BaseFragmentActivityController instanceController() {
        return null;
    }
}
