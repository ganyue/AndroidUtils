package com.android.ganyue.frg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.ganyue.R;
import com.android.ganyue.application.MApplication;
import com.android.ganyue.controller.FuncCtrl;
import com.gy.appbase.controller.BaseFragmentActivityController;
import com.gy.appbase.fragment.BaseFragment;
import com.gy.appbase.inject.ViewInject;
import com.gy.utils.file.SdcardUtils;
import com.gy.utils.log.LogUtils;
import com.gy.xunfei.XunfeiUtils;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;

import java.io.File;

/**
 * Created by yue.gan on 2016/4/30.
 * Nsd test
 */
public class XunFeiTestFrg extends BaseFragment {

    @ViewInject (R.id.btn_v2t)          Button btnV2T;
    @ViewInject (R.id.btn_v2c)          Button btnV2C;
    @ViewInject (R.id.btn_t2v)          Button btnT2V;
    @ViewInject (R.id.btn_stop_v2t)     Button btnStopV2T;
    @ViewInject (R.id.btn_stop_v2c)     Button btnStopV2C;
    @ViewInject (R.id.btn_stop_t2v)     Button btnStopT2V;

    private String soundPath = SdcardUtils.getUsableCacheDir(MApplication.getApplication()) + File.separator + "test";

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_xunfei, null);
    }

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        btnV2T.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XunfeiUtils.getInstance(MApplication.getApplication()).startRecognizeSpeech(new RecognizerListener() {
                    @Override
                    public void onVolumeChanged(int i, byte[] bytes) {
                        LogUtils.d("yue.gan", "onVolumeChanged");
                    }

                    @Override
                    public void onBeginOfSpeech() {
                        LogUtils.d("yue.gan", "onBeginOfSpeech");
                    }

                    @Override
                    public void onEndOfSpeech() {
                        LogUtils.d("yue.gan", "onEndOfSpeech");
                    }

                    @Override
                    public void onResult(RecognizerResult recognizerResult, boolean b) {
                        LogUtils.d("yue.gan", "onResult : " + recognizerResult.getResultString() + " -- is end : " + b);
                    }

                    @Override
                    public void onError(SpeechError speechError) {
                        LogUtils.d("yue.gan", "onError : " + speechError);
                    }

                    @Override
                    public void onEvent(int i, int i1, int i2, Bundle bundle) {
                        LogUtils.d("yue.gan", "onEvent");
                    }
                });
            }
        });

        btnStopV2T.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XunfeiUtils.getInstance(MApplication.getApplication()).stopRecognizeSpeech();
            }
        });

        btnT2V.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XunfeiUtils.getInstance(MApplication.getApplication()).textToVoice(soundPath, "熊猫眼睛像红枣", new SynthesizerListener() {
                    @Override
                    public void onSpeakBegin() {
                        LogUtils.d("yue.gan", "onSpeakBegin");
                    }

                    @Override
                    public void onBufferProgress(int i, int i1, int i2, String s) {
                        LogUtils.d("yue.gan", "onBufferProgress : " + i);
                    }

                    @Override
                    public void onSpeakPaused() {
                        LogUtils.d("yue.gan", "onSpeakPaused");
                    }

                    @Override
                    public void onSpeakResumed() {
                        LogUtils.d("yue.gan", "onSpeakResumed");
                    }

                    @Override
                    public void onSpeakProgress(int i, int i1, int i2) {
                        LogUtils.d("yue.gan", "onSpeakProgress : " + i);
                    }

                    @Override
                    public void onCompleted(SpeechError speechError) {
                        LogUtils.d("yue.gan", "onCompleted");
                    }

                    @Override
                    public void onEvent(int i, int i1, int i2, Bundle bundle) {
                        LogUtils.d("yue.gan", "onEvent");
                    }
                });
            }
        });
    }

    @Override
    protected BaseFragmentActivityController instanceController() {
        return new FuncCtrl(mActivity);
    }
}
