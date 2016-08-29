package com.gy.widget.fragment.tabfrag;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gy.appbase.fragment.BaseFragment;
import com.gy.widget.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ganyu on 2016/8/29.
 *
 */
public abstract class HTabFragment extends BaseFragment{

    protected HorizontalScrollView horizontalScrollView;
    protected LinearLayout tabContainer;
    protected ViewPager viewPager;
    protected FragPageAdapter pageAdapter;

    protected List<Tab> tabs;
    private int selectedTextSize;
    private int unselectedTextSize;
    private int selectedTextColor;
    private int unselectedTextColor;
    private int windowWidth;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_htab, container, false);
    }

    @Override
    protected void findViews(View view, Bundle savedInstanceState) {
        super.findViews(view, savedInstanceState);
        horizontalScrollView = (HorizontalScrollView) view.findViewById(R.id.hs_tabs);
        tabContainer = (LinearLayout) view.findViewById(R.id.llyt_tabContainer);
        viewPager = (ViewPager) view.findViewById(R.id.vp_pages);
    }

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        pageAdapter = new FragPageAdapter(getChildFragmentManager());
        viewPager.setAdapter(pageAdapter);
        viewPager.addOnPageChangeListener(onPageChangeListener);

        horizontalScrollView.setHorizontalScrollBarEnabled(false);

        Resources resources = mActivity.getResources();
        selectedTextSize = (int) resources.getDimension(R.dimen.tab_selectedSize);
        unselectedTextSize = (int) resources.getDimension(R.dimen.tab_unselectedSize);
        selectedTextColor = ContextCompat.getColor(mActivity, R.color.tab_selected);
        unselectedTextColor = ContextCompat.getColor(mActivity, R.color.tab_unselected);
        windowWidth = resources.getDisplayMetrics().widthPixels;
    }

    protected  void updatePager () {
        if (tabs == null) return;
        if (pageAdapter != null) {
            pageAdapter.notifyDataSetChanged();
        }
    }

    protected void updateTabs () {
        if (tabs == null) return;

        LayoutInflater inflater = LayoutInflater.from(mActivity);
        for (int i = 0; i < tabs.size(); i++) {
            TextView textView = (TextView) inflater.inflate(R.layout.item_tab, tabContainer, false);
            textView.setText(tabs.get(i).tabName);
            textView.setTag(i);
            textView.setOnClickListener(onTabClickListener);
            tabContainer.addView(textView);
        }
    }

    protected void setSelectedTab (int pos) {
        for (int i = 0; i < tabContainer.getChildCount(); i++) {
            TextView textView = (TextView) tabContainer.getChildAt(i);
            if (i != pos) {
                textView.setTextColor(unselectedTextColor);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, unselectedTextSize);
            } else {
                textView.setTextColor(selectedTextColor);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, selectedTextSize);
            }
        }

        View view = tabContainer.getChildAt(pos);
        int[] loc = new int[2];
        view.getLocationOnScreen(loc);
        if (loc[0] < 0) {
            int scrollX = horizontalScrollView.getScrollX() + loc[0];
            if (pos >= 1) {
                View prevChild = tabContainer.getChildAt(pos - 1);
                scrollX -= prevChild.getWidth() >> 1;
            }
            horizontalScrollView.smoothScrollTo(scrollX, 0);
        } else if (loc[0] + view.getWidth() > windowWidth) {
            int scrollX = horizontalScrollView.getScrollX() + loc[0] + view.getWidth() - windowWidth;
            if (pos < tabContainer.getChildCount() - 1) {
                View nextChild = tabContainer.getChildAt(pos + 1);
                scrollX += nextChild.getWidth() >> 1;
            }
            horizontalScrollView.smoothScrollTo(scrollX, 0);
        }
    }

    /**************************** horizontal tabs *********************************/
    private View.OnClickListener onTabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = (int) v.getTag();
            viewPager.setCurrentItem(pos, true);
            setSelectedTab(pos);
        }
    };

    /***************************** view pager *************************************/
    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setSelectedTab(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private class FragPageAdapter extends FragmentPagerAdapter {

        private Map<String, Fragment> fragments;

        public FragPageAdapter(FragmentManager fm) {
            super(fm);
            fragments = new HashMap<>();
        }

        @Override
        public Fragment getItem(int position) {
            Tab tab = tabs.get(position);
            Fragment fragment = fragments.get(tab.tabName);
            if (fragment == null) {
                fragment = mController.createFragment(tab.fragClazz, tab.fragParamClazzs, tab.fragParamValues);
                if (fragment != null) {
                    fragments.put(tab.tabName, fragment);
                    Bundle bundle = new Bundle();
                    bundle.putString("name", tab.tabName);
                    fragment.setArguments(bundle);
                    mController.setController(fragment);
                }
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return tabs == null? 0: tabs.size();
        }
    }
}
