package com.android.ganyue.frg;

import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.android.ganyue.controller.FuncCtrl;
import com.gy.appbase.controller.BaseFragmentActivityController;
import com.gy.widget.fragment.LoadingFragment;
import com.gy.widget.fragment.tabfrag.HTabFragment;
import com.gy.widget.fragment.tabfrag.Tab;

import java.util.ArrayList;

/**
 * Created by ganyu on 2016/8/29.
 *
 */
public class HTabFragTestFrag extends HTabFragment{

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            getView().findViewById(com.gy.widget.R.id.flyt_empty).setVisibility(View.GONE);
            mController.hideFragment(HTabFragTestFrag.this.getChildFragmentManager(), LoadingFragment.class);
            if (tabs == null) tabs = new ArrayList<>();
            Tab tab1 = new Tab("推荐啊啊啊啊", TestFrag.class, null, null);
            Tab tab2 = new Tab("主播", TestFrag.class, null, null);
            Tab tab3 = new Tab("儿歌", TestFrag.class, null, null);
            Tab tab4 = new Tab("童谣", TestFrag.class, null, null);
            Tab tab5 = new Tab("故事", TestFrag.class, null, null);
            Tab tab6 = new Tab("音乐", TestFrag.class, null, null);
            Tab tab7 = new Tab("学堂", TestFrag.class, null, null);
            Tab tab8 = new Tab("过级", TestFrag.class, null, null);
            Tab tab9 = new Tab("早教", TestFrag.class, null, null);
            tabs.add(tab1);
            tabs.add(tab2);
            tabs.add(tab3);
            tabs.add(tab4);
            tabs.add(tab5);
            tabs.add(tab6);
            tabs.add(tab7);
            tabs.add(tab8);
            tabs.add(tab9);
            updateTabs();
            updatePager();
            setSelectedTab(0);
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        getView().findViewById(com.gy.widget.R.id.flyt_empty).setVisibility(View.VISIBLE);
        mController.replaceFragment(getChildFragmentManager(),
                com.gy.widget.R.id.flyt_empty,
                LoadingFragment.class, null, null);
        handler.sendEmptyMessageDelayed(1, 2000);
    }

    @Override
    protected BaseFragmentActivityController instanceController() {
        return new FuncCtrl(mActivity);
    }
}
