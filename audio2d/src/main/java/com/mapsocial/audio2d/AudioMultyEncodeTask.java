package com.mapsocial.audio2d;

import android.os.AsyncTask;

import com.gy.utils.log.LogUtils;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by ganyu on 2016/8/16.
 *
 */
public class AudioMultyEncodeTask extends AsyncTask <Void, Void, Void> {

    private String msg;
    private String cachePath;

    public AudioMultyEncodeTask(String msg, String cachePath) {
        this.cachePath = cachePath;
        this.msg = msg;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        byte[] bMsg = msg.getBytes();

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(cachePath));
//            fileOutputStream.write(AudioCoder.encode(Audio2DConsts.Coodbook.F_Start));
//            int[] freqs = new int[9];
//            freqs[8] = 0;
//            for (byte abyte : bMsg) {
//                fileOutputStream.write(AudioCoder.encode(Audio2DConsts.Coodbook.F_Divider));
//                for (int n = 0; n < 8; n++) {
//                    if (((abyte >> n) & 0x01) == 1) {
//                        freqs[n] = Audio2DConsts.Coodbook.F_Start[n];
//                    } else {
//                        freqs[n] = 0;
//                    }
//                }
//                fileOutputStream.write(AudioCoder.encode(freqs));
//            }
//            fileOutputStream.write(AudioCoder.encode(Audio2DConsts.Coodbook.F_Divider));
//            fileOutputStream.write(AudioCoder.encode(Audio2DConsts.Coodbook.F_Divider));
//            fileOutputStream.write(AudioCoder.encode(Audio2DConsts.Coodbook.F_End));
//            fileOutputStream.write(AudioCoder.encode(Audio2DConsts.Coodbook.F_End));
//            fileOutputStream.write(AudioCoder.encode(Audio2DConsts.Coodbook.F_End));
//
//            LogUtils.d("yue.gan", "encode ok");

            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
