package com.gy.widget.gridview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by yue.gan on 2016/11/2.
 *
 */
public class NestableGridView extends GridView {

    public NestableGridView(Context context) {
        super(context);
    }
    public NestableGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public NestableGridView(Context context, AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
