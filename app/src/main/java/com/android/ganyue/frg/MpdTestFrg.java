package com.android.ganyue.frg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.ganyue.R;
import com.android.ganyue.application.MApplication;
import com.android.ganyue.controller.FuncCtrl;
import com.gy.appbase.controller.BaseFragmentActivityController;
import com.gy.appbase.fragment.BaseFragment;
import com.gy.appbase.inject.ViewInject;
import com.gy.utils.audio.AudioUtils;
import com.gy.utils.audio.Playlist;
import com.gy.utils.audio.Track;
import com.gy.utils.audio.mpd.MpdPlayer;
import com.gy.utils.log.LogUtils;
import com.gy.utils.tcp.TcpClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganyu on 2016/4/30.
 * Nsd test
 */
public class MpdTestFrg extends BaseFragment {

    @ViewInject (R.id.tv_log)           TextView tvLog;
    @ViewInject (R.id.btn_clearLog)     Button btnClearLog;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_media, null);
    }

    private MpdPlayer mpdPlayer;

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {

        view.findViewById(R.id.btn_pre).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mpdPlayer = new MpdPlayer("192.168.1.142", mActivity, tvLog);
                mpdPlayer = new MpdPlayer("192.168.0.156", mActivity, tvLog);
            }
        });
        view.findViewById(R.id.btn_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpdPlayer.testCmd();
            }
        });
        view.findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        view.findViewById(R.id.btn_init).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Track> tracks = new ArrayList<Track>();
                Track track1 = new Track(); track1.mp3Url = "http://zzya.beva.cn/dq/lm9-E1JJDs5RrAKIclfIyJFZ9fQ0.mp3";
                tracks.add(track1);
                MApplication.getAudioUtils().getPlayer(AudioUtils.AudioType.MEDIA).initPlaylist(new Playlist(tracks));
            }
        });
        view.findViewById(R.id.btn_seek).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        btnClearLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvLog.setText("");
            }
        });

        MApplication.getAudioUtils().addOnAudioListener(onAudioListener);
    }

    private AudioUtils.OnAudioListener onAudioListener = new AudioUtils.OnAudioListener() {
        @Override
        public void onStateChanged(String sender, Playlist playlist, int operation, int position, boolean isPlaying) {
            LogUtils.d("yue.gan", "sender : " + sender + " operation : " + operation + " position : " + position + " isplaying : " + isPlaying);
        }

        @Override
        public void onComplete(String sender, Playlist playlist) {
            LogUtils.d("yue.gan", "sender : " + sender + " playlistVersion : " + playlist);
        }

        @Override
        public void onError(String sender, int extra) {
            LogUtils.d("yue.gan", "sender : " + sender + " extra : " + extra);
        }
    };

    @Override
    protected BaseFragmentActivityController instanceController() {
        return new FuncCtrl(mActivity);
    }
}
