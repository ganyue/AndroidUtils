package com.beva.txplayer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.io.File;

public class MainActivity extends Activity implements View.OnClickListener{

    private String[] videoUrls = {
            "http://192.168.101.19:8080/Test/res/video/Sword Art Online 2 01.mp4",
            "http://192.168.101.19:8080/Test/res/video/Sword Art Online 2 02.mp4",
            "http://192.168.101.19:8080/Test/res/video/Sword Art Online 2 03.mp4",
            "http://192.168.101.19:8080/Test/res/video/Sword Art Online 2 04.mp4",
            "http://192.168.101.19:8080/Test/res/video/Sword Art Online 2 05.mp4",
            "http://192.168.101.19:8080/Test/res/video/Sword Art Online 2 06.mp4",
            "http://192.168.101.19:8080/Test/res/video/Sword Art Online 2 07.mp4",
            "http://192.168.101.19:8080/Test/res/video/Sword Art Online 2 08.mp4",
            "http://192.168.101.19:8080/Test/res/video/Sword Art Online 2 09.mp4",
            "http://192.168.101.19:8080/Test/res/video/Sword Art Online 2 10.mp4",
            "http://192.168.101.19:8080/Test/res/video/Sword Art Online 2 11.mp4",
            "http://192.168.101.19:8080/Test/res/video/Sword Art Online 2 12.mp4",
            "http://192.168.101.19:8080/Test/res/video/Sword Art Online 2 13.mp4",
            "http://192.168.101.19:8080/Test/res/video/Sword Art Online 2 14.5.mp4",
            "http://192.168.101.19:8080/Test/res/video/Sword Art Online 2 14.mp4",
            "http://192.168.101.19:8080/Test/res/video/Sword Art Online 2 15.mp4",
            "http://192.168.101.19:8080/Test/res/video/Sword Art Online 2 16.mp4",
            "http://192.168.101.19:8080/Test/res/video/Sword Art Online 2 17.mp4",
            "http://192.168.101.19:8080/Test/res/video/Sword Art Online 2 18.mp4",
            "http://192.168.101.19:8080/Test/res/video/Sword Art Online 2 19.mp4",
            "http://192.168.101.19:8080/Test/res/video/Sword Art Online 2 20.mp4",
            "http://192.168.101.19:8080/Test/res/video/Sword Art Online 2 21.mp4",
            "http://192.168.101.19:8080/Test/res/video/Sword Art Online 2 22.mp4",
            "http://192.168.101.19:8080/Test/res/video/Sword Art Online 2 23.mp4",
            "http://192.168.101.19:8080/Test/res/video/Sword Art Online 2 24.mp4",
    };

    private TXCloudVideoView mVideoView;
    private TXLivePlayer mPlayer;

    private Button mBtnRenderMode;
    private Button mBtnCodec;
    private Button mBtnPrev;
    private Button mBtnPlayOrPause;
    private Button mBtnNext;

    private int currentIndex = 0;
    private boolean isPlayerInited = false;
    private int playType = TXLivePlayer.PLAY_TYPE_VOD_MP4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
            WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mVideoView = (TXCloudVideoView) findViewById(R.id.txv_videoView);
        mPlayer = new TXLivePlayer(this);
        mPlayer.setPlayerView(mVideoView);
        mPlayer.enableHardwareDecode(false);
        mPlayer.setPlayListener(itxLivePlayListener);

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

    private ITXLivePlayListener itxLivePlayListener = new ITXLivePlayListener() {
        @Override
        public void onPlayEvent(int i, Bundle bundle) {
            Log.d("yue.gan", "onPlayEvent : " + i + "  " + bundle.toString());
        }

        @Override
        public void onNetStatus(Bundle bundle) {
//            Log.d("yue.gan", "onNetStatus : ");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.stopPlay(true);
        mVideoView.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_renderMode:
                if ("适应".equals(mBtnRenderMode.getText())) {
                    mPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
                    mBtnRenderMode.setText("铺满");
                } else {
                    mPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
                    mBtnRenderMode.setText("适应");
                }
//                File file = new File("/sdcard/temp");
//                File[] files = file.listFiles();
//                int len = files.length;
                break;
            case R.id.btn_codecType:
                if ("软解".equals(mBtnCodec.getText())) {
                    mPlayer.stopPlay(true);
                    mPlayer.enableHardwareDecode(true);
                    mPlayer.startPlay(videoUrls[currentIndex], playType);
                    mBtnCodec.setText("硬解");
                } else {
                    mPlayer.stopPlay(true);
                    mPlayer.enableHardwareDecode(false);
                    mPlayer.startPlay(videoUrls[currentIndex], playType);
                    mBtnCodec.setText("软解");
                }
                break;
            case R.id.btn_prev:
                currentIndex = (currentIndex - 1) % videoUrls.length;
                if (currentIndex < 0) currentIndex = videoUrls.length - 1;
                mPlayer.startPlay(videoUrls[currentIndex], playType);
                break;
            case R.id.btn_next:
                currentIndex = (currentIndex + 1) % videoUrls.length;
                mPlayer.startPlay(videoUrls[currentIndex], playType);
                break;
            case R.id.btn_playOrPause:
                if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                    mBtnPlayOrPause.setText("播放");
                } else if (isPlayerInited) {
                    mPlayer.resume();
                    mBtnPlayOrPause.setText("暂停");
                } else {
                    mPlayer.startPlay(videoUrls[currentIndex], playType);
                    isPlayerInited = true;
                    mBtnPlayOrPause.setText("暂停");
                }
                break;
        }
    }
}
