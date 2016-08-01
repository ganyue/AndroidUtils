package com.android.ganyue.frg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.ganyue.R;
import com.android.ganyue.application.MApplication;
import com.android.ganyue.controller.FuncCtrl;
import com.gy.appbase.controller.BaseFragmentActivityController;
import com.gy.appbase.fragment.BaseFragment;
import com.gy.utils.audio.AudioUtils;
import com.gy.utils.audio.Playlist;
import com.gy.utils.audio.Track;
import com.gy.utils.log.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganyu on 2016/4/30.
 * Nsd test
 */
public class MediaTestFrg extends BaseFragment {

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_media, null);
    }

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {

        view.findViewById(R.id.btn_pre).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MApplication.getAudioUtils().getPlayer(AudioUtils.AudioType.MEDIA).prev();
            }
        });
        view.findViewById(R.id.btn_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MApplication.getAudioUtils().getPlayer(AudioUtils.AudioType.MEDIA).playOrPause();
            }
        });
        view.findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MApplication.getAudioUtils().getPlayer(AudioUtils.AudioType.MEDIA).next();
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
                MApplication.getAudioUtils().getPlayer(AudioUtils.AudioType.MEDIA).seek(10000);
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
            LogUtils.d("yue.gan", "sender : " + sender + " playlist : " + playlist);
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
