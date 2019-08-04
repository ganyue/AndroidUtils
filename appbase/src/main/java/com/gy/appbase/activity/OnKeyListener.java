package com.gy.appbase.activity;

import android.view.KeyEvent;

/**
 * Created by yue.gan on 2016/11/4.
 *
 */
public interface OnKeyListener {
    boolean onKeyDown(int keyCode, KeyEvent event);
    boolean onKeyUp(int keyCode, KeyEvent event);
}
