package com.android.ganyue.frg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.ganyue.R;
import com.gy.appbase.controller.BaseFragmentActivityController;
import com.gy.appbase.fragment.BaseFragment;

/**
 * Created by ganyu on 2016/8/29.
 *
 */
public class TestFrag extends BaseFragment {

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_testfrag, container, false);
    }

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
    }

    @Override
    protected BaseFragmentActivityController instanceController() {
        return null;
    }
}
