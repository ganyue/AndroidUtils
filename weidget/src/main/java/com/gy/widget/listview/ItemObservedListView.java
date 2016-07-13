package com.gy.widget.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by ganyu on 2016/5/17.
 *
 */
public class ItemObservedListView extends ListView implements AbsListView.OnScrollListener {
    public ItemObservedListView(Context context) {
        super(context);
        init();
    }

    public ItemObservedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ItemObservedListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init () {
        setOnScrollListener(this);
    }

    private OnItemObservedListener onItemObservedListeners;
    private int observePosition;

    public void setOnItemObservedListener (int itemPosition, OnItemObservedListener listener) {
        onItemObservedListeners = listener;
        observePosition = itemPosition;
    }

    public void removeOnItemObservedListener () {
        onItemObservedListeners = null;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    private int[] lastItemLoc = new int[2];
    private int[] currentItemLoc = new int[2];
    private int[] listLoc = new int[2];
    private int itemHeight;
    private int itemMinY;
    private int itemMaxY;
    private int dy;
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if (view.getChildCount() <= 0
                || onItemObservedListeners == null) {
            return;
        }

        if (listLoc[1] == 0) {
            getLocationOnScreen(listLoc);
            itemMinY = listLoc[1];
            itemMaxY = listLoc[1] + getHeight();
        }

        if (firstVisibleItem > observePosition) {
            dy = 0;
            if (lastItemLoc[1] != 0 && lastItemLoc[1] > itemMinY) {
                dy = itemMinY - lastItemLoc[1];
                onItemObservedListeners.onItemScrolled(observePosition, State.BEYOND_TOP, null, dy);
            }
            lastItemLoc[0] = listLoc[0];
            lastItemLoc[1] = itemMinY;
        } else if (getLastVisiblePosition() < observePosition) {
            dy = 0;
            if (lastItemLoc[1] != 0 && lastItemLoc[1] < itemMaxY) {
                dy = itemMaxY - lastItemLoc[1];
                onItemObservedListeners.onItemScrolled(observePosition, State.BELOW_BOTTOM, null, dy);
            }
            lastItemLoc[0] = listLoc[0];
            lastItemLoc[1] = itemMaxY;
        } else {
            int dPos = observePosition - firstVisibleItem;
            View childV = getChildAt(dPos);
            childV.getLocationOnScreen(currentItemLoc);

            if (lastItemLoc[1] == 0) {
                itemHeight = childV.getHeight();
                itemMinY = listLoc[1] - itemHeight;
                lastItemLoc[0] = currentItemLoc[0];
                lastItemLoc[1] = currentItemLoc[1];
            } else {
                int dy = currentItemLoc[1] - lastItemLoc[1];
                onItemObservedListeners.onItemScrolled(observePosition, State.VISIBLE, currentItemLoc, dy);
                lastItemLoc[0] = currentItemLoc[0];
                lastItemLoc[1] = currentItemLoc[1];
            }
        }
    }

    public static class State {
        public final static int VISIBLE = 1;
        public final static int BEYOND_TOP = 2;
        public final static int BELOW_BOTTOM = 3;
    }

    public interface OnItemObservedListener {
        void onItemScrolled (int position, int state, int[] location, int dy);
    }
}
