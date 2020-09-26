package com.gy.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleableRes;
import android.util.AttributeSet;
import android.view.View;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class WidgetBaseAnimView extends View implements Runnable {

    protected int mWidth;                                 // 可显示宽度 （view测量宽度 - 左右padding）
    protected int mHeight;                                // 可显示高度 （view测量高度 - 上下padding）
    protected int mUpdateMillis = 100;                    // 刷新间隔 （毫秒，一般100ms以内肉眼都看不出卡顿）
    protected Paint mPaint;
    protected Path mPath;
    protected AtomicBoolean mInited = new AtomicBoolean(false);

    private HandlerThread mWorkThread;
    private Handler mWorkHandler;
    private boolean mStartOnAttach = false;
    private boolean mIsStarted = false;

    public WidgetBaseAnimView(Context context) {
        super(context);
    }

    public WidgetBaseAnimView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WidgetBaseAnimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            int[] id = getStyleableResId();
            if (id == null || id.length <= 0) return;
            TypedArray typedArray = context.obtainStyledAttributes(attrs, id);
            initAttrs();
            typedArray.recycle();
        }
    }

    protected abstract @StyleableRes int[] getStyleableResId ();
    protected abstract void initAttrs ();

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initThread();
        if (mStartOnAttach) startAnim();
        mStartOnAttach = false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
        mWorkThread.quitSafely();
        mWorkThread = null;
        mWorkHandler = null;
    }

    private void initThread () {
        if (mWorkThread != null) {
            mWorkThread.quitSafely();
        }
        mWorkThread = new HandlerThread(getClass().getName());
        mWorkThread.start();
        mWorkHandler = new Handler(mWorkThread.getLooper());

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLACK);
        mPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        mHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        if (mInited.compareAndSet(false, true)) init();
        else reInit();
    }

    protected abstract void init();
    protected abstract void reInit();

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        if (mWorkHandler != null) {
            mWorkHandler.removeCallbacksAndMessages(null);
            if (visibility == VISIBLE && mIsStarted) {
                mWorkHandler.postDelayed(this, mUpdateMillis);
            }
        }
    }

    public void start () {
        if (isAttachedToWindow()) startAnim();
        else mStartOnAttach = true;
    }

    public void stop () {
        mIsStarted = false;
        mWorkHandler.removeCallbacksAndMessages(null);
    }

    private void startAnim () {
        mIsStarted = true;
        mWorkHandler.removeCallbacksAndMessages(null);
        mWorkHandler.postDelayed(this, mUpdateMillis);
    }

    @Override
    public void run() {
        if (mWorkHandler != null) mWorkHandler.postDelayed(this, mUpdateMillis);
        if (mWidth <= 0 || mHeight <= 0) return;
        calculate();
        postInvalidate();
    }

    protected abstract void calculate ();
}
