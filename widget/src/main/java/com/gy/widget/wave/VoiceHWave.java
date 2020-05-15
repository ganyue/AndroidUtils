package com.gy.widget.wave;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Choreographer;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class VoiceHWave extends View {
    public VoiceHWave(Context context) {
        super(context);
        init();
    }

    public VoiceHWave(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VoiceHWave(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private final String TAG = "VoiceHWave";
    private int mWidth;                                 // 可显示宽度 （view测量宽度 - 左右padding）
    private int mHeight;                                // 可显示高度 （view测量高度 - 上下padding）
    private int mUpdateMillis = 100;                    // 刷新间隔 （毫秒，一般100ms以内肉眼都看不出卡顿）
    private float mLineWidth = 2;                       // 线宽 (dp 需要在init中转换成px)
    private float mLineCap = 2;                         // 线与线之间间隔 (dp 需要在init中转换成px)
    private float mLineStep = mLineWidth + mLineCap;    // 步长 (dp 需要在init中转换成px)
    private List<Line> mLines;                          // 需要绘制的线
    private int mLineHLevel = 5;                        // 线高分级 （把line高度分成几个级别）
    private float[] mLineHLevels;                       // 分级后的每级别的线高
    private float[] mLineStartYs;                       // 每个级别线对应的线绘制的起始Y（提前计算，节省计算时间）
    private float[] mLineEndYs;                         // 每个级别线对应的线绘制的结束Y（提前计算，节省计算时间）
    private float mMaxScaleLevel = 20;                  // 最大音量 （用于吧外部出入数据分别对应到某个线高上）
    private float[] mScaleLevels;                       // 音量的分级 （用于把外部传入数据对应到某个线高上）
    private int mNextLineHeightLevel;                   // 下个被添加进 mLines 中的线的高度的级别
    private boolean mIsMeasured = false;                // 是否已经测量完毕，只有onMeasure后才可以绘制
    private boolean mStartMove = false;                 // 是否开始移动mLines的坐标，最后一个线段超框后需要开始移动
    private float startReuseX;                          // 判断是否要把第一条线放到最后的标准（最后一条线移动到需要添加下调线的时候）

    private int mLineColor = Color.BLACK;               // 线颜色
    private Paint mLinePaint;                           // 画笔
    private float density;                              // 像素密度，用来重新计算线宽，线间距（dp转px）

    private Choreographer choreographer = Choreographer.getInstance();
    private void init () {
        mLineHLevels = new float[mLineHLevel];
        mLineStartYs = new float[mLineHLevel];
        mLineEndYs = new float[mLineHLevel];
        mScaleLevels = new float[mLineHLevel];
        mLines = new ArrayList<>();
        setMaxVolume(mMaxScaleLevel);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStrokeWidth(mLineWidth);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setColor(mLineColor);
        density = getContext().getResources().getDisplayMetrics().density;
        mLineWidth = mLineWidth * density;
        mLineCap = mLineCap * density;
        mLineStep = mLineWidth + mLineCap;
        Log.i(TAG, "init  mLineWidth=" + mLineWidth + " mLineCap=" + mLineCap);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        mHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();


        // 高度分成 mLineHLevel + 1 段，留1段是为了不绘制到边框
        float dy = mHeight / (mLineHLevel + 1f);
        for (int i = 0; i < mLineHLevel; i++) {
            mLineHLevels[i] = dy * (i + 1);
            mLineStartYs[i] = (mHeight - mLineHLevels[i]) / 2;
            mLineEndYs[i] = mLineStartYs[i] + mLineHLevels[i];
        }

        mNextLineHeightLevel = 0;
        startReuseX = mWidth - mLineStep;
        mIsMeasured = true;
        Log.i(TAG, "onMeasure  w=" + mWidth + " h=" + mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // 没有线就不需要绘制，直接返回
        if (mLines.isEmpty()) return;

        if (!mStartMove) {
            // 不需要移动，直接绘制每条线
            for (Line line: mLines) {
                canvas.drawLine(line.xStart, line.yStart, line.xEnd, line.yEnd, mLinePaint);
            }
        } else {
            // 需要移动，每条线的x都需要变动
            for (Line line: mLines) {
                line.resetX(line.xStart - mLineStep);
                canvas.drawLine(line.xStart, line.yStart, line.xEnd, line.yEnd, mLinePaint);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.i(TAG, "onDetachedFromWindow");
        stop();
    }

    public void start () {
        Log.i(TAG, "start");
        choreographer.postFrameCallbackDelayed(mFrameCallback, mUpdateMillis);
    }

    public void stop () {
        Log.i(TAG, "stop");
        choreographer.removeFrameCallback(mFrameCallback);
    }

    private Choreographer.FrameCallback mFrameCallback = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long frameTimeNanos) {
            Log.i(TAG, "mFrameCallback : " + frameTimeNanos/1000000);
            choreographer.postFrameCallbackDelayed(mFrameCallback, mUpdateMillis);
            addLine();
            invalidate();
        }
    };


    private void addLine () {
        if (!mIsMeasured) {
            return;
        }

        if (mLines.isEmpty()) {
            mLines.add(new Line(0, mNextLineHeightLevel));
            return;
        }

        Line lineEnd = mLines.get(mLines.size() - 1);

        if (!mStartMove) {
            // 最后一条线还未超框
            Line line = new Line(lineEnd.xStart + mLineStep, mNextLineHeightLevel);
            mLines.add(line);
            mNextLineHeightLevel = 0;
            mStartMove = line.xStart > mWidth;
            Log.i(TAG, "addNew Line : " + line.xStart);
            return;
        }

        // 最后一条线超框后需要移动，如果最后一条线还未移动 【线宽+线间隔】 的长度，不需要再添加下条线（因为下条线还看不到）
        if (lineEnd.xStart > startReuseX) {
            return;
        }

        // 直接复用第一条线，因为第一条线这时候已经出框，看不到了
        Line line = mLines.remove(0);
        line.reset(lineEnd.xStart + mLineStep, mNextLineHeightLevel);
        mLines.add(line);
        mNextLineHeightLevel = 0;
        Log.i(TAG, "reuse Line : " + line.xStart);
        Log.i(TAG, "mLines size() " + mLines.size());
    }

    public void setMaxVolume (float maxVolume) {
        mMaxScaleLevel = maxVolume;
        float dy = mMaxScaleLevel / mLineHLevel;
        for (int i = 0; i < mLineHLevel; i++) {
            mScaleLevels[i] = dy * (i + 1);
        }
    }

    public void setVolume (float volume) {
        mNextLineHeightLevel = 0;
        for (int i = 1; i < mLineHLevel; i++) {
            if (volume > mScaleLevels[i]) {
                mNextLineHeightLevel = i;
            } else {
                break;
            }
        }
    }

    class Line {
        float xStart;
        float xEnd;
        float yStart;
        float yEnd;
        float height;

        Line(float x, int heightLevel) {
            xStart = x;
            xEnd = xStart;
            yStart = mLineStartYs[heightLevel];
            yEnd = mLineEndYs[heightLevel];
            height = mLineHLevels[heightLevel];
        }

        void reset (float x, int heightLevel) {
            xStart = x;
            xEnd = xStart;
            yStart = mLineStartYs[heightLevel];
            yEnd = mLineEndYs[heightLevel];
            height = mLineHLevels[heightLevel];
        }

        void resetX (float x) {
            xStart = x;
            xEnd = xStart;
        }
    }
}
