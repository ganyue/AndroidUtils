package com.gy.widget.wave;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TestView extends View {
    public TestView(Context context) {
        super(context);
    }

    public TestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        initLine();
    }

    private void initLine () {
        lines = new Line[width];
        for (int i = 0; i < width; i++) {
            lines[i] = new Line(i, 3);
        }

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStrokeWidth(1);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setColor(Color.BLACK);
    }

    private int width;
    private int height;
    private Line[] lines;
    private Paint mLinePaint;
    private File file;
    private HandlerThread handlerThread;
    private Handler handler;
    public boolean start (String p) {
        if (handler != null) return false;
        pos = 0;
        file = new File(p);
        handlerThread = new HandlerThread("aaa");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        handler.postDelayed(runnable, 100);
        return true;
    }

    public void stop () {
        handlerThread.quitSafely();
        handlerThread = null;
        handler = null;
    }

    private int pos = 0;
    private int step = 20;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(runnable, 100);
            try {
                FileInputStream fin = new FileInputStream(file);
                fin.skip(pos * 280 * 8);
                pos += step;
                byte[] data = new byte[280];//为了1s内显示44100双声道16bit的数据在600像素内,每70个点选出一个最大值来绘制
                int tmpHeight;
                for (int i = 0; i < width; i++) {
                    tmpHeight = 32;
                    if (fin.read(data)<=0) break;
                    for (int j = 0; j < 280; j+=4) {//每个数据为4个byte, 16bit * 双声道 = 32位 也就是4byte
                        tmpHeight = Math.max(tmpHeight, (int)data[j+2] + ((int)data[j+3] << 8));
                    }
                    lines[i].setHeight(tmpHeight/16);//太长了,除个才能16显示完全
                }
                postInvalidate();
                fin.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        for (Line line: lines) {
            canvas.drawLine(line.xStart, line.yStart, line.xEnd, line.yEnd, mLinePaint);
        }
    }

    class Line {
        int xStart;
        int xEnd;
        int yStart;
        int yEnd;

        Line(int x, int h) {
            xStart = x;
            xEnd = xStart;
            yStart = (height - h)/2;
            yEnd = yStart + h;
        }

        void setHeight (int h) {
            yStart = (height - h)/2;
            yEnd = yStart + h;
        }
    }
}
