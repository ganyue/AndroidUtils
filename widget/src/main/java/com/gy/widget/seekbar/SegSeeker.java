package com.gy.widget.seekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import com.gy.widget.R;

/**
 * Created by yue.gan on 2016/11/8.
 *
 */
public class SegSeeker extends FrameLayout{

    private SeekBar.OnSeekBarChangeListener delegate;
    private SeekBar seekBar;
    private Paint textPaint;
    private Paint pointPaint;
    private int unselectedTextSize = 10;
    private int selectedTextSize = 12;
    private int unselectedTextColor = Color.parseColor("#474747");
    private int selectedTextColor = Color.BLACK;
    private String[] segs;

    public SegSeeker(Context context) {
        super(context);
        init(context, null);
    }

    public SegSeeker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init (Context context, AttributeSet attrs) {
        View rootV = LayoutInflater.from(context).inflate(R.layout.view_timer_seeker, this, false);
        seekBar = (SeekBar) rootV.findViewById(R.id.sb_progress);
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        addView(rootV);
        textPaint = new Paint();
        textPaint.setTextSize(10);
        textPaint.setColor(unselectedTextColor);

        pointPaint = new Paint();
        pointPaint.setColor(Color.parseColor("#ffd200"));
        pointPaint.setStyle(Paint.Style.FILL);
        if (attrs == null) return;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SegSeeker);
        String segStr = typedArray.getString(R.styleable.SegSeeker_segs);
        typedArray.recycle();

        if (TextUtils.isEmpty(segStr)) {
            segStr = "-15分钟-30分钟-45分钟-60分钟";
        }

        segs = segStr.split("-");
    }

    float seekX, seekY, seekWidth, seekHeight, pointY, segLen, thumbRadius, pointRadius;

    @Override
    protected void onDraw(Canvas canvas) {
        if (seekX <= 0) {
            float density = getResources().getDisplayMetrics().density;
            seekX = seekBar.getX();
            seekY = seekBar.getY();
            seekWidth = seekBar.getWidth();
            seekHeight = seekBar.getHeight();
            pointY = seekHeight/2 + seekY;
            pointRadius = density * 3;
            thumbRadius = density * 5;
            segLen = seekWidth/segs.length;
        }

        for (int i = 0; i < segs.length; i++) {
            float pointX = seekX + i * segLen;
            //绘制圆点
            canvas.drawCircle(pointX, pointY, pointRadius, pointPaint);

            float textX = pointX;
            float textY = seekY - thumbRadius - pointRadius;
            //绘制文字
            canvas.drawText(segs[i], textX , textY, textPaint);
        }

    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener l) {
        delegate = l;
    }

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (delegate != null) delegate.onProgressChanged(seekBar, progress, fromUser);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            if (delegate != null) delegate.onStartTrackingTouch(seekBar);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (delegate != null) delegate.onStopTrackingTouch(seekBar);
        }
    };
}
