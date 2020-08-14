package com.android.ganyue.frg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.ganyue.R;
import com.gy.appbase.fragment.BaseFragment;
import com.gy.appbase.inject.ViewInject;
import com.gy.widget.wave.VoiceHWave;

import java.util.Random;

/**
 * created by yue.gan 18-9-22
 */
public class TestFrg extends BaseFragment{

    @ViewInject(R.id.mVVoiceWave) VoiceHWave mVVoiceHWave;
    @ViewInject(R.id.mVStart) TextView mVStart;
    @ViewInject(R.id.mVStop) TextView mVStop;
    @ViewInject(R.id.mVRandV) TextView mVRandom;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    private Random random;
    @Override
    protected void initViews(View view) {
        random = new Random(System.currentTimeMillis());
        mVStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVVoiceHWave.start();
            }
        });
        mVStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVVoiceHWave.stop();
            }
        });
        mVRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int volume = random.nextInt(20);
                mVVoiceHWave.setVolume(volume);
            }
        });


    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void activityCall(int type, Object extra) {

    }
}
