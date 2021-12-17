package com.gy.utils.recorder;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by yue.gan on 2016/8/6.
 *
 */
public class AudioTrackTask extends Thread {

    private boolean isPlaying;
    private String audioPath;
    private int audioRate;
    private int audioConfig;
    private int audioFormat;
    private int minBuffSize;
    private ArrayBlockingQueue<byte[]> bufferQueue;

    public AudioTrackTask(String audioPath, int rate, int config, int format) {
        this.audioPath = audioPath;
        audioRate = rate;
        audioFormat = format;
        if (config == AudioFormat.CHANNEL_IN_MONO || config == AudioFormat.CHANNEL_OUT_MONO) {
            audioConfig = AudioFormat.CHANNEL_OUT_MONO;
        } else {
            audioConfig = AudioFormat.CHANNEL_OUT_STEREO;
        }
        minBuffSize = AudioTrack.getMinBufferSize(audioRate, audioConfig, audioFormat);
        bufferQueue = new ArrayBlockingQueue<>(32);
    }

    public void writeData (byte[] buff) {
        bufferQueue.offer(buff);
    }

    @Override
    public void run() {
        doInBackground();
    }

    protected Void doInBackground() {
        try {
            AudioTrack audioTrack = new AudioTrack(
                    AudioManager.STREAM_VOICE_CALL,
                    audioRate,
                    audioConfig,
                    audioFormat,
                    minBuffSize,
                    AudioTrack.MODE_STREAM);

            FileInputStream fIn = null;
            if (audioPath != null) {
               fIn = new FileInputStream(new File(audioPath));
            }
            byte[] buff = new byte[minBuffSize];
            int len = 0;
            audioTrack.play();
            isPlaying = true;

            while (isPlaying) {
                if (fIn != null) {
                    len = fIn.read(buff, 0, minBuffSize);
                    if (len <= 0) {
                        break;
                    }
                    audioTrack.write(buff, 0, len);
                } else {
                    byte[] tempBuffer = bufferQueue.take();
                    byte[] buffer = new byte[tempBuffer.length];
                    System.arraycopy(tempBuffer, 0, buffer, 0, tempBuffer.length);
                    audioTrack.write(buffer, 0, buffer.length);
                }
            }

            if (fIn != null) fIn.close();
            audioTrack.stop();
            audioTrack.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void stopPlay () {
        isPlaying = false;
        interrupt();
    }
}
