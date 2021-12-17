package com.gy.utils.recorder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by yue.gan on 2016/8/6.
 *
 */
public class AudioRecordTask extends Thread {

    private boolean mIsRecording;                   // 标记是否在录音true正在录音，false已经停止录音
    private String mAudioStorePath;                 // 录音存储位置（不分声道）
    private String mLeftChannelStorePath;           // 录音存储位置（左声道）
    private String mRightChannelStorePath;          // 录音存储位置（右声道）
    private OnRecordListener mOnRecordListener;     // 录音回调，包括开始，结束，录音数据（不分声道，左右声道），录音错误
    private AudioRecordInfo mAudioRecordInfo;       // 录音参数（采样率，声道个数，采样格式，buffer大小）

    public AudioRecordTask() {
        this(null, null);
    }

    public AudioRecordTask(String audioStorPath) {
        this(audioStorPath, null);
    }

    public AudioRecordTask(AudioRecordInfo audioRecordInfo) {
        this(null, audioRecordInfo);
    }

    public AudioRecordTask(String audioStorePath, AudioRecordInfo audioRecordInfo) {
        this(audioStorePath, null, null, audioRecordInfo);
    }

    /***
     * 创建一个录音任务，
     * @param audioStorePath        录音文件存储位置（不分声道），传入null则不存储
     * @param leftChannelStorePath  分离出的左声道音频存储位置，传入null则不存储（仅在双声道时起作用）
     * @param rightChannelStorePath 分离出的右声道音频存储位置，传入null则不存储（仅在双声道时起作用）
     * @param audioRecordInfo       音频参数，采样率，声道数，采样格式，buff大小等，传入null则会自动找一个可用参数
     */
    public AudioRecordTask(String audioStorePath, String leftChannelStorePath,
            String rightChannelStorePath, AudioRecordInfo audioRecordInfo) {
        mAudioStorePath = audioStorePath;
        mAudioRecordInfo = audioRecordInfo;
        mLeftChannelStorePath = leftChannelStorePath;
        mRightChannelStorePath = rightChannelStorePath;
    }

    public void setOnRecordListener(OnRecordListener listener) {
        mOnRecordListener = listener;
    }

    @Override
    public void run() {
        doInBackground();
    }

    protected Void doInBackground() {

        if (mAudioRecordInfo == null) {
            // 如果没有传入录音参数，随便找一个支持的录音参数做录音
            List<AudioRecordInfo> allValidInfo = AudioRecordInfo.getAllValidInfo();
            mAudioRecordInfo = allValidInfo.size() > 0? allValidInfo.get(0): null;
        }

        if (mAudioRecordInfo == null) {
            // 依然没能找到可用的参数，报错
            if (mOnRecordListener != null) {
                mOnRecordListener.onRecordError(new Exception(
                        "AudioRecordTask can not create audio recorder"));
            }
            return null;
        }

        AudioRecord audioRecord = new AudioRecord(
                mAudioRecordInfo.source,
                mAudioRecordInfo.rate,
                mAudioRecordInfo.channel,
                mAudioRecordInfo.format,
                mAudioRecordInfo.bufSize);

        try {
            FileOutputStream fOut = null;// 原始PCM
            FileOutputStream lOut = null;// 左声道PCM
            FileOutputStream rOut = null;// 右声道PCM
            boolean is16Bit = mAudioRecordInfo.format == AudioFormat.ENCODING_PCM_16BIT;
            boolean isStereo = mAudioRecordInfo.channel == AudioFormat.CHANNEL_IN_STEREO;
            if (mAudioStorePath != null) fOut = new FileOutputStream(new File(mAudioStorePath));
            if (mLeftChannelStorePath != null && isStereo) lOut = new FileOutputStream(mLeftChannelStorePath);
            if (mRightChannelStorePath != null && isStereo) rOut = new FileOutputStream(mRightChannelStorePath);

            audioRecord.startRecording();
            mIsRecording = true;
            if (mOnRecordListener != null) {
                mOnRecordListener.onRecordStart(
                        mAudioRecordInfo.source,
                        mAudioRecordInfo.rate,
                        mAudioRecordInfo.channel,
                        mAudioRecordInfo.format,
                        mAudioRecordInfo.bufSize,
                        audioRecord.getAudioSessionId());
            }

            int len;
            long currentTime;
            long lastVolumeNotifyTime = 0;
            byte[] buff = new byte[mAudioRecordInfo.bufSize];
            byte[] lBuff = new byte[mAudioRecordInfo.bufSize/2];
            byte[] rBuff = new byte[mAudioRecordInfo.bufSize/2];
            while (mIsRecording) {
                len = audioRecord.read(buff, 0, buff.length);
                if (len == AudioRecord.ERROR_INVALID_OPERATION
                        || len == AudioRecord.ERROR_BAD_VALUE
                        || len == AudioRecord.ERROR_DEAD_OBJECT) {
                    if (mOnRecordListener != null) mOnRecordListener.onRecordError(
                            new IOException("AudioRecordTask recorder is invalid ret=" + len));
                }

                if (mOnRecordListener != null) {
                    mOnRecordListener.onRecord(buff, len);
                }

                if (fOut != null) {
                    fOut.write(buff, 0, len);
                }

                if (isStereo) {
                    // 是双声道，做数据分离
                    int count = 0;
                    if (is16Bit) {
                        for (int i = 3; i < len; i+=4) {
                            // |左声道低字节|左声道高字节|右声道低字节|右声道高字节|左声道低字节|...
                            lBuff[count] = buff[i - 3];
                            lBuff[count + 1] = buff[i - 2];
                            rBuff[count] = buff[i - 1];
                            rBuff[count + 1] = buff[i];
                            count += 2;
                        }
                    } else {
                        for (int i = 1; i < len; i+=2) {
                            // |左声道|右声道|左声道|...
                            lBuff[count] = buff[i - 1];
                            rBuff[count] = buff[i];
                            count += 1;
                        }
                    }
                    if (mOnRecordListener != null) {
                        mOnRecordListener.onRecord(buff, len, lBuff, rBuff);
                    }
                    if (lOut != null) lOut.write(lBuff, 0, count);
                    if (rOut != null) rOut.write(rBuff, 0, count);
                }

                currentTime = System.currentTimeMillis();
                if (currentTime - lastVolumeNotifyTime > 250) {//TODO 音量
                    if (mOnRecordListener != null) mOnRecordListener.onRecordSound(0);
                    lastVolumeNotifyTime = currentTime;
                }
            }

            if (mOnRecordListener != null) {
                mOnRecordListener.onRecordStop();
            }
            if (fOut != null) fOut.close();
            audioRecord.stop();
            audioRecord.release();
        } catch (Exception e) {
            if (mOnRecordListener != null) {
                mOnRecordListener.onRecordError(e);
            }
        }
        return null;
    }

    public void startRecord () {
        start();
    }

    public void stopRecord () {
        this.mIsRecording = false;
    }

    public interface OnRecordListener {
        void onRecordStart (int audioSource, int sampleRateInHz, int channelConfig, int audioFormat,
                            int bufferSizeInBytes, int audioRecordSessionId);
        void onRecordStop ();
        void onRecordError (Exception e);
        void onRecord (byte[] buff, int len);
        void onRecord (byte[] buff, int bufLen, byte[] leftChannel, byte[] rightChannel);
        void onRecordSound (int db);//参数是分贝
    }
}
