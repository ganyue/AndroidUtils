package com.gy.widget.indicator;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sam_gan on 2016/5/9.
 *
 */
public abstract class IndicatorBase extends ViewGroup implements IIndicator {

    protected SparseArray<PointF> mPositions;
    protected View mVIndicator;
    protected int mIndicatorX;
    protected int mIndicatorY;
    protected int mCurrentIndicatorIndex;

    public IndicatorBase(Context context) {
        super(context);
        init();
    }

    public IndicatorBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init () {
        mPositions = new SparseArray<>();
    }

    /**
     * add a indicator view at the index of 0
     * @param indicatorV .
     */
    public void setIndicator (View indicatorV) {
        addView(indicatorV);
    }

    public void setCurrentIndicatorIndex (int currentIndicatorIndex) {
        mCurrentIndicatorIndex = currentIndicatorIndex;
        if (mVIndicator != null) {
//            currentIndicatorIndex -= 1;
            currentIndicatorIndex = currentIndicatorIndex < 0? 0: currentIndicatorIndex;
            scrollToPosition(currentIndicatorIndex, 0.01f);
        }
    }

    @Override
    public void scrollToPosition(int position, float percentage) {

        if (percentage == 0) {
            return;
        }

        int nextPosition = position + 1;

        PointF childPos = mPositions.get(position);
        PointF nextChildPos = mPositions.get(nextPosition);

        if (childPos == null) {
            View child = getChildAt(position);
            childPos = new PointF(child.getLeft() ,child.getTop());
            mPositions.put(position, childPos);
        }

        if (nextChildPos == null) {
            View nextChild = getChildAt(nextPosition);
            nextChildPos = new PointF(nextChild.getLeft(), nextChild.getTop());
            mPositions.put(nextPosition, nextChildPos);
        }

        int indicatorX = (int) ((nextChildPos.x - childPos.x) * percentage + childPos.x);
        int indicatorY = (int) ((nextChildPos.y - childPos.y) * percentage + childPos.y);

        mVIndicator.offsetLeftAndRight(indicatorX - mIndicatorX);
        mVIndicator.offsetTopAndBottom(indicatorY - mIndicatorY);

        mIndicatorX = indicatorX;
        mIndicatorY = indicatorY;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new IndicatorBase.LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
