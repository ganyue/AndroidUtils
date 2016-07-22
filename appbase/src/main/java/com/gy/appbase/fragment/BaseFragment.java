package com.gy.appbase.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gy.appbase.controller.BaseFragmentActivityController;
import com.gy.appbase.inject.ViewInject;
import com.gy.appbase.inject.ViewInjectInterpreter;

import java.lang.reflect.Field;

/**
 * Created by ganyu on 2016/4/30.
 *
 */
public abstract class BaseFragment extends Fragment {
    protected BaseFragmentActivityController mController;
    protected FragmentActivity mActivity;
    public void setController (BaseFragmentActivityController controller) {
        this.mController = controller;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mController == null) {
            instanceController();
        }
        return createView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view, savedInstanceState);
        initViews(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (FragmentActivity) context;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (mController == null) {
            instanceController();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mController == null) {
            instanceController();
        }
    }

    @CallSuper
    protected void findViews (View view, Bundle savedInstanceState) {
        ViewInjectInterpreter.interpret(this, view);
    }

    protected abstract View createView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
//    protected abstract void findViews (View view, Bundle savedInstanceState);
    protected abstract void initViews (View view, Bundle savedInstanceState);
    protected abstract BaseFragmentActivityController instanceController ();
}
