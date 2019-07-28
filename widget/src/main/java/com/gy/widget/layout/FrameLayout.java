package com.gy.widget.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yue.gan on 2016/5/17.
 *
 */
public class FrameLayout extends ViewGroup {
    public FrameLayout(Context context) {
        super(context);
    }

    public FrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (getChildCount() <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int childWidth = 0;
        int childHeight = 0;

        /**
         * 衡量标准是MeasureSpec.EXACTLY的时候，直接使用传入的宽高，
         * 不再通过子view来计算
         */
        if (widthMode == MeasureSpec.EXACTLY) {
            childWidth = MeasureSpec.getSize(widthMeasureSpec);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            childHeight = MeasureSpec.getSize(heightMeasureSpec);
        }

        /**
         * 衡量标准是MeasureSpec.UNSPECIFIED和MeasureSpec.AT_MOST的时候，通过子view计算宽高
         */
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        for (int i = 0; i< getChildCount(); i++) {
            View view = getChildAt(i);
            MarginLayoutParams params = (MarginLayoutParams) view.getLayoutParams();
            if (widthMode != MeasureSpec.EXACTLY) {
                childWidth = Math.max(params.leftMargin + params.rightMargin + view.getMeasuredWidth() +
                        paddingLeft + paddingRight, childWidth);
            }
            if (heightMode != MeasureSpec.EXACTLY) {
                childHeight = Math.max(params.topMargin + params.bottomMargin + view.getMeasuredHeight() +
                        paddingTop + paddingBottom, childHeight);
            }
        }

        setMeasuredDimension(childWidth, childHeight);
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!changed) {
            //如果大小和位置都没有改变就不再重新布局
            return;
        }
        int childCount = getChildCount();
        if (childCount <= 0) {
            //如果没有子view不需要重新布局
            return;
        }

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        int childWidth;
        int childHeight;
        int childLeftOffset;
        int childTopOffset;
        View childView;
        LayoutParams params;
        for (int i = 0; i < childCount; i++) {
            childView = getChildAt(i);
            childWidth = childView.getMeasuredWidth();
            childHeight = childView.getMeasuredHeight();
            params = (LayoutParams) childView.getLayoutParams();

            if (params.leftMargin > 0) {
                childLeftOffset = params.leftMargin + paddingLeft;
            } else {
                childLeftOffset = paddingLeft;
            }

            if (params.topMargin > 0) {
                childTopOffset = params.topMargin + paddingTop;
            } else {
                childTopOffset = paddingTop;
            }

            childView.layout(childLeftOffset, childTopOffset,
                    childLeftOffset + childWidth, childTopOffset + childHeight);
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
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
