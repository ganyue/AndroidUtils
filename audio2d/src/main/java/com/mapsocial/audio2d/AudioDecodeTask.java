package com.mapsocial.audio2d;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganyu on 2016/8/16.
 *
 */
public class AudioDecodeTask extends AsyncTask <Void, Void, Void> {

    private String cachePath;
    private List<Byte> results;

    public AudioDecodeTask(String cachePath) {
        this.cachePath = cachePath;
        results = new ArrayList<>();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            AudioCoder audioCoder = new AudioCoder();
            FileInputStream fileInputStream = new FileInputStream(new File(cachePath));
            byte[] buff = new byte[1024];
            int len = 0;
            int totalLen = 0;
//            fileInputStream.read(buff, 0, 2);
            RandomAccessFile randomAccessFile = new RandomAccessFile(cachePath, "r");
            boolean isStart = false;
            while ((len = fileInputStream.read(buff)) > 0) {
                Complex[] complices = audioCoder.decode(results, buff, len);
                if (listener != null) {
                    Thread.sleep(100);
                    listener.onDecode(complices, 512);
                }

//                if (!isStart) {
//                    randomAccessFile.
//                }
            }
            fileInputStream.close();

            if (listener != null) {
                listener.onDecodeFinished(results);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public AudioDecodListener listener;
    public void setAudioMultyDecodeListener (AudioDecodListener listener) {
        this.listener = listener;
    }
    public interface AudioDecodListener {
        void onDecode (Complex[] complex, int len);
        void onDecodeFinished (List<Byte> result);
    }
}
