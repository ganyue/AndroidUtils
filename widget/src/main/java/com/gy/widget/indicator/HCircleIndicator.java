package com.gy.widget.indicator;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.gy.widget.shapview.Circle;



/**
 * Created by ganyu on 2016/10/9.
 *
 */
public class HCircleIndicator extends  HorizontalIndicator{
    public HCircleIndicator(Context context) {
        super(context);
    }

    public HCircleIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCount (int count) {
        float density = getResources().getDisplayMetrics().density;
        HorizontalIndicator.LayoutParams indicatorParams = new HorizontalIndicator.LayoutParams(
                ((int)density * 12), ((int)density * 8));
        for (int i = 0; i < count; i++) {
            Circle circle = new Circle(getContext());
            circle.setColor(Color.WHITE);
            circle.setLayoutParams(indicatorParams);
            addView(circle);
        }

        Circle circle = new Circle(getContext());
        circle.setLayoutParams(indicatorParams);
        circle.setFillContent(true);
        circle.setColor(Color.WHITE);
        addView(circle);
    }

    public void setCount (int count, int colorNormal, int colorSelected, int itemWidth, int itemHeight) {
        float density = getResources().getDisplayMetrics().density;
        HorizontalIndicator.LayoutParams indicatorParams = new HorizontalIndicator.LayoutParams(
                ((int)density * itemWidth), ((int)density * itemHeight));
        for (int i = 0; i < count; i++) {
            Circle circle = new Circle(getContext());
            circle.setColor(colorNormal);
            circle.setFillContent(true);
            circle.setLayoutParams(indicatorParams);
            addView(circle);
        }

        Circle circle = new Circle(getContext());
        circle.setLayoutParams(indicatorParams);
        circle.setFillContent(true);
        circle.setColor(colorSelected);
        addView(circle);
    }
}
