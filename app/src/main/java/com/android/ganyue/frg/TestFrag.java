package com.android.ganyue.frg;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.ganyue.R;
import com.gy.appbase.controller.BaseFragmentActivityController;
import com.gy.appbase.fragment.BaseFragment;
import com.gy.appbase.inject.ViewInject;
import com.gy.utils.log.LogUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganyu on 2016/8/29.
 *
 */
public class TestFrag extends BaseFragment {

    List<Fragment> fragment;
    @ViewInject(R.id.pb_progress)  private ProgressBar pbProgress;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_testfrag, container, false);
    }

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        pbProgress.setProgress(20);
    }

    @Override
    protected BaseFragmentActivityController instanceController() {
        return null;
    }
}
