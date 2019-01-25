package com.gy.widget.textview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.gy.widget.R;

import java.util.ArrayList;
import java.util.List;

/**
 * created by yue.gan 19-1-19
 */
public class PinyinTextView extends View{

    private float mTextSizePinyin = 10;         // 拼音字体大小
    private float mTextSizeChinese = 20;        // 汉字字体大小
    private int mTextColorPinyin = Color.BLACK; // 拼音颜色
    private int mTextColorChinese = Color.BLACK;// 汉字颜色
    private TextPaint mTPaintPinyin;            // 拼音画笔
    private TextPaint mTPaintChinese;           // 汉字画笔
    private float mHeightPinyin;                // 拼音行高
    private float mHeightChinese;               // 汉字行高
    private List<Pinyin> mTexts;                // 拼音和对应的汉字
    private List<Float> mTextWidth;             // 文字宽度
    private List<Integer> mLineOffset;          // 每行首个文字在mTexts中的位置
    private List<String> mLinePinyin;


    public PinyinTextView(Context context) {
        super(context);
    }

    public PinyinTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PinyinTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PinyinTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PinyinTextView);
        mTextSizePinyin = typedArray.getDimensionPixelSize(R.styleable.PinyinTextView_textSizePinyin, 10);
        mTextSizeChinese = typedArray.getDimensionPixelSize(R.styleable.PinyinTextView_textSizeChinese, 20);
        mTextColorPinyin = typedArray.getColor(R.styleable.PinyinTextView_textColorPinyin, Color.BLACK);
        mTextColorChinese = typedArray.getColor(R.styleable.PinyinTextView_textColorChinese, Color.BLACK);
        typedArray.recycle();

        float densitiy = getResources().getDisplayMetrics().density;
        mTPaintPinyin = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTPaintPinyin.density = densitiy;
        mTPaintPinyin.setTextSize(mTextSizePinyin);
        mTPaintPinyin.setColor(mTextColorPinyin);

        mTPaintChinese = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTPaintChinese.density = densitiy;
        mTPaintChinese.setTextSize(mTextSizeChinese);
        mTPaintChinese.setColor(mTextColorChinese);

        Paint.FontMetrics pinyinFontMetrics = mTPaintPinyin.getFontMetrics();
        Paint.FontMetrics chineseFontMetrics = mTPaintChinese.getFontMetrics();
        mHeightPinyin = pinyinFontMetrics.bottom - pinyinFontMetrics.top;
        mHeightChinese = chineseFontMetrics.bottom - pinyinFontMetrics.top;

        mTexts = new ArrayList<>();
        mLineOffset = new ArrayList<>();
        mTextWidth = new ArrayList<>();
    }

    public void setText (List<Pinyin> pinyins) {
        if (pinyins == null || pinyins.size() <= 0) return;
        mTexts = pinyins;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            if (mTexts.size() <= 0) {
                heightSize = (int) (mHeightPinyin + getPaddingTop() + getPaddingBottom());
            } else {
                calculateLines(widthSize);
                int totalHeight = (int) (mHeightPinyin * mLineOffset.size() +
                        mHeightChinese * mLineOffset.size() + getPaddingTop() + getPaddingBottom());
                if (heightMode == MeasureSpec.AT_MOST) {
                    heightSize = Math.min(totalHeight, heightSize);
                } else {
                    heightSize = totalHeight;
                }
            }
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    private void calculateLines (int widthSize) {
        int totalWidth = 0;
        mLineOffset.clear();
        mLineOffset.add(0);
        int len = mTexts.size();
        for (int i = 0; i < len; i++) {
            Pinyin pinyin = mTexts.get(i);
            float pinyinWidth = mTPaintPinyin.measureText(pinyin.pinyin);
            mTextWidth.add(pinyinWidth);
            totalWidth += pinyinWidth;
            if (totalWidth > widthSize) {
                totalWidth = (int) pinyinWidth;
                mLineOffset.add(i);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float x = 0;
        float y = 0;
        int line = 0;
        for (int i = 0; i < mTexts.size(); i++) {
            if (mLineOffset.indexOf(i) != -1) {
                line = mLineOffset.indexOf(i);
                x = 0;
                y = line * (mHeightChinese + mHeightPinyin);
            }
            Pinyin pinyin = mTexts.get(i);
            canvas.drawText(pinyin.pinyin, x, y + mHeightPinyin, mTPaintPinyin);
            canvas.drawText(""+pinyin.chinese, x, y + mHeightPinyin + mHeightChinese, mTPaintChinese);
            x += mTextWidth.get(i);
        }
    }

    class Pinyin {
        public String pinyin;
        public char chinese;
        public Pinyin(String pinyin, char chinese) {
            if (TextUtils.isEmpty(pinyin)) pinyin = "";
            this.pinyin = formatPinyin(pinyin);
            this.chinese = chinese;
        }

        /**
         * <p>格式化拼音
         * <p>拼音声母加韵母最长是6个字符，为了方便显示和计算，直接全都转换为6个长度
         * @param pinyin .
         * @return 格式化后的拼音， 如："hao" 返回值：" hao  "
         */
        private String formatPinyin (String pinyin) {
            switch (pinyin.length()) {
                case 0:
                    return "      ";
                case 1:
                    return "  " + pinyin + "   ";
                case 2:
                    return "  " + pinyin + "  ";
                case 3:
                    return " " + pinyin + "  ";
                case 4:
                    return " " + pinyin + " ";
                case 5:
                    return pinyin + " ";
                case 6:
                    return pinyin;
            }
            return pinyin;
        }
    }
}
