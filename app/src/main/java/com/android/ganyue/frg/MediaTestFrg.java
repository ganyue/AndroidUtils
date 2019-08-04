package com.android.ganyue.frg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.ganyue.R;
import com.android.ganyue.application.MApplication;
import com.gy.appbase.fragment.BaseFragment;
import com.gy.appbase.inject.ViewInject;
import com.gy.utils.audio.mediaplayer.MediaPlayerUtils;
import com.gy.utils.audio.mediaplayer.MediaStatus;
import com.gy.utils.audio.mediaplayer.OnMediaListener;
import com.gy.utils.log.LogUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by yue.gan on 2016/4/30.
 * Nsd test
 */
public class MediaTestFrg extends BaseFragment {

    @ViewInject (R.id.tv_log)
    TextView tvLog;
    @ViewInject (R.id.btn_clearLog)
    View clearLog;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_media, null);
    }

    private MediaPlayerUtils mediaPlayerUtils;


    private List<String> musicUrls = new ArrayList<>();
    @Override
    protected void initViews(View view) {
        clearLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvLog.setText("");
            }
        });
//        testMediaPlayer(view);
        testMpdPlayer(view);

        musicUrls.add("http://zzya.beva.cn/dq/lhMwW0mGEa20NST8HCWPGMl9HS3r.mp3");
        musicUrls.add("http://zzya.beva.cn/dq/lkQpVHJtFpZIQOP87xj85c-EW-76.mp3");
        musicUrls.add("http://zzya.beva.cn/dq/lu6QCotRNQvAdoPeNV3FUb1MVa8Z.mp3");
        musicUrls.add("http://zzya.beva.cn/dq/lo0gttnKbiObx39gquvOk3ES-BiI.mp3");
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void activityCall(int type, Object extra) {

    }

    public void testMpdPlayer (View view) {
        view.findViewById(R.id.btn_pre).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        view.findViewById(R.id.btn_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> songs = new ArrayList<String>();
                songs.add("A2280.mp3");
                songs.add("A2369.mp3");
                songs.add("1607.mp3");
                songs.add("A1607.mp3");
            }
        });
        view.findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> indexes = new ArrayList<Integer>();
                indexes.add(3);
                indexes.add(1);
                indexes.add(2);
                indexes.add(0);
            }
        });
        view.findViewById(R.id.btn_init).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        view.findViewById(R.id.btn_seek).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    public void testMediaPlayer (View view) {
        mediaPlayerUtils = MApplication.getMediaPlayerUtils();
        mediaPlayerUtils.addOnMediaListener(onMediaListener);
        view.findViewById(R.id.btn_pre).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayerUtils.seek(0);
            }
        });
        view.findViewById(R.id.btn_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayerUtils.playOrPause();
            }
        });
        view.findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayerUtils.seek(30000);
            }
        });
        view.findViewById(R.id.btn_init).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mediaPlayerUtils.play("http://zzya.beva.cn/dq/lm9-E1JJDs5RrAKIclfIyJFZ9fQ0.mp3");
//                List<Track> tracks = new ArrayList<Track>();
//                Track track1 = new Track();
//                track1.mp3Url = "http://zzya.beva.cn/dq/lm9-E1JJDs5RrAKIclfIyJFZ9fQ0.mp3";
//                track1.mp3Url = "http://live.beva.cn/app/stream.mp3";
//                tracks.add(track1);
//                MApplication.getAudioUtils().getPlayer(AudioUtils.AudioType.MEDIA).initPlaylist(new Playlist(tracks));
            }
        });
        view.findViewById(R.id.btn_seek).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayerUtils.seek(10000);
            }
        });
    }

    private OnMediaListener onMediaListener = new OnMediaListener() {
        @Override
        public void onPlay(final MediaStatus mediaStatus) {
            LogUtils.d("yue.gan", "onPlay : " + mediaStatus.isPlaying);
        }

        @Override
        public void onPause(MediaStatus mediaStatus) {
            LogUtils.d("yue.gan", "onPause : " + mediaStatus.isPlaying);
        }

        @Override
        public void onStop(MediaStatus mediaStatus) {
            LogUtils.d("yue.gan", "onStop : " + mediaStatus.isPlaying);
        }

        @Override
        public void onSeek(MediaStatus mediaStatus) {
            LogUtils.d("yue.gan", "onSeek : " + mediaStatus.isPlaying + " " + mediaStatus.currentTime);
        }

        @Override
        public void onCompelete(MediaStatus mediaStatus) {
            LogUtils.d("yue.gan", "onCompelete : " + mediaStatus.sourcePath);
        }

        @Override
        public void onError(MediaStatus mediaStatus, String errorMsg) {
            LogUtils.d("yue.gan", "onError : " + errorMsg);
        }

        @Override
        public void onPreparing(MediaStatus mediaStatus) {
            LogUtils.d("yue.gan", "onPreparing");
        }
    };

    @Override
    public void onPause() {
        super.onPause();
    }
}
