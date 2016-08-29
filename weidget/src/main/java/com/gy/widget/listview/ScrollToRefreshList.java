package com.gy.widget.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by ganyu on 2016/8/29.
 *
 */
public class ScrollToRefreshList extends ListView implements AbsListView.OnScrollListener{

    protected View mVFootLoadingView;
    protected boolean mHasMore;

    public ScrollToRefreshList(Context context) {
        super(context);
        init();
    }

    public ScrollToRefreshList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrollToRefreshList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init () {

    }

    public void setHasMore (boolean hasMore) {
        this.mHasMore = hasMore;
    }

    public void setFootLoadingView (View view) {
        if (mVFootLoadingView != null) {
            removeFooterView(view);
        }
        addFooterView(view);
        mVFootLoadingView = view;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem + visibleItemCount == totalItemCount) {
            //TODO end of listview
        }
    }
}
