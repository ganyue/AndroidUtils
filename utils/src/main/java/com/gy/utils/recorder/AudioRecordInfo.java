package com.gy.utils.recorder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.gy.utils.log.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yue.gan on 2016/8/6.
 *
 */
public class AudioRecordInfo {
    public int source = MediaRecorder.AudioSource.MIC;
    public int rate;
    public int format;
    public int channel;
    public int bufSize;

    private AudioRecordInfo () {}

    private AudioRecordInfo (int rate, int channel, int format) {
        this.rate = rate;
        this.channel = channel;
        this.format = format;
        bufSize = AudioRecord.getMinBufferSize(rate, channel, format);
    }

    /**
     * 如果得不到结果 检查权限，需要录音权限。
     * @return 所有支持的频率、声道、采音格式组合，任取一个都可以成功创建AudioRecord来录音
     */
    public static List<AudioRecordInfo> getAllValidInfo () {
        ArrayList<AudioRecordInfo> ret = new ArrayList<>();
        int[] rates = new int[]{44100, 22050, 16000, 11025, 8000};
        for (int rate : rates) {
            short[] formats = new short[]{AudioFormat.ENCODING_PCM_16BIT, AudioFormat.ENCODING_PCM_8BIT};
            for (short format : formats) {
                short[] channels = new short[]{AudioFormat.CHANNEL_IN_STEREO, AudioFormat.CHANNEL_IN_MONO};
                for (short channel : channels) {
                    AudioRecordInfo info = new AudioRecordInfo(rate, channel, format);
                    try {
                        if (info.bufSize != AudioRecord.ERROR_BAD_VALUE) {
                            AudioRecord recorder = new AudioRecord(
                                    info.source,
                                    info.rate,
                                    info.channel,
                                    info.format,
                                    info.bufSize);
                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                                ret.add(info);
                            }
                            recorder.release();
                        }
                    } catch (Exception e) {
                        LogUtils.d("yue.gan", rate + "Exception, keep trying.");
                    }
                }
            }
        }
        return ret;
    }

    public static AudioRecordInfo get(int rate, int channel, int format) {
        AudioRecordInfo info = new AudioRecordInfo(rate, channel, format);
        try {
            if (info.bufSize != AudioRecord.ERROR_BAD_VALUE) {
                AudioRecord recorder = new AudioRecord(
                        info.source,
                        info.rate,
                        info.channel,
                        info.format,
                        info.bufSize);
                if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                    return info;
                }
                recorder.release();
            }
        } catch (Exception e) {
            LogUtils.d("yue.gan", rate + "Exception, keep trying.");
        }
        return null;
    }
}
