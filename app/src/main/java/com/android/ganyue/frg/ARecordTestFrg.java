package com.android.ganyue.frg;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ganyue.R;
import com.android.ganyue.application.MApplication;
import com.gy.appbase.fragment.BaseFragment;
import com.gy.appbase.inject.ViewInject;
import com.gy.utils.bluetooth.BltHeadsetManager;
import com.gy.utils.file.SdcardUtils;
import com.gy.utils.log.LogUtils;
import com.gy.utils.recorder.AudioRecordInfo;
import com.gy.utils.recorder.AudioRecordTask;
import com.gy.utils.recorder.AudioTrackTask;
import com.gy.widget.wave.TestView;
import com.gy.widget.wave.VoiceWave;

import java.io.File;

/**
 * Created by yue.gan on 2016/4/30.
 * Nsd test
 */
public class ARecordTestFrg extends BaseFragment {

    @ViewInject (R.id.btn_scoStart)       Button btnScoStart;
    @ViewInject (R.id.btn_scoStop)       Button btnScoStop;
    @ViewInject (R.id.btn_record)       Button btnRecord;
    @ViewInject (R.id.btn_play)         Button btnPlay;
    @ViewInject (R.id.btn_playL)        Button btnPlayL;
    @ViewInject (R.id.btn_playR)        Button btnPlayR;
    @ViewInject (R.id.btn_stop_record)  Button btnStopRecord;
    @ViewInject (R.id.btn_stop_play)    Button btnStopPlay;
    @ViewInject (R.id.btn_delete)       Button btnDelete;
    @ViewInject (R.id.btn_plus)         Button btnPlus;
    @ViewInject (R.id.btn_add)          Button btnAdd;
    @ViewInject (R.id.tv_playfreq)      TextView tvFreq;
    @ViewInject (R.id.vw_voiceWave)     VoiceWave mVWVoiceWave;

    private String soundPath = SdcardUtils.getUsableCacheDir(MApplication.getApplication()) +
            File.separator + "test2";
    private String soundPathL = SdcardUtils.getUsableCacheDir(MApplication.getApplication()) +
            File.separator + "test";
    private String soundPathR = SdcardUtils.getUsableCacheDir(MApplication.getApplication()) +
            File.separator + "testr";
    private int playFreq;

    private AudioRecordTask recordTask;
    private AudioRecordTask recordTask2;
    private AudioTrackTask playTask;
    private BltHeadsetManager mBltHeadsetManager;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_arecord, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBltHeadsetManager.stopSco();
        mBltHeadsetManager.release();
    }

    @Override
    protected void initViews(View view) {
        LogUtils.enableLog(true);

        AudioManager am = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        am.setMode(AudioManager.MODE_IN_COMMUNICATION);

        mBltHeadsetManager = new BltHeadsetManager(getContext());
        mBltHeadsetManager.setOnHeadSetManagerCallback(new BltHeadsetManager.OnHeadSetManagerCallback() {
            @Override
            public void onServiceConnected() {
                LogUtils.d("yue.gan", "onServiceConnected");
            }

            @Override
            public void onServiceDisconnected() {
                LogUtils.d("yue.gan", "onServiceDisconnected");
            }

            @Override
            public void onScoOn() {
                LogUtils.d("yue.gan", "onScoOn");
            }

            @Override
            public void onScoOff() {
                LogUtils.d("yue.gan", "onScoOff");
            }
        });

        final AudioRecordInfo audioRecordInfo = AudioRecordInfo.get(16000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        playFreq = audioRecordInfo.rate;
        tvFreq.setText(""+playFreq);

        view.findViewById(R.id.v_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!((TestView)v).start(soundPath))((TestView)v).stop();
            }
        });

        btnScoStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBltHeadsetManager.startSco();
            }
        });

        btnScoStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBltHeadsetManager.stopSco();
            }
        });

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordTask != null) {
                    return;
                }
                recordTask = new AudioRecordTask(soundPath, soundPathL, soundPathR, audioRecordInfo);
                recordTask.setOnRecordListener(onRecordListener);
                recordTask.startRecord();
            }
        });

        btnStopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordTask != null) {
                    recordTask.stopRecord();
                    recordTask = null;
                }
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playTask != null) playTask.stopPlay();
                playTask = new AudioTrackTask(soundPath, playFreq,
                        audioRecordInfo.channel, audioRecordInfo.format);
                playTask.start();
            }
        });

        btnPlayL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playTask != null) playTask.stopPlay();
                playTask = new AudioTrackTask(soundPathL, playFreq,
                        AudioFormat.CHANNEL_OUT_MONO, audioRecordInfo.format);
                playTask.start();
            }
        });

        btnPlayR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playTask != null) playTask.stopPlay();

                playTask = new AudioTrackTask(null, playFreq,
                        AudioFormat.CHANNEL_IN_MONO, audioRecordInfo.format);
                playTask.start();
                LogUtils.d("yue.gan", "btnPlayR");


//                playTask = new AudioTrackTask(soundPathR, playFreq,
//                        AudioFormat.CHANNEL_OUT_MONO, audioRecordInfo.format);
//                playTask.execute();
            }
        });

        btnStopPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playTask != null) {
                    playTask.stopPlay();
                    playTask = null;
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new File(soundPath).delete();
                new File(soundPathL).delete();
                new File(soundPathR).delete();
            }
        });

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playFreq -= 1000;
                tvFreq.setText(""+playFreq);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playFreq += 1000;
                tvFreq.setText(""+playFreq);
            }
        });
        tvFreq.setText(""+playFreq);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void activityCall(int type, Object extra) {

    }

    private AudioRecordTask.OnRecordListener onRecordListener = new AudioRecordTask.OnRecordListener() {
        @Override
        public void onRecordStart(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSizeInBytes, int audioRecordSessionId) {
            LogUtils.d("yue.gan", "onRecordStart sampleRateInHz="+sampleRateInHz);
            mVWVoiceWave.start();
        }

        @Override
        public void onRecordStop() {
            LogUtils.d("yue.gan", "stop record");
            mVWVoiceWave.stop();
        }

        @Override
        public void onRecordError(Exception e) {
            LogUtils.d("yue.gan", "record error");
        }

        @Override
        public void onRecord(byte[] buff, int len) {
            if (playTask != null) playTask.writeData(buff);
        }

        @Override
        public void onRecord(byte[] buff, int bufLen, byte[] leftChannel, byte[] rightChannel) {
        }

        @Override
        public void onRecordSound(int db) {
            LogUtils.d("yue.gan", "record db : " + db);
        }
    };

}
