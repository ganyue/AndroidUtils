package com.gy.appbase.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gy.appbase.activity.BaseFragmentActivity;
import com.gy.appbase.activity.OnKeyDownListener;
import com.gy.appbase.controller.BaseFragmentActivityController;
import com.gy.appbase.inject.ViewInjectInterpreter;

import java.lang.reflect.Field;

/**
 * Created by yue.gan on 2016/4/30.
 *
 */
public abstract class BaseFragment extends Fragment implements OnKeyDownListener {
    protected BaseFragmentActivityController mController;
    protected FragmentActivity mActivity;

    @CallSuper
    public void setController (BaseFragmentActivityController controller) {
        this.mController = controller;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mController == null) {
            mController = instanceController();
        }
        return createView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        initViews(view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (FragmentActivity) context;
        if (mActivity instanceof BaseFragmentActivity) {
            ((BaseFragmentActivity)mActivity).addOnKeyDownListener(this);
        }
    }

    @Override
    public void onDetach() {
        try {
            /** 当fragment中嵌套fragment的时候可能会出现bug：
             * java.lang.IllegalStateException: Activity has been destroyed
             * 此时要把childFragmentManager给置空* */
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (Exception e) {}

        if (mActivity instanceof BaseFragmentActivity) {
            ((BaseFragmentActivity)mActivity).removeOnKeyDownListener(this);
        }
        mActivity = null;
        super.onDetach();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (mController == null) {
            mController = instanceController();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mController == null) {
            mController = instanceController();
        }
    }

    @CallSuper
    protected void findViews (View view) {
        ViewInjectInterpreter.interpret(this, view);
    }

    protected abstract View createView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
    protected abstract void initViews (View view);
    protected abstract BaseFragmentActivityController instanceController ();
    protected boolean onKeyDown(int keyCode, KeyEvent event) {return false;}
    protected boolean onBackPressed () {return false;}
    public boolean onKeyDownListener(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) return onBackPressed();
        return onKeyDown(keyCode, event);
    }
}
