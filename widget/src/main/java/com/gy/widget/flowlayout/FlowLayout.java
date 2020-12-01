package com.gy.widget.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {


    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private List<List<View>> mLineViews = new ArrayList<>();// 用于安排每行显示的view
    private List<Integer> mLineHeight = new ArrayList<>();  // 用于安排每行位置
    private int lineCap;// 行间距
    private int columnCap;// 列间距

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float density = getContext().getResources().getDisplayMetrics().density;
        lineCap = (int) (density * 3);
        columnCap = (int) (density * 3);

        //获取测量的模式和尺寸大小
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec)-getPaddingLeft()-getPaddingRight();
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec)+getPaddingTop()+getPaddingBottom();


        //记录ViewGroup测量宽高
        int calculatedWidth = getPaddingLeft()+getPaddingRight();
        int calculatedHeight = getPaddingTop()+getPaddingBottom();

        //当前所占的宽高
        int currentLineWidth = 0;
        int currentLineHeight = 0;

        mLineViews.clear();
        mLineHeight.clear();
        //用来存储每一行上的子View
        List<View> lineView = new ArrayList<>();
        int childViewsCount = getChildCount();

        // 宽度不用担心，计算下每行最大宽度，最后看看用哪个就行，关键是高度，如果过度固定就需要算出固定高度内能放多少view
        int maxHeight = heightMode == MeasureSpec.EXACTLY? heightSize: Integer.MAX_VALUE;

        // 从后往前计算，保证最后添加View能显示
        for (int i = 0; i < childViewsCount; i++) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            int leftMargin = 0, rightMargin = 0, topMargin = 0, bottomMargin = 0;
            LayoutParams params = childView.getLayoutParams();
            if (params instanceof MarginLayoutParams) {
                leftMargin = ((MarginLayoutParams) params).leftMargin;
                rightMargin = ((MarginLayoutParams) params).rightMargin;
                topMargin = ((MarginLayoutParams) params).topMargin;
                bottomMargin = ((MarginLayoutParams) params).bottomMargin;
            }
            int childViewWidth = childView.getMeasuredWidth() + leftMargin + rightMargin;
            int childViewHeight = childView.getMeasuredHeight() + topMargin + bottomMargin;

            if (currentLineWidth + childViewWidth > widthSize) {

                if (lineView.size() <= 0) {
                    // 如果单单这一个view长度就超出了就直接add进去做一行
                    //当前行宽+子View+左右外边距>ViewGroup的宽度,换行
                    currentLineHeight = childViewHeight;
                    if (calculatedHeight + currentLineHeight > maxHeight) break; // 超过最大高度，直接跳出循环不再添加其他view
                    calculatedWidth = widthSize;
                    calculatedHeight += currentLineHeight;
                    //添加行高
                    mLineHeight.add(currentLineHeight);
                    lineView.add(childView);
                    mLineViews.add(new ArrayList<>(lineView));
                    lineView = new ArrayList<>();
                    currentLineWidth = 0;
                    currentLineHeight = 0;
                    if (i == childViewsCount - 1) break; // 如果刚好就是最后一个，这里因为已经加入作为一行了，直接break即可
                } else {
                    //当前行宽+子View+左右外边距>ViewGroup的宽度,换行
                    if (calculatedHeight + currentLineHeight > maxHeight) break; // 超过最大高度，直接跳出循环不再添加其他view
                    calculatedWidth = Math.max(currentLineWidth, widthSize);
                    calculatedHeight += currentLineHeight;
                    //添加行高
                    mLineHeight.add(currentLineHeight);
                    mLineViews.add(new ArrayList<>(lineView));
                    lineView = new ArrayList<>();
                    lineView.add(childView);
                    currentLineWidth = childViewWidth + columnCap;
                    currentLineHeight = childViewHeight;
                }
            } else {
                //当前行宽+子View+左右外边距<=ViewGroup的宽度,不换行
                currentLineWidth += childViewWidth + columnCap;
                currentLineHeight = Math.max(currentLineHeight, childViewHeight);
                //添加行对象里的子View
                lineView.add(childView);
            }
            if (i == childViewsCount - 1) {
                //最后一个子View的时候
                if (calculatedHeight + currentLineHeight > maxHeight) break; // 超过最大高度，直接跳出循环不再添加其他view
                calculatedWidth = Math.max(childViewWidth, calculatedWidth);
                calculatedHeight += currentLineHeight;
                //添加行对象
                mLineViews.add(new ArrayList<>(lineView));
                //添加行高
                mLineHeight.add(currentLineHeight);
            }
        }

        // 算下行高，如果当前默认行高太高，就直接用剩余高度来算出新的行高
        int totalLineCap = lineCap * mLineViews.size();
        if (calculatedHeight + totalLineCap > maxHeight) {
            lineCap = (maxHeight - calculatedHeight)/mLineViews.size();
            calculatedHeight = maxHeight;
        } else {
            calculatedHeight += totalLineCap;
        }

        // 真实宽高
        int actualWidth = widthMode == MeasureSpec.EXACTLY? widthSize: calculatedWidth;
        int actualHeight = calculatedHeight;

        setMeasuredDimension(actualWidth, actualHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = getPaddingLeft();
        int top = getPaddingTop();
        //一共有几行
        int lines = mLineViews.size();
        for (int i = 0; i < lines; i++) {
            //每行行高
            int lineHeight = mLineHeight.get(i);
            List<View> viewList = mLineViews.get(i);

            for (int j = 0; j < viewList.size(); j++) {
                View view = viewList.get(j);
                int leftMargin = 0, rightMargin = 0, topMargin = 0;
                LayoutParams params = view.getLayoutParams();
                if (params instanceof MarginLayoutParams) {
                    leftMargin = ((MarginLayoutParams) params).leftMargin;
                    rightMargin = ((MarginLayoutParams) params).rightMargin;
                    topMargin = ((MarginLayoutParams) params).topMargin;
                }
                int vl = left + leftMargin;
                int vt = top + topMargin;
                int vr = vl + view.getMeasuredWidth();
                int vb = vt + view.getMeasuredHeight();
                view.layout(vl, vt, vr, vb);
                left += columnCap + view.getMeasuredWidth() + leftMargin + rightMargin;
            }
            left = getPaddingLeft();
            top += lineHeight + lineCap;
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
