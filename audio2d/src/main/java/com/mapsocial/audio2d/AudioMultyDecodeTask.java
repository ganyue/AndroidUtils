package com.mapsocial.audio2d;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganyu on 2016/8/16.
 *
 */
public class AudioMultyDecodeTask extends AsyncTask <Void, Void, Void> {

    private String cachePath;
    private List<Integer> results;
    public AudioMultyDecodListener listener;

    public AudioMultyDecodeTask(String cachePath) {
        this.cachePath = cachePath;
        results = new ArrayList<>();
    }

    public void setAudioMultyDecodeListener (AudioMultyDecodListener listener) {
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            FileInputStream fileInputStream = new FileInputStream(new File(cachePath));
            byte[] buff = new byte[1024];
            int len = 0;
//            AudioCoder.reset();
//            while ((len = fileInputStream.read(buff)) > 0) {
//                Complex[] complices = AudioCoder.multyDecode(results, buff, len);
//                if (listener != null) {
//                    listener.onDecode(complices, 512);
//                    Thread.sleep(1000);
//                }
//            }
            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface AudioMultyDecodListener {
        void onDecode (Complex[] complex, int len);
    }
}
