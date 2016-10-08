package com.gy.widget.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganyu on 2016/5/17.
 *
 */
public class ScrollObservedListView extends android.widget.ListView implements AbsListView.OnScrollListener {
    public ScrollObservedListView(Context context) {
        super(context);
        init();
    }

    public ScrollObservedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrollObservedListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init () {
        setOnScrollListener(this);
    }

    private List<OnScrollObservedListener> onScrollObservedListeners;

    public void addOnScrollObservedListener (OnScrollObservedListener listener) {
        if (onScrollObservedListeners == null) {
            onScrollObservedListeners = new ArrayList<>();
        }
        onScrollObservedListeners.add(listener);
    }

    public void removeOnScrollObservedListener (OnScrollObservedListener listener) {
        if (onScrollObservedListeners == null || !onScrollObservedListeners.contains(listener)){
            return;
        }
        onScrollObservedListeners.remove(listener);
    }

    private int observedItem;

    public void setObservedItem (int pos) {
        observedItem = pos;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        if (view.getChildCount() <= 0
                || onScrollObservedListeners == null
                || onScrollObservedListeners.size() <= 0) {
            return;
        }


        if (firstVisibleItem > observedItem) {
            //BeyondScreen
            for (OnScrollObservedListener listener: onScrollObservedListeners) {
                listener.onObservedItemPosChanged(ItemState.BeyondScreen, null, null);
            }
        } else if (firstVisibleItem + visibleItemCount <= observedItem) {
            //BelowScreen
            for (OnScrollObservedListener listener: onScrollObservedListeners) {
                listener.onObservedItemPosChanged(ItemState.BelowScreen, null, null);
            }
        } else {
            //OnScreen
            View childV = view.getChildAt(observedItem - firstVisibleItem);
            if (childV == null) return;
            int[] location = new int[2];
            int[] size = new int[2];
            size[0] = childV.getWidth();
            size[1] = childV.getHeight();
            childV.getLocationOnScreen(location);
            for (OnScrollObservedListener listener: onScrollObservedListeners) {
                listener.onObservedItemPosChanged(ItemState.OnScreen, location, size);
            }
        }

    }

    public enum ItemState {
        BelowScreen, OnScreen, BeyondScreen,
    }

    public interface OnScrollObservedListener {
        /**
         * @param state     enum visible state
         * @param location  int[2] item at 0 is x, item at 1 is height
         * @param size      int[2] item at 0 is width, item at 1 is height
         */
        void onObservedItemPosChanged (ItemState state, int[] location, int[] size);
    }
}
