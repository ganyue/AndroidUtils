package com.android.ganyue.frg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.ganyue.R;
import com.android.ganyue.controller.FuncCtrl;
import com.gy.appbase.controller.BaseFragmentActivityController;
import com.gy.appbase.fragment.BaseFragment;
import com.gy.appbase.inject.ViewInject;
import com.gy.utils.constants.WindowConstants;
import com.gy.widget.listview.PushLoadMoreListView;
import com.gy.widget.listview.ScrollObservedListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ganyu on 2016/10/5.
 *
 */
public class ScrollObservListFrag extends BaseFragment {

    @ViewInject(R.id.lv_content)    private PushLoadMoreListView mLvContent;
    @ViewInject(R.id.tv_test)       private TextView mTvTest;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scrollobservedlist, container, false);
    }

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        mLvContent.addOnScrollObservedListener(onScrollObservedListener);
        mLvContent.setObservedItem(3);
        List<Map<String, String>> datas = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("text", ""+i);
            datas.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(mActivity, datas, R.layout.item_single_text,
                new String[]{"text"}, new int[]{R.id.tv_item_name});
        mLvContent.setAdapter(adapter);
        mLvContent.setOnLoadMoreListener(onLoadMoreListener);
    }

    @Override
    protected BaseFragmentActivityController instanceController() {
        return new FuncCtrl(mActivity);
    }

    private PushLoadMoreListView.OnLoadMoreListener onLoadMoreListener
            = new PushLoadMoreListView.OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            //TODO
        }

        @Override
        public boolean canLoadMore() {
            return true;
        }
    };

    private ScrollObservedListView.OnScrollObservedListener onScrollObservedListener
            = new ScrollObservedListView.OnScrollObservedListener() {
        @Override
        public void onObservedItemPosChanged(ScrollObservedListView.ItemState state, int[] location, int[] size) {
            if (state == ScrollObservedListView.ItemState.OnScreen) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mTvTest.getLayoutParams();
                params.topMargin = location[1] + size[1] -
                        WindowConstants.getInstance(mActivity).getNotificationBarHeight();
                mTvTest.setLayoutParams(params);
            }
        }
    };
}
