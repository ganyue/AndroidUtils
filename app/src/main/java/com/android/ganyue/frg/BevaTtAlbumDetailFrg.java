package com.android.ganyue.frg;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.ganyue.R;
import com.android.ganyue.controller.FuncCtrl;
import com.gy.appbase.controller.BaseFragmentActivityController;
import com.gy.appbase.fragment.BaseFragment;
import com.gy.appbase.inject.ViewInject;
import com.gy.utils.constants.WindowConstants;
import com.gy.widget.listview.ItemObservedListView;

/**
 * Created by ganyu on 2016/4/30.
 * head view & list with a head
 */
public class BevaTtAlbumDetailFrg extends BaseFragment {

    @ViewInject(R.id.vp_pager)              private ViewPager mViewPager;
    @ViewInject(R.id.rlyt_title)            private RelativeLayout mRlytTitle;
    @ViewInject(R.id.tv_test)               private TextView mTvTest;
    private int minIndicatorY = 0;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bevatt_detailfrg, null);
    }

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        mViewPager.setAdapter(new MPagerAdapter());
        mViewPager.addOnPageChangeListener(onPageChangeListener);
        minIndicatorY = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48,
                getResources().getDisplayMetrics()) +
                WindowConstants.getInstance(mActivity).getNotificationBarHeight();
    }

    @Override
    protected BaseFragmentActivityController instanceController() {
        return new FuncCtrl(mActivity);
    }

    /**
     * listener for ViewPager
     */
    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    private ItemObservedListView.OnItemObservedListener onItemObservedListener = new ItemObservedListView.OnItemObservedListener() {
        @Override
        public void onItemScrolled(int position, int state, int[] location, int dy) {
            if (dy != 0) {
                mTvTest.offsetTopAndBottom(dy);
            }
        }
    };

    private AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem <= 1 && view.getLastVisiblePosition() >= 1) {
                int[] childLoc = new int[2];
                int[] indicatorLoc = new int[2];
                View childV = view.getChildAt(1 - firstVisibleItem);
                childV.getLocationOnScreen(childLoc);
                mTvTest.getLocationOnScreen(indicatorLoc);
                int dy = childLoc[1] - indicatorLoc[1];
                mTvTest.offsetTopAndBottom(dy);
                ((MPagerAdapter)mViewPager.getAdapter()).getItem(1).offsetTopAndBottom(dy);
            }
        }
    };

    /**
     * a simple adapter for ViewPager
     */
    class MPagerAdapter extends PagerAdapter {

        private SparseArray<View> mViews;

        public MPagerAdapter () {
            mViews = new SparseArray<>();
        }

        @Override
        public int getCount() {
            return 2;
        }

        public View getItem (int position) {
            View view = mViews.get(position);
            if (view == null) {
                if (position == 0) {
                    ListView listView = new ListView(getActivity());
                    listView.setAdapter(new MListAdapter());
                    listView.setOnScrollListener(onScrollListener);
                    view = listView;
                    View headerV = new View(mActivity);
                    AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics()));
                    headerV.setLayoutParams(params);
                    listView.addHeaderView(headerV);
                    mViews.put(position, listView);
                } else {
                    TextView textView = new TextView(mActivity);
                    textView.setGravity(Gravity.CENTER);
                    textView.setTextColor(Color.BLACK);
                    textView.setText("page : "+position);
                    view = textView;
                    mViews.put(position, textView);
                }
            }

            return view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(getItem(position));
            return getItem(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViews.get(position));
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }


    class MListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 100;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                convertView = inflater.inflate(R.layout.item_single_text, parent, false);
                holder = new ViewHolder();
                holder.textView = (TextView) convertView.findViewById(R.id.tv_item_name);
                convertView.setTag(holder);
                holder.textView.setTextColor(Color.BLACK);
            }

            holder = (ViewHolder) convertView.getTag();
            holder.textView.setText("position : "+position);
            return convertView;
        }

        class ViewHolder {
            TextView textView;
        }
    }
}
