package com.gy.utils.recorder;

import android.media.AudioRecord;
import android.os.AsyncTask;
import android.os.SystemClock;

import com.gy.utils.log.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ganyu on 2016/8/6.
 *
 */
public class AudioRecordTask extends AsyncTask <Void, Integer, Void> {

    private boolean isRecording;
    private String audioStorPath;
    private OnRecordListener onRecordListener;
    private AudioRecordInfo audioRecordInfo;

    private byte[] dbCalculateBuff;
    private int dbCalculateBuffLen;

    public AudioRecordTask(String audioStorPath) {
        this.audioStorPath = audioStorPath;
    }

    public void setOnRecordListener (OnRecordListener listener) {
        onRecordListener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {

        audioRecordInfo = AudioRecordInfo.getInstance();
        if (audioRecordInfo == null) {
            if (onRecordListener != null) {
                onRecordListener.onRecordError(new Exception("can not find audio recorder"));
            }
            return null;
        }

        AudioRecord audioRecord = new AudioRecord(
                audioRecordInfo.audioSource,
                audioRecordInfo.audioRate,
                audioRecordInfo.audioRecordConfig,
                audioRecordInfo.audioFormat,
                audioRecordInfo.audioRecordMinBuffSize);

        try {
            FileOutputStream fOut = null;
            if (audioStorPath != null) {
                fOut = new FileOutputStream(new File(audioStorPath));
            }
            byte[] buff = new byte[audioRecordInfo.audioRecordMinBuffSize];
            dbCalculateBuff = new byte[audioRecordInfo.audioRecordMinBuffSize];
            int len = 0;
            audioRecord.startRecording();
            isRecording = true;
            if (onRecordListener != null) {
                onRecordListener.onRecordStart();
            }

            long lastProgressUpdateTime = System.currentTimeMillis();
            long currentTime;
            while (isRecording) {
                len = audioRecord.read(buff, 0, buff.length);
                if (len == AudioRecord.ERROR_INVALID_OPERATION || len == AudioRecord.ERROR_BAD_VALUE) {
                    if (onRecordListener != null) {
                        onRecordListener.onRecordError(new IOException("can not read from audio recorder"));
                    }
                }

                if (onRecordListener != null) {
                    onRecordListener.onRecord(buff, len);
                }

                if (fOut != null) {
                    fOut.write(buff, 0, len);
                }

                currentTime = System.currentTimeMillis();
                if (currentTime - lastProgressUpdateTime > 250) {
                    System.arraycopy(buff, 0, dbCalculateBuff, 0, len);
                    dbCalculateBuffLen = len;
                    publishProgress();
                    lastProgressUpdateTime = currentTime;
                }
            }

            if (onRecordListener != null) {
                onRecordListener.onRecordStop();
            }
            if (fOut != null) {
                fOut.close();
            }
            audioRecord.stop();
            audioRecord.release();
        } catch (Exception e) {
            if (onRecordListener != null) {
                onRecordListener.onRecordError(e);
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (onRecordListener != null) {
            onRecordListener.onRecordSound(calculateDB(dbCalculateBuff, dbCalculateBuffLen));
        }
    }

    /**
     * 计算分贝值
     */
    private int calculateDB (byte[] buff, int len) {
        // 将 buffer 内容取出，进行平方和运算
        if (buff == null || len <= 0) {
            return 0;
        }
        long voice = 0;
        for (int i = 0; i < len; i++) {
            voice += buff[i] * buff[i];
        }
        // 平方和除以数据总长度，得到音量大小。
        double mean = voice / len;
        return (int) (10 * Math.log10(mean));
    }

    public void stopRecord () {
        this.isRecording = false;
    }

    public interface OnRecordListener {
        void onRecordStart ();
        void onRecordStop ();
        void onRecordError (Exception e);
        void onRecord (byte[] buff, int len);
        void onRecordSound (int db);//参数是分贝
    }
}
