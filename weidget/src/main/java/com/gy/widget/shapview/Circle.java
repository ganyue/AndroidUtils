package com.gy.widget.shapview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ganyu on 2016/5/15.
 *
 */
public class Circle extends View {


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
            paint = new Paint();;
            paint.setColor(color);
            if (fillContent) {
                paint.setStyle(Paint.Style.FILL);
            } else {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2);
            }
            paint.setAntiAlias(true);
        }
        int width = getWidth();
        int height = getHeight();
        if (fillContent) {
            canvas.drawCircle(width/2, height/2, Math.min(width, height)/2 - 3*paint.getStrokeWidth(), paint);
        } else {
            canvas.drawCircle(width/2, height/2, Math.min(width, height)/2 - paint.getStrokeWidth(), paint);
        }
    }
}
