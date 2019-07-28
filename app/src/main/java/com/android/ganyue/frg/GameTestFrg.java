package com.android.ganyue.frg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ganyue.R;
import com.android.ganyue.controller.FuncCtrl;
import com.android.ganyue.game.TestGameAdapter;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.BaseGdxViewIniter;
import com.gy.appbase.controller.BaseFragmentActivityController;
import com.gy.appbase.fragment.BaseFragment;
import com.gy.appbase.inject.ViewInject;
import com.gy.utils.constants.WindowConstants;

/**
 * Created by yue.gan on 2016/4/30.
 * Nsd test
 */
public class GameTestFrg extends BaseFragment {

    @ViewInject (R.id.flyt_content) private FrameLayout mFlytContent;
    @ViewInject (R.id.tv_title)     private TextView mTvTitle;


    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_libgdx_test, null);
    }

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        WindowConstants.getInstance(mActivity);
        BaseGdxViewIniter baseGdxViewIniter = new BaseGdxViewIniter(mActivity);
        View gameView = baseGdxViewIniter.initializeForView(
                new TestGameAdapter(),
                new AndroidApplicationConfiguration());

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        mFlytContent.addView(gameView, params);

        mTvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mActivity, "title clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected BaseFragmentActivityController instanceController() {
        return new FuncCtrl(mActivity);
    }
}
