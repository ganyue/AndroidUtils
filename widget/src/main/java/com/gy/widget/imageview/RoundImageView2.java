package com.gy.widget.imageview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * Created by yue.gan on 2017/3/15.
 *
 */

public class RoundImageView2 extends RatioImageView {
    public RoundImageView2(Context context) {
        super(context);
    }

    public RoundImageView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundImageView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private float roundRectRadius = 20f;

    public void setRoundRectRadus (float radius) {
        roundRectRadius = radius;
    }

    private Path path;
    @Override
    protected void onDraw(Canvas canvas) {
        if (path == null) {
            path = new Path();
            int width = getWidth();
            int height = getHeight();
            int radius = Math.min(width/2, height/2);
            path.addCircle(width/2, height/2, radius, Path.Direction.CW);
        }
        canvas.save();
        canvas.clipPath(path);
        super.onDraw(canvas);
        canvas.restore();
    }
}
