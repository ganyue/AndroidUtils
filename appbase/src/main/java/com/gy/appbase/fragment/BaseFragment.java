package com.gy.appbase.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gy.appbase.activity.BaseFragmentActivity;
import com.gy.appbase.activity.IBaseFragmentActivity;
import com.gy.appbase.activity.OnKeyListener;
import com.gy.appbase.inject.ViewInjectInterpreter;

import java.lang.reflect.Field;

/**
 * Created by yue.gan on 2016/4/30.
 *
 */
public abstract class BaseFragment extends Fragment implements OnKeyListener, View.OnClickListener, IBaseFragment {
    protected FragmentActivity mActivity;
    protected IBaseFragmentActivity mIActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return createView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
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
            mIActivity = (IBaseFragmentActivity) mActivity;
        }
    }

    @Override
    public void onDetach() {
        try {
            /* 当fragment中嵌套fragment的时候可能会出现bug：
             * java.lang.IllegalStateException: Activity has been destroyed
             * 此时要把childFragmentManager给置空* */
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (Exception e) {e.printStackTrace();}

        if (mActivity instanceof BaseFragmentActivity) {
            ((BaseFragmentActivity)mActivity).removeOnKeyDownListener(this);
        }
        mActivity = null;
        mIActivity = null;
        super.onDetach();
    }

    @CallSuper
    protected void findViews (View view) {
        ViewInjectInterpreter.interpret(this, view);
    }

    protected abstract View createView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
    protected abstract void initViews (View view);
    public abstract void onClick(View v);
    public abstract void activityCall(int type, Object extra);

    protected boolean handlKeyDown(int keyCode, KeyEvent event) { return false; }
    protected boolean handlKeyUp(int keyCode, KeyEvent event) { return false; }
    protected boolean consumeBackKeyEvent() {return false;}
    protected void onBackPressed () {}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!consumeBackKeyEvent()) {
                return false;
            }
            event.startTracking();
            return true;
        }

        return handlKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!consumeBackKeyEvent() || !event.isTracking() || event.isCanceled()) return false;
            onBackPressed();
            return true;
        }

        return handlKeyUp(keyCode, event);
    }

}
