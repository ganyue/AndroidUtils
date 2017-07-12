package com.android.ganyue.frg;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.ganyue.R;
import com.android.ganyue.application.MApplication;
import com.gy.appbase.controller.BaseFragmentActivityController;
import com.gy.appbase.fragment.BaseFragment;
import com.gy.appbase.inject.ViewInject;
import com.gy.utils.constants.DeviceConstants;
import com.gy.utils.log.LogUtils;
import com.gy.utils.phone.PhoneUtils;

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
    @ViewInject(R.id.tv_test)      private TextView tvTest;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.d("yue.gan", "mac : " + DeviceConstants.getUniqueCode(mActivity));
        return inflater.inflate(R.layout.fragment_testfrag, container, false);

    }

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        pbProgress.setProgress(20);

        tvTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "market://details?id=com.beva.bevatingting";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(str));
                startActivity(intent);
            }
        });
    }

    @Override
    protected BaseFragmentActivityController instanceController() {
        return null;
    }
}
