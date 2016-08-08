package com.gy.utils.recorder;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.AsyncTask;

import com.gy.utils.log.LogUtils;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by ganyu on 2016/8/6.
 *
 */
public class AudioTrackTask extends AsyncTask<Void, Void, Void> {

    private boolean isPlaying;
    private String audioPath;
    private int audioRate;
    private int audioConfig;
    private int audioFormat;
    private int minBuffSize;

    public AudioTrackTask(String audioPath, int rate, int config, int format) {
        this.audioPath = audioPath;
        audioRate = rate;
        audioFormat = format;
        minBuffSize = AudioTrack.getMinBufferSize(rate, config, format);

        if (config == AudioFormat.CHANNEL_IN_MONO) {
            audioConfig = AudioFormat.CHANNEL_OUT_MONO;
        } else {
            audioConfig = AudioFormat.CHANNEL_OUT_STEREO;
        }
    }

    @Override
    protected Void doInBackground(Void... params) {

        AudioTrack audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                audioRate,
                audioConfig,
                audioFormat,
                minBuffSize,
                AudioTrack.MODE_STREAM);

        try {
            FileInputStream fIn = new FileInputStream(new File(audioPath));
            byte[] buff = new byte[minBuffSize];
            int len = 0;
            audioTrack.play();
            isPlaying = true;

            byte[] tempbuff = new byte[2 * minBuffSize];
            while (isPlaying) {
                len = fIn.read(buff, 0, minBuffSize);
                if (len <= 0) {
                    LogUtils.d("yue.gan", "end of pcm file");
                    break;
                }
//                int index = 0;
//                for (int i = 0; i < len; i++) {
//                    if (i % 2 == 0 && i != 0) {
//                        tempbuff[index] = tempbuff[index - 2];
//                        index++;
//                        tempbuff[index] = tempbuff[index - 2];
//                        index ++;
//                    }
//                    tempbuff[index] = buff[i];
//                    index++;
//                }
                audioTrack.write(buff, 0, len);
            }

            LogUtils.d("yue.gan", "stop play");
            fIn.close();
            audioTrack.stop();
            audioTrack.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void stopPlay () {
        isPlaying = false;
    }
}
