package com.android.ganyue.frg;

import android.media.AudioFormat;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.ganyue.R;
import com.android.ganyue.application.MApplication;
import com.gy.appbase.fragment.BaseFragment;
import com.gy.appbase.inject.ViewInject;
import com.gy.utils.file.SdcardUtils;
import com.gy.utils.log.LogUtils;
import com.gy.utils.recorder.AudioRecordInfo;
import com.gy.utils.recorder.AudioRecordTask;
import com.gy.utils.recorder.AudioTrackTask;
import com.gy.widget.wave.VoiceWave;

import java.io.File;

/**
 * Created by yue.gan on 2016/4/30.
 * Nsd test
 */
public class ARecordTestFrg extends BaseFragment {

    @ViewInject (R.id.btn_record)       Button btnRecord;
    @ViewInject (R.id.btn_play)         Button btnPlay;
    @ViewInject (R.id.btn_stop_record)  Button btnStopRecord;
    @ViewInject (R.id.btn_stop_play)    Button btnStopPlay;
    @ViewInject (R.id.btn_delete)       Button btnDelete;
    @ViewInject (R.id.btn_plus)         Button btnPlus;
    @ViewInject (R.id.btn_add)          Button btnAdd;
    @ViewInject (R.id.tv_playfreq)      TextView tvFreq;
    @ViewInject (R.id.vw_voiceWave)     VoiceWave mVWVoiceWave;

    private String soundPath = SdcardUtils.getUsableCacheDir(MApplication.getApplication()) + File.separator + "test";
    private int playFreq;

    private AudioRecordTask recordTask;
    private AudioTrackTask playTask;
    private AudioRecordInfo audioRecordInfo;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_arecord, null);
    }

    @Override
    protected void initViews(View view) {
        LogUtils.enableLog(true);

        audioRecordInfo = AudioRecordInfo.getInstance();
        playFreq = audioRecordInfo.audioRate;

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordTask != null) {
                    return;
                }
                recordTask = new AudioRecordTask(soundPath);
                recordTask.setOnRecordListener(onRecordListener);
                recordTask.execute();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playTask != null) return;
                playTask = new AudioTrackTask(soundPath,
                        playFreq,
                        audioRecordInfo.audioRecordConfig,
                        audioRecordInfo.audioFormat);
                playTask.execute();
                mVWVoiceWave.start();
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

        btnStopPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playTask != null) {
                    playTask.stopPlay();
                    playTask = null;
                    mVWVoiceWave.stop();
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new File(soundPath).delete();
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
        }

        @Override
        public void onRecordSound(int db) {
            LogUtils.d("yue.gan", "record db : " + db);
        }
    };

}
