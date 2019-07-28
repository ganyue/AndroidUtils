package com.gy.widget.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.gy.widget.R;

/**
 * Created by yue.gan on 2017/8/4.
 */

public class TestActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_timer_seeker);
    }
}
