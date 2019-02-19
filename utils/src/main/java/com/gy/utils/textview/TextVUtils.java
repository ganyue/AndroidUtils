package com.gy.utils.textview;

import android.text.TextPaint;

/**
 * created by yue.gan 19-2-19
 */
public class TextVUtils {

    public static int maxSingleLineTextSize (int maxTextViewWidth, String ...texts) {
        int textSize = 22;
        int step = 2;
        TextPaint textPaint = new TextPaint();

        int tw = 0;
        int flag = -2;//-2初始状态， -1小于view宽度， 1大于view宽度， 0计算完毕
        int i = 0;
        while(i++ < 10) {
            textPaint.setTextSize(textSize);

            for (String text: texts) {
                int textWidth = (int) textPaint.measureText(text);
                if (textWidth > tw) {
                    tw = textWidth;
                }
            }

            if (tw > maxTextViewWidth) {
                textSize -= step;
                if (flag == -2) {
                    flag = 1;
                } else if (flag == -1) {
                    return textSize;
                }
            } else {
                if (flag == -2) {
                    flag = -1;
                } else if (flag == 1) {
                    return  textSize;
                }
                textSize += step;
            }
        }
        return textSize;
    }
}
