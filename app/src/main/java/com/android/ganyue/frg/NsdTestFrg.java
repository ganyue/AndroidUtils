package com.android.ganyue.frg;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.ganyue.R;
import com.android.ganyue.controller.FuncCtrl;
import com.android.ganyue.utils.nsd.NsdTest;
import com.gy.appbase.controller.BaseFragmentActivityController;
import com.gy.appbase.fragment.BaseFragment;

/**
 * Created by ganyu on 2016/4/30.
 * Nsd test
 */
public class NsdTestFrg extends BaseFragment {

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nsd, null);
    }

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        new NsdTest(getActivity(), view.findViewById(R.id.btn_register),
                view.findViewById(R.id.btn_discovery), (TextView) view.findViewById(R.id.tv_log),
                "_NsdTest._tcp", "NsdService-"+ Build.MODEL, 18001);
    }

    @Override
    protected BaseFragmentActivityController instanceController() {
        return new FuncCtrl(mActivity);
    }
}
