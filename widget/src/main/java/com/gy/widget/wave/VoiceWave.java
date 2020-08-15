package com.gy.widget.wave;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.audiofx.Visualizer;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.gy.widget.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class VoiceWave extends View {

    private final String TAG = "VoiceHWave";
    private int mWidth;                                 // 可显示宽度 （view测量宽度 - 左右padding）
    private int mHeight;                                // 可显示高度 （view测量高度 - 上下padding）
    private int mUpdateMillis = 100;                    // 刷新间隔 （毫秒，一般100ms以内肉眼都看不出卡顿）
    private int mLineWidth = 2;                       // 线宽 (dp 需要在init中转换成px)
    private int mLineCap = 2;                         // 线与线之间间隔 (dp 需要在init中转换成px)
    private int mLineStep = mLineWidth + mLineCap;    // 步长 (dp 需要在init中转换成px)
    private int mMinLineHeight = 10;
    private List<Line> mLines;                          // 需要绘制的线
    private AtomicInteger mLineNum = new AtomicInteger(0);
    private byte[] mFftData;
    private Visualizer mVisualizer;

    private int mLineColor = Color.BLACK;               // 线颜色
    private Paint mLinePaint;                           // 画笔

    private HandlerThread mWorkThread;
    private Handler mWorkHandler;
    private boolean mStartOnAttach = false;
    private boolean mIsStarted = false;

    public VoiceWave(Context context) {
        super(context);
    }

    public VoiceWave(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoiceWave(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VoiceWave);
            mLineWidth = typedArray.getDimensionPixelSize(R.styleable.VoiceWave_voicewave_line_width, mLineWidth);
            mLineCap = typedArray.getDimensionPixelSize(R.styleable.VoiceWave_voicewave_line_cap, mLineCap);
            mLineColor = typedArray.getColor(R.styleable.VoiceWave_voicewave_line_color, mLineColor);
            mLineStep = mLineWidth + mLineCap;
            typedArray.recycle();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        init();
        if (mStartOnAttach) startAnim();
        mStartOnAttach = false;
    }

    private void init () {
        if (mWorkThread != null) {
            mWorkThread.quitSafely();
        }
        mWorkThread = new HandlerThread(TAG);
        mWorkThread.start();
        mWorkHandler = new Handler(mWorkThread.getLooper());

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStrokeWidth(mLineWidth);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setColor(mLineColor);
        Log.i(TAG, "init  mLineWidth=" + mLineWidth + " mLineCap=" + mLineCap);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        mHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        initLines();
        Log.i(TAG, "onMeasure  w=" + mWidth + " h=" + mHeight + " lineNum=" + mLineNum.get());
    }

    private void initLines () {
        int lineStartX = mWidth % mLineStep / 2;
        mLineNum.set(mWidth / mLineStep);
        mLines = new ArrayList<>(mLineNum.get());
        for (int i = 0; i < mLineNum.get(); i++) {
            lineStartX += mLineStep;
            mLines.add(new Line(lineStartX, 0));
        }
        mFftData = new byte[mLineNum.get()];
        mMinLineHeight = mHeight/10;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (Line line: mLines) {
            canvas.drawLine(line.xStart, line.yStart, line.xEnd, line.yEnd, mLinePaint);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.i(TAG, "onDetachedFromWindow");
        stop();
        mWorkThread.quitSafely();
        mWorkThread = null;
        mWorkHandler = null;
        mLines.clear();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        mWorkHandler.removeCallbacksAndMessages(null);
        if (visibility == VISIBLE && mIsStarted) {
            mWorkHandler.postDelayed(mAnimRunable, mUpdateMillis);
        }
    }

    public void start () {
        Log.i(TAG, "start");
        if (isAttachedToWindow()) startAnim();
        else mStartOnAttach = true;
    }

    public void stop () {
        Log.i(TAG, "stop");
        mIsStarted = false;
        mWorkHandler.removeCallbacksAndMessages(null);
        if (mVisualizer != null) {
            mVisualizer.setEnabled(false);
            mVisualizer.release();
            mVisualizer = null;
        }
    }

    private void startAnim () {
        mIsStarted = true;
        mWorkHandler.removeCallbacksAndMessages(null);
        mWorkHandler.postDelayed(mAnimRunable, mUpdateMillis);
    }

    private Runnable mAnimRunable = new Runnable() {
        @Override
        public void run() {
            if (mWorkHandler != null) mWorkHandler.postDelayed(mAnimRunable, mUpdateMillis);
            if (mLineNum.get() <= 0) return;
            if (mVisualizer == null) {
                mVisualizer = new Visualizer(0);
                mVisualizer.setCaptureSize(mLineNum.get());
                mVisualizer.setDataCaptureListener(mOnDataCaptureListener,
                        Visualizer.getMaxCaptureRate()/2, false, true);
                int ret = mVisualizer.setEnabled(true);
                Log.d(TAG, "mVisualizer.setEnabled ret="+(ret==Visualizer.SUCCESS));
            }

            for (int i = 0; i < mLineNum.get(); i++) {
                mLines.get(i).setHeight(mFftData[i]);
            }

            postInvalidate();
        }
    };

    private Visualizer.OnDataCaptureListener mOnDataCaptureListener = new Visualizer.OnDataCaptureListener() {
        @Override
        public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
            Log.d(TAG, "onWaveFormDataCapture samplingRate="+samplingRate+", len="+waveform.length+", rate="+samplingRate);
            System.arraycopy(waveform, 0, mFftData, 0, Math.min(waveform.length, mLineNum.get()));
        }

        @Override
        public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
            Log.d(TAG, "onFftDataCapture samplingRate="+samplingRate+", len="+fft.length+", rate="+samplingRate);
            System.arraycopy(fft, 0, mFftData, 0, Math.min(fft.length, mLineNum.get()));
        }
    };

    class Line {
        int xStart;
        int xEnd;
        int yStart;
        int yEnd;

        Line(int x, int h) {
            xStart = x;
            xEnd = xStart;
            yStart = mHeight;
            yEnd = mHeight - Math.max(mMinLineHeight, h);
        }

        void setHeight (int h) {
            yEnd = mHeight - Math.max(mMinLineHeight, h);
        }
    }
}
