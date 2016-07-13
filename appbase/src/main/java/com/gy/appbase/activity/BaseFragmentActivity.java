package com.gy.appbase.activity;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.gy.appbase.controller.BaseFragmentActivityController;
import com.gy.appbase.inject.ViewInject;
import com.gy.appbase.inject.ViewInjectInterpreter;

import java.lang.reflect.Field;

/**
 * Created by ganyu on 2016/5/19.
 *
 */
public abstract class BaseFragmentActivity extends FragmentActivity {
    private BaseFragmentActivityController mController;
    public void setController (BaseFragmentActivityController controller) {
        mController = controller;
    }

    public BaseFragmentActivityController getController () {
        return mController;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContent(savedInstanceState);
        findViews(savedInstanceState);
        initViews(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mController == null) {
            instanceController();
        }
    }

    @CallSuper
    protected void findViews (Bundle savedInstanceState) {
        ViewInjectInterpreter.interpret(this);
    }

    protected abstract void setContent (Bundle savedInstanceState);
    protected abstract void initViews (Bundle savedInstanceState);
    protected abstract BaseFragmentActivityController instanceController ();
}
