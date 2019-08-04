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
import android.widget.TextView;

import com.android.ganyue.R;
import com.gy.appbase.fragment.BaseFragment;
import com.gy.appbase.inject.ViewInject;
import com.gy.widget.indicator.HorizontalIndicator;
import com.gy.widget.shapview.Circle;

/**
 * Created by yue.gan on 2016/4/30.
 * head view & list with a head
 */
public class HorizontalIndicatorFrg extends BaseFragment {

    @ViewInject(R.id.vp_pager)                  private ViewPager mViewPager;
    @ViewInject(R.id.hi_indicator)              private HorizontalIndicator mIndicator;
    @ViewInject(R.id.hi_circle_indicator)      private HorizontalIndicator mCircleIndicator;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_h_indicator, null);
    }

    @Override
    protected void initViews(View view) {
        mViewPager.setAdapter(new MPagerAdapter());
        mViewPager.addOnPageChangeListener(onPageChangeListener);

        initCircleIndicator();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void activityCall(int type, Object extra) {

    }

    private void initCircleIndicator () {
        HorizontalIndicator.LayoutParams params = new HorizontalIndicator.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 9, getResources().getDisplayMetrics()));
        int count = mViewPager.getAdapter().getCount();
        for (int i = 0; i < count; i++) {
            Circle circle = new Circle(getActivity());
            circle.setLayoutParams(params);
            circle.setColor(Color.BLACK);
            mCircleIndicator.addView(circle);
        }

        Circle circle = new Circle(getActivity());
        circle.setLayoutParams(params);
        circle.setFillContent(true);
        circle.setColor(Color.BLACK);
        mCircleIndicator.addView(circle);
    }


    /**
     * listener for ViewPager
     */
    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            mIndicator.scrollToPosition(position, positionOffset);
            mCircleIndicator.scrollToPosition(position, positionOffset);
        }

        @Override
        public void onPageSelected(int position) {
            mIndicator.setCurrentIndicatorIndex(position);
            mCircleIndicator.setCurrentIndicatorIndex(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
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
            return 3;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            if (mViews.get(position) == null) {
                TextView textView = new TextView(container.getContext());
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(Color.BLACK);
                textView.setText("page : "+position);
                mViews.put(position, textView);
            }

            container.addView(mViews.get(position));
            return mViews.get(position);
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


}
