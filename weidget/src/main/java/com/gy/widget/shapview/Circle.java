package com.gy.widget.shapview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ganyu on 2016/5/15.
 *
 */
public class Circle extends View {

    public void setColor(int color) {
        this.color = color;
    }

    private boolean fillContent;
    private int color;
    private Paint paint;

    public Circle(Context context) {
        super(context);
    }

    public Circle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Circle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isFillContent() {
        return fillContent;
    }

    public void setFillContent(boolean fillContent) {
        this.fillContent = fillContent;
    }

    public int getColor() {
        return color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (paint == null) {
            paint = new Paint();
            color = Color.BLACK;
            paint.setColor(color);
            if (fillContent) {
                paint.setStyle(Paint.Style.FILL);
            } else {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(4);
            }
        }
        int width = getWidth();
        int height = getHeight();
        canvas.drawCircle(width/2, height/2, Math.min(width, height)/2 - paint.getStrokeWidth(), paint);
    }
}
