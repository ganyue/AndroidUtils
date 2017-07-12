package com.gy.widget.indicator;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sam_gan on 2016/5/9.
 *
 */
public class HorizontalIndicator extends IndicatorBase {

    public HorizontalIndicator(Context context) {
        super(context);
    }

    public HorizontalIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (getChildCount() < 1) {
            /**
             * 默认“指示器”是最后一个view，所以需要child的个数至少要有一个。
             * 当然，想要有“指示器”的效果，肯定是要child个数至少要有三个的
             */
        return ;
//            throw new InflateException("indicator shell has more than 1 child view");
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
        for (int i = 0; i< getChildCount() - 1; i++) {
            View view = getChildAt(i);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (params instanceof IndicatorBase.LayoutParams) {
                MarginLayoutParams marginParams = (MarginLayoutParams) view.getLayoutParams();
                if (widthMode != MeasureSpec.EXACTLY) {
                    childWidth += marginParams.leftMargin + marginParams.rightMargin + view.getMeasuredWidth();
                }
                if (heightMode != MeasureSpec.EXACTLY) {
                    childHeight = Math.max(marginParams.topMargin + marginParams.bottomMargin + view.getMeasuredHeight(), childHeight);
                }
            } else {
                if (widthMode != MeasureSpec.EXACTLY) {
                    childWidth += view.getMeasuredWidth();
                }
                if (heightMode != MeasureSpec.EXACTLY) {
                    childHeight = Math.max(view.getMeasuredHeight(), childHeight);
                }
            }
        }

        setMeasuredDimension(childWidth, childHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!changed) {
            return;
        }
        /**
         * 把子节点横向排列起来，为了更好的移植性，不使用自定义属性来排列子view，
         * 根据平时使用场景，直接把view放在中间的位置就好
         */
        int childCount = getChildCount();
        int indicatorTabCount = childCount - 1;//最后一个view是indicator，所以要减1

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if (indicatorTabCount <= 0) {
            throw new InflateException("indicator shell has more than 1 child view");
        }

        int stepWidth = width/indicatorTabCount;

        int childWidth;
        int childHeight;
        int childLeftOffset;
        int childTopOffset;
        int childStartX;
        View childView;
        LayoutParams marginParams;
        for (int i = 0; i < indicatorTabCount; i++) {
            childView = getChildAt(i);
            childWidth = childView.getMeasuredWidth();
            childHeight = childView.getMeasuredHeight();
            marginParams = (LayoutParams) childView.getLayoutParams();

            if (marginParams.leftMargin > 0) {
                childLeftOffset = marginParams.leftMargin;
            } else {
                if (childWidth >= stepWidth) {
                    childLeftOffset = 0;
                } else {
                    childLeftOffset = (stepWidth - childWidth)/2;
                }
            }

            if (marginParams.topMargin > 0) {
                childTopOffset = marginParams.topMargin;
            } else {
                if (childHeight >= height) {
                    childTopOffset = 0;
                } else {
                    childTopOffset = (height - childHeight)/2;
                }
            }

            childStartX = stepWidth * i + childLeftOffset;
            childView.layout(childStartX, childTopOffset,
                    childStartX + childWidth, childTopOffset + height);
        }

        mVIndicator = getChildAt(childCount - 1);
        childWidth = mVIndicator.getMeasuredWidth();
        childHeight = mVIndicator.getMeasuredHeight();
        marginParams = (LayoutParams) mVIndicator.getLayoutParams();

        if (marginParams.leftMargin > 0) {
            childLeftOffset = marginParams.leftMargin;
        } else {
            if (childWidth >= stepWidth) {
                childLeftOffset = 0;
            } else {
                childLeftOffset = (stepWidth - childWidth)/2;
            }
        }

        if (marginParams.topMargin > 0) {
            childTopOffset = marginParams.topMargin;
        } else {
            if (childHeight >= height) {
                childTopOffset = 0;
            } else {
                childTopOffset = (height - childHeight)/2;
            }
        }

        mIndicatorX = mCurrentIndicatorIndex * stepWidth + childLeftOffset;
        mIndicatorY = childTopOffset;

        mVIndicator.layout(mIndicatorX, mIndicatorY,
                mIndicatorX + childWidth, mIndicatorY + height);

    }

}
