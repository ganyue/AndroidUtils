package com.gy.utils.recorder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.gy.utils.log.LogUtils;

/**
 * Created by yue.gan on 2016/8/6.
 *
 */
public class AudioRecordInfo {

    private static AudioRecordInfo mInstance;

    public static AudioRecordInfo getInstance () {
        if (mInstance == null) {
            mInstance = new AudioRecordInfo();
        }
        return mInstance;
    }

    public int audioSource = MediaRecorder.AudioSource.MIC;
    public int audioRate;
    public int audioFormat;
    public int audioRecordConfig;
    public int audioRecordMinBuffSize;

    public void changeInfo (int rate, int config, int format) {
        audioRate = rate;
        audioRecordConfig = config;
        audioFormat = format;
        audioRecordMinBuffSize = AudioRecord.getMinBufferSize(rate, config, format);
    }

    public AudioRecordInfo () {
        int[] rates = new int[]{44100, 22050, 11025, 8000};
        for (int rate : rates) {
            short[] format1 = new short[]{AudioFormat.ENCODING_PCM_16BIT, AudioFormat.ENCODING_PCM_8BIT};
            for (short format : format1) {
                short[] recordConfig = new short[]{AudioFormat.CHANNEL_IN_STEREO, AudioFormat.CHANNEL_IN_MONO};
                for (short channelConfig : recordConfig) {
                    try {
                        audioRate = rate;
                        audioFormat = format;
                        audioRecordConfig = channelConfig;

                        audioRecordMinBuffSize = AudioRecord.getMinBufferSize(rate, channelConfig, format);

                        if (audioRecordMinBuffSize != AudioRecord.ERROR_BAD_VALUE) {
                            // check if we can instantiate and have a success

                            AudioRecord recorder = new AudioRecord(
                                    audioSource,
                                    rate,
                                    channelConfig,
                                    format,
                                    audioRecordMinBuffSize);

                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                                recorder.release();
                                return;
                            }
                            recorder.release();
                        }
                    } catch (Exception e) {
                        LogUtils.d("yue.gan", rate + "Exception, keep trying.");
                    }
                }
            }
        }
    }
}
