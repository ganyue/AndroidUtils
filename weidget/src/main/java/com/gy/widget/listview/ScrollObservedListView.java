package com.gy.widget.listview;

import android.content.Context;
import android.util.AttributeSet;
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
        if (onScrollObservedListeners == null || !onScrollObservedListeners.contains(listener)) {
            return;
        }
        onScrollObservedListeners.remove(listener);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    private int preLastChildPos = 0;
    private int preLastChildY = 0;
    private int listItemHeight = 0;
    private int listDeviderHeight = 0;
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if (view.getChildCount() <= 0 || onScrollObservedListeners == null || onScrollObservedListeners.size() <= 0) {
            return;
        }

        View lastChild = view.getChildAt(view.getChildCount() - 1);
        int[] lastChildLoc = new int[2];
        lastChild.getLocationOnScreen(lastChildLoc);
        if (preLastChildPos == 0) {
            //init
            preLastChildPos = view.getLastVisiblePosition();
            preLastChildY = lastChildLoc[1];
            listItemHeight = lastChild.getHeight();
            listDeviderHeight = ((ListView)view).getDividerHeight();
        } else {
            //calculate dy
            int lastVisibleChildPos = view.getLastVisiblePosition();
            int dPos = lastVisibleChildPos - preLastChildPos;
            int preLastChildCurrentY = lastChildLoc[1] - dPos * listItemHeight - listDeviderHeight * dPos;
            int dy = preLastChildY - preLastChildCurrentY;
            preLastChildPos = lastVisibleChildPos;
            preLastChildY = lastChildLoc[1];

            if (dy > 0) {
                //scroll up
                for (OnScrollObservedListener listener : onScrollObservedListeners) {
                    listener.onScrollUp(this, dy);
                }
            } else if (dy < 0) {
                //scroll down
                for (OnScrollObservedListener listener : onScrollObservedListeners) {
                    listener.onScrollDown(this, dy);
                }
            }
        }
    }

    public interface OnScrollObservedListener {
        void onScrollUp (ListView view, int dy);
        void onScrollDown (ListView view, int dy);
    }
}
