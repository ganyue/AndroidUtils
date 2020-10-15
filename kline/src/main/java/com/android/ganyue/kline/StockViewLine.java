package com.android.ganyue.kline;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class StockViewLine extends View {

    private List<DayInfo> mDayInfos = new ArrayList<>();
    private Paint mRaisePaint;
    private Paint mRaiseTailPaint;
    private Paint mFallPaint;
    private Paint mFallTailPaint;
    private int mLineWidth = 12;
    private int mLineTailWidth = 2;
    private int mWidth;
    private int mHeight;

    public StockViewLine(Context context) {
        super(context);
    }

    public StockViewLine(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StockViewLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    public void setLines (List<DayInfo> lines) {
        mDayInfos = lines;
        postInvalidate();
    }



}
