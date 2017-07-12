package com.gy.widget.viewpager.tabpage;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gy.widget.R;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganyu on 2016/9/13.
 *
 */
public class TabPages extends LinearLayout{

    private HorizontalScrollView horizontalScrollView;
    private LinearLayout tabContainer;
    private View divider;
    private ViewPager viewPager;
    private List<Tab> tabs;
    private FragPageAdapter pageAdapter;
    private TabPagesCallback callback;

    private int selectedTextSize;
    private int unSelectedTextSize;

    private int windowWidth;
    private int selectedTextColor;
    private int unselectedTextColor;

    public TabPages(Context context) {
        super(context);
        initViews();
    }

    public TabPages(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public TabPages(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    public void setCurrentItem (int pos) {
        viewPager.setCurrentItem(pos);
    }

    public void setTabPagesCallback (TabPagesCallback callback) {
        this.callback = callback;
    }

    private void initViews () {
        Context context = getContext();
        windowWidth = context.getResources().getDisplayMetrics().widthPixels;
        selectedTextColor = Color.parseColor("#fed102");
        unselectedTextColor = Color.parseColor("#464646");
        selectedTextSize = 16;
        unSelectedTextSize = 14;

        LayoutInflater inflater = LayoutInflater.from(context);
        View rootV = inflater.inflate(R.layout.view_tabpage, this, false);
        horizontalScrollView = (HorizontalScrollView) rootV.findViewById(R.id.hs_tabbar);
        tabContainer = (LinearLayout) rootV.findViewById(R.id.llyt_tabContainer);
        viewPager = (ViewPager) rootV.findViewById(R.id.vp_content);
        addView(rootV);
        setBackgroundColor(Color.WHITE);

        horizontalScrollView.setHorizontalScrollBarEnabled(false);
    }

    public void bindTabs (List<Tab> tabs, FragmentManager fragmentManager) {

        if (tabs == null) return;
        if (this.tabs == null) this.tabs = new ArrayList<>();
        this.tabs.clear();
        this.tabs.addAll(tabs);

        Context context = getContext();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int textVLeftPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, displayMetrics);
        int textVRightPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22, displayMetrics);

        tabContainer.removeAllViews();
        for (int i = 0; i < tabs.size(); i++) {
            TextView textView = new TextView(context);
            textView.setText(tabs.get(i).tabName);
            textView.setTag(i);
            textView.setOnClickListener(onTabClickListener);
            textView.setPadding(textVLeftPadding, 0, textVRightPadding, 0);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, unselectedTextColor);
            LayoutParams textVParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            tabContainer.addView(textView, textVParams);
        }

        if (pageAdapter == null) {
            pageAdapter = new FragPageAdapter(fragmentManager);
            viewPager.setAdapter(pageAdapter);
            viewPager.addOnPageChangeListener(onPageChangeListener);
        } else {
            pageAdapter.notifyDataSetChanged();
        }
        setSelectedTab(0);
    }

    protected void setSelectedTab (int pos) {
        for (int i = 0; i < tabContainer.getChildCount(); i++) {
            TextView textView = (TextView) tabContainer.getChildAt(i);
            if (i != pos) {
                textView.setTextColor(unselectedTextColor);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, unSelectedTextSize);
            } else {
                textView.setTextColor(selectedTextColor);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, selectedTextSize);
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
    private OnClickListener onTabClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = (int) v.getTag();
            viewPager.setCurrentItem(pos, true);
            setSelectedTab(pos);
            if (tabClickDelegate != null) {
                tabClickDelegate.onTabClick(pos, ""+((TextView)v).getText());
            }
        }
    };

    private OnTabClickListener tabClickDelegate;
    public void setOnTabClickListener (OnTabClickListener listener) {
        tabClickDelegate = listener;
    }

    public interface OnTabClickListener {
        void onTabClick (int pos, String name);
    }
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

    private class FragPageAdapter extends TabPageAdapter {

        public FragPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d("yue.gan", "tabPages getItem ..." + position);
            Tab tab = tabs.get(position);

            Fragment fragment = null;

            if (callback != null) {
                fragment = callback.createFragment(tab);
            } else {
                fragment = createFragment(tab);
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return tabs == null? 0: tabs.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            Log.d("yue.gan", "tabPages destroyItem ..." + position);
        }
    }

    public Fragment createFragment (Tab tab) {
        Fragment fragment = null;
        try {
            Constructor constructor = tab.fragClazz.getConstructor();
            fragment = (Fragment) constructor.newInstance();
            if (tab.arguments != null) fragment.setArguments(tab.arguments);
            return fragment;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public interface TabPagesCallback {
        Fragment createFragment(Tab tab);
    }
}
