package com.gy.widget.imageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.gy.widget.R;

/**
 * Created by ganyu on 2016/9/2.
 *
 * <p>用宽除以高做ratio，作为属性传入后可以按比例自适应高度</p>
 */
public class RatioImageView extends ImageView {
    public RatioImageView(Context context) {
        super(context);
        init(context, null);
    }

    public RatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private float ratio;
    private void init (Context context, AttributeSet attrs) {
        if (attrs == null) return;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioImageView);
        ratio = typedArray.getFloat(R.styleable.RatioImageView_ratio, 0);
        typedArray.recycle();
    }

    public void setRatio (float ratio) {
        this.ratio = ratio;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (ratio <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = (int) (1f * width / ratio);
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, widthMode));
    }
}
