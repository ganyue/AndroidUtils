package com.gy.widget.scrollview;

import android.content.Context;
import android.util.AttributeSet;

import com.gy.widget.listview.ScrollObservedListView;

/**
 * Created by ganyu on 2016/10/24.
 *
 */
public class ScrollView extends android.widget.ScrollView{
    public ScrollView(Context context) {
        super(context);
    }

    public ScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollChangedListener != null) {
            onScrollChangedListener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    private OnScrollChangedListener onScrollChangedListener;
    public void setOnScrollChangedListener (OnScrollChangedListener listener) {
        onScrollChangedListener = listener;
    }

    public interface OnScrollChangedListener {
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }
}
