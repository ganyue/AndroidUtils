package com.mapsocial.audio2d;

import android.os.AsyncTask;

import com.gy.utils.log.LogUtils;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by ganyu on 2016/8/16.
 *
 */
public class AudioEncodeTask extends AsyncTask <Void, Void, Void> {

    private String msg;
    private String cachePath;

    public AudioEncodeTask(String msg, String cachePath) {
        this.cachePath = cachePath;
        this.msg = msg;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        byte[] bMsg = msg.getBytes();

        try {
            AudioCoder audioCoder = new AudioCoder();
            FileOutputStream fileOutputStream = new FileOutputStream(new File(cachePath));
            fileOutputStream.write(audioCoder.encode(Audio2DConsts.Coodbook.BStart));
            for (byte abyte : bMsg) {
                byte low4 = (byte) (abyte & 0x0f);
                byte up4 = (byte) ((abyte >> 4) & 0x0f);
                fileOutputStream.write(audioCoder.encode(Audio2DConsts.Coodbook.BDivide));
                writeCode(low4, fileOutputStream, audioCoder);
                fileOutputStream.write(audioCoder.encode(Audio2DConsts.Coodbook.BDivide));
                writeCode(up4, fileOutputStream, audioCoder);
            }
            fileOutputStream.write(audioCoder.encode(Audio2DConsts.Coodbook.BDivide));
            fileOutputStream.write(audioCoder.encode(Audio2DConsts.Coodbook.BEnd));
            for (int i = 0; i < 10; i++) {
                fileOutputStream.write(audioCoder.encode(Audio2DConsts.Coodbook.BEnd));
            }
            LogUtils.d("yue.gan", "encode ok");
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void writeCode (byte msg, FileOutputStream fileOutputStream, AudioCoder audioCoder) {
        try {
            switch (msg) {
                case 0:
                    fileOutputStream.write(audioCoder.encode(Audio2DConsts.Coodbook.B0));
                    break;
                case 1:
                    fileOutputStream.write(audioCoder.encode(Audio2DConsts.Coodbook.B1));
                    break;
                case 2:
                    fileOutputStream.write(audioCoder.encode(Audio2DConsts.Coodbook.B2));
                    break;
                case 3:
                    fileOutputStream.write(audioCoder.encode(Audio2DConsts.Coodbook.B3));
                    break;
                case 4:
                    fileOutputStream.write(audioCoder.encode(Audio2DConsts.Coodbook.B4));
                    break;
                case 5:
                    fileOutputStream.write(audioCoder.encode(Audio2DConsts.Coodbook.B5));
                    break;
                case 6:
                    fileOutputStream.write(audioCoder.encode(Audio2DConsts.Coodbook.B6));
                    break;
                case 7:
                    fileOutputStream.write(audioCoder.encode(Audio2DConsts.Coodbook.B7));
                    break;
                case 8:
                    fileOutputStream.write(audioCoder.encode(Audio2DConsts.Coodbook.B8));
                    break;
                case 9:
                    fileOutputStream.write(audioCoder.encode(Audio2DConsts.Coodbook.B9));
                    break;
                case 10:
                    fileOutputStream.write(audioCoder.encode(Audio2DConsts.Coodbook.Ba));
                    break;
                case 11:
                    fileOutputStream.write(audioCoder.encode(Audio2DConsts.Coodbook.Bb));
                    break;
                case 12:
                    fileOutputStream.write(audioCoder.encode(Audio2DConsts.Coodbook.Bc));
                    break;
                case 13:
                    fileOutputStream.write(audioCoder.encode(Audio2DConsts.Coodbook.Bd));
                    break;
                case 14:
                    fileOutputStream.write(audioCoder.encode(Audio2DConsts.Coodbook.Be));
                    break;
                case 15:
                    fileOutputStream.write(audioCoder.encode(Audio2DConsts.Coodbook.Bf));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
