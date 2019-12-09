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

    private boolean fillContent = false;
    private int color = Color.WHITE;
    private Paint paint;
    private float r;
    private int cx,cy;

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

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(color);
            if (fillContent) {
                paint.setStyle(Paint.Style.FILL);
            } else {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2);
            }
            paint.setAntiAlias(true);
            int width = getWidth();
            int height = getHeight();

            cx = width /2;
            cy = height /2;
            r = (Math.min(width, height) - paint.getStrokeWidth())/2f;
        }

        if (fillContent) {
            canvas.drawCircle(cx, cy, r, paint);
        } else {
            canvas.drawCircle(cx, cy, r, paint);
        }
    }
}
