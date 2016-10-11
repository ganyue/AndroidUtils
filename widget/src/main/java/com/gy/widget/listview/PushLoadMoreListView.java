package com.gy.widget.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;

import com.gy.widget.R;

/**
 * Created by ganyu on 2016/10/8.
 *
 */
public class PushLoadMoreListView extends ScrollObservedListView {

    public PushLoadMoreListView(Context context) {
        super(context);
    }

    public PushLoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PushLoadMoreListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        super.init();
        showLoadMoreFooter();
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        hideLoadMoreFooter();
    }

    private OnLoadMoreListener onLoadMoreListener;
    public void setOnLoadMoreListener (OnLoadMoreListener listener) {
        onLoadMoreListener = listener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        super.onScrollStateChanged(view, scrollState);
        /**
         * 在不滑动的时候检查是否需要加载更多
         */
        if (scrollState == SCROLL_STATE_IDLE && onLoadMoreListener != null) {
            int firstVisibleItem = getFirstVisiblePosition();
            int lastVisibleItem = getLastVisiblePosition();
            int totalItemCount = getCount();
            if (firstVisibleItem == 0 && lastVisibleItem == totalItemCount - 1) {
                /**一页显示完全，不需要加载更多*/
                return;
            }
            if (lastVisibleItem == totalItemCount - 1 && onLoadMoreListener.canLoadMore()) {
                showLoadMoreFooter();
                onLoadMoreListener.onLoadMore();
            } else {
                hideLoadMoreFooter();
            }
        }
    }

    private boolean isLoadMoreFooterShown () {
        if (getFooterViewsCount() > 0 && getFooterView() == footerV) {
            return true;
        }
        return false;
    }

    public void showLoadMoreFooter () {
        if (!isLoadMoreFooterShown()) {
            addFooterView(getFooterView());
            smoothScrollToPosition(getCount());
        }
    }

    public void hideLoadMoreFooter () {
        if (isLoadMoreFooterShown()) {
            removeFooterView(getFooterView());
        }
    }

    private View footerV;
    private int refreshViewStyle = 0;
    public void setRefreshViewStyle (int style) {
        refreshViewStyle = style;
    }

    private View getFooterView() {
        if (footerV == null) {

            switch (refreshViewStyle) {
                //TODO add other style
                default:
                    footerV = LayoutInflater.from(getContext()).inflate(R.layout.item_normal_load_more_footer, this, false);
//                    AbsListView.LayoutParams defaultParams = new AbsListView.LayoutParams(
//                            LayoutParams.MATCH_PARENT,
//                            getResources().getDisplayMetrics().widthPixels/6);
//                    footerV.setLayoutParams(defaultParams);
                    break;
            }
        }
        return footerV;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
        boolean canLoadMore();
    }
}
