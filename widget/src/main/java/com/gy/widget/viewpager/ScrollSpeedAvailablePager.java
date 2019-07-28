package com.gy.widget.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;

import java.lang.reflect.Field;

/**
 * Created by yue.gan on 2016/10/9.
 *
 * 可以改变pager自动滑动速度
 */
public class ScrollSpeedAvailablePager extends ViewPager {
    public ScrollSpeedAvailablePager(Context context) {
        super(context);
    }

    public ScrollSpeedAvailablePager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    FixedSpeedScroller mScroller;
    public void setScrollSpeed (int speed) {
        try {
            Field mField;
            mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);

            mScroller = new FixedSpeedScroller(
                    getContext(),
                    new DecelerateInterpolator());
            mScroller.setmDuration(speed);
            mField.set(this, mScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
