package com.gy.libgdx.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.BaseGdxViewIniter;

/**
 * Created by ganyu on 2016/7/29.
 *
 */
public class GameActivity extends AndroidApplication{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize(new GameAdapter(), new AndroidApplicationConfiguration());
    }
}
