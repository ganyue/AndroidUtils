package com.beva.txplayer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener{

    private Button mBtnRenderMode;
    private Button mBtnCodec;
    private Button mBtnPrev;
    private Button mBtnPlayOrPause;
    private Button mBtnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
            WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mBtnRenderMode = (Button) findViewById(R.id.btn_renderMode);
        mBtnCodec = (Button) findViewById(R.id.btn_codecType);
        mBtnPrev = (Button) findViewById(R.id.btn_prev);
        mBtnPlayOrPause = (Button) findViewById(R.id.btn_playOrPause);
        mBtnNext = (Button) findViewById(R.id.btn_next);

        mBtnRenderMode.setOnClickListener(this);
        mBtnCodec.setOnClickListener(this);
        mBtnPrev.setOnClickListener(this);
        mBtnPlayOrPause.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_renderMode:
                break;
            case R.id.btn_codecType:
                break;
            case R.id.btn_prev:
                break;
            case R.id.btn_next:
                break;
            case R.id.btn_playOrPause:
                break;
        }
    }
}
