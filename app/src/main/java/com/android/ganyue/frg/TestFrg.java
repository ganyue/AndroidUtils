package com.android.ganyue.frg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.ganyue.R;
import com.android.ganyue.controller.FuncCtrl;
import com.gy.appbase.controller.BaseFragmentActivityController;
import com.gy.appbase.fragment.BaseFragment;
import com.gy.utils.app.AppUtils;
import com.gy.utils.log.LogUtils;

import java.util.List;

/**
 * created by yue.gan 18-9-22
 */
public class TestFrg extends BaseFragment{

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        List permissions = AppUtils.getAllUnPermitedPermissions(getContext());
        LogUtils.d(permissions.toString());
    }

    @Override
    protected BaseFragmentActivityController instanceController() {
        return new FuncCtrl(mActivity);
    }
}
