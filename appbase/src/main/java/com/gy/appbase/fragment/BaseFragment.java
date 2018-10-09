package com.gy.appbase.fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gy.appbase.activity.BaseFragmentActivity;
import com.gy.appbase.activity.OnKeyDownCallback;
import com.gy.appbase.controller.BaseFragmentActivityController;
import com.gy.appbase.inject.ViewInjectInterpreter;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by ganyu on 2016/4/30.
 *
 */
public abstract class BaseFragment extends Fragment implements OnKeyDownCallback {
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
        findViews(view, savedInstanceState);
        initViews(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (FragmentActivity) context;
    }

    /** 当fragment中嵌套fragment的时候可能会出现bug：
     * java.lang.IllegalStateException: Activity has been destroyed
     * 此时要把childFragmentManager给置空* */
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (Exception e) {}
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
        ((BaseFragmentActivity)mActivity).setOnKeyDownCallback(this);
    }

    @CallSuper
    protected void findViews (View view, Bundle savedInstanceState) {
        ViewInjectInterpreter.interpret(this, view);
    }

    protected abstract View createView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
    protected abstract void initViews (View view, Bundle savedInstanceState);
    protected abstract BaseFragmentActivityController instanceController ();
    protected boolean onKeyDown (int keyCode, KeyEvent event) {return false;}
    protected boolean onBackPressed () {return false;}

    public boolean onKeyDownListener (int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) return onBackPressed();
        return onKeyDown(keyCode, event);
    }

    protected void requestPermission (String ...permissions) {
        ArrayList<String> unpermited = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                unpermited.add(permission);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
