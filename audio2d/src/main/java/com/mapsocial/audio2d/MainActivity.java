package com.mapsocial.audio2d;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gy.appbase.activity.BaseFragmentActivity;
import com.gy.appbase.controller.BaseFragmentActivityController;
import com.gy.appbase.inject.ViewInject;
import com.gy.utils.String.StringUtils;
import com.gy.utils.file.SdcardUtils;
import com.gy.utils.recorder.AudioRecordInfo;
import com.gy.utils.recorder.AudioRecordTask;
import com.gy.utils.recorder.AudioTrackTask;

import java.io.File;
import java.util.List;

public class MainActivity extends BaseFragmentActivity implements View.OnClickListener{

    @ViewInject (R.id.btn_encode)       Button btnEncode;
    @ViewInject (R.id.btn_send)         Button btnSend;
    @ViewInject (R.id.btn_record)       Button btnRecord;
    @ViewInject (R.id.btn_stop_record)  Button btnStopRecord;
    @ViewInject (R.id.btn_delete_file)  Button btnDeleteFile;
    @ViewInject (R.id.btn_decode)       Button btnDecode;
    @ViewInject (R.id.tv_log)           TextView tvLog;
    @ViewInject (R.id.sv_surface)       SurfaceView svSurface;
    @ViewInject (R.id.edt_send)         EditText edtSend;

    private String cachePath;

    @Override
    protected void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_test);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        btnEncode.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        btnRecord.setOnClickListener(this);
        btnStopRecord.setOnClickListener(this);
        btnDeleteFile.setOnClickListener(this);
        btnDecode.setOnClickListener(this);
        cachePath = SdcardUtils.getUsableCacheDir(this).getAbsolutePath() + File.separator + "test";
    }

    @Override
    protected BaseFragmentActivityController instanceController() {
        return null;
    }

    String sendStr = "";

    AudioRecordTask audioRecordTask;
    AudioTrackTask audioTrackTask;
    AudioDecodeTask decodeTask;
    AudioMultyDecodeTask multyDecodeTask;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_encode:
                sendStr = edtSend.getText().toString();
                tvLog.setText("encode bytes : "+StringUtils.toHexString(sendStr.getBytes()));
                AudioEncodeTask task = new AudioEncodeTask(sendStr, cachePath);
                task.execute();
//                AudioMultyEncodeTask task = new AudioMultyEncodeTask(sendStr, cachePath);
//                task.execute();
                break;
            case R.id.btn_send:
                audioTrackTask = new AudioTrackTask(cachePath, 44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
                audioTrackTask.execute();
                break;
            case R.id.btn_record:
                if (audioRecordTask == null) {
                    AudioRecordInfo.getInstance().changeInfo(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
                    audioRecordTask = new AudioRecordTask(cachePath);
                    audioRecordTask.execute();
                }
                break;
            case R.id.btn_stop_record:
                if (audioRecordTask != null) {
                    audioRecordTask.stopRecord();
                    audioRecordTask = null;
                }
                break;
            case R.id.btn_decode:
                decodeTask = new AudioDecodeTask(cachePath);
                decodeTask.setAudioMultyDecodeListener(audioDecodListener);
                decodeTask.execute();
//                multyDecodeTask = new AudioMultyDecodeTask(cachePath);
//                multyDecodeTask.setAudioMultyDecodeListener(audioDecodListener);
//                multyDecodeTask.execute();
                break;
            case R.id.btn_delete_file:
                File file = new File(cachePath);
                file.delete();
                break;
        }
    }

    private AudioDecodeTask.AudioDecodListener audioDecodListener = new AudioDecodeTask.AudioDecodListener() {
        @Override
        public void onDecode(Complex[] complex, int len) {
            Canvas canvas = svSurface.getHolder().lockCanvas();
            canvas.drawColor(Color.WHITE);
            Paint linePaint = new Paint();
            Paint rectPaint = new Paint();
            rectPaint.setColor(Color.GREEN);
            rectPaint.setStyle(Paint.Style.FILL);
            linePaint.setColor(Color.BLACK);
            linePaint.setTextSize(16);
            float width = svSurface.getWidth();
            float height = svSurface.getHeight();
            float wStep = width/150;
            canvas.drawLine(0, height - 50, width, height - 50, linePaint);
            int startIndex = 0;
            for (int i = startIndex; i < 150; i++) {
                if (i % 5 == 0)canvas.drawText(""+i, wStep * (i - startIndex), height - 20, linePaint);
                if (i % 5 == 0) {
                    canvas.drawLine(wStep * (i - startIndex), height - 50, wStep * (i - startIndex), height - 90, linePaint);
                } else {
                    canvas.drawLine(wStep * (i - startIndex), height - 50, wStep * (i - startIndex), height - 60, linePaint);
                }
                int val = complex[i].getIntValue();
                canvas.drawRect(wStep * (i - startIndex), height - val/20000 - 90, wStep * (i - startIndex + 1) - 6, height - 70, rectPaint);
//                canvas.drawText(""+(int)(complex[i].getIntValue()/hStep), wStep * i + 8, complex[i].getIntValue()/hStep - 50, linePaint);
            }
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvLog.setText(""+count++);
                }
            });
            svSurface.getHolder().unlockCanvasAndPost(canvas);
        }

        private int count = 0;

        @Override
        public void onDecodeFinished(List<Byte> result) {
            AudioCoder audioCoder = new AudioCoder();
            List<String> results = audioCoder.getRecognizeStrings(result);
            String receiveStr = "receive: ";
            for (String str: results) {
                receiveStr+=str + " ";
            }
            final String finalReceiveStr = receiveStr;
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvLog.setText(finalReceiveStr);
                }
            });
        }
    };
}
