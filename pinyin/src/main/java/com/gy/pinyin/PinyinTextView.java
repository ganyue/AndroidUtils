package com.gy.pinyin;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * created by yue.gan 19-1-19
 */
public class PinyinTextView extends View{

    private float mTextSizePinyin;              // 拼音字体大小
    private float mTextSizeChinese;             // 汉字字体大小
    private float mTextSpaceHorizontal;         // 最小字间距（横向字与字）
    private float mTextSpaceVertical;           // 最小字间距（拼音和汉字）
    private float mTextSpaceLine;               // 行间距
    private int mTextColorPinyin = Color.BLACK; // 拼音颜色
    private int mTextColorChinese = Color.BLACK;// 汉字颜色
    private TextPaint mTPaintPinyin;            // 拼音画笔
    private TextPaint mTPaintChinese;           // 汉字画笔
    private float mHeightPinyin;                // 拼音行高
    private float mHeightChinese;               // 汉字行高
    private float mWidthPinyin;                 // 拼音单个字符宽
    private float mWidthChinese;                // 单个汉字宽
    private float mSingleChineseWidth;          // 显示一个汉字需要的长度（拼音和汉字取大的，加上最小字间距）
    private float mSingleChineseHeight;         // 显示一个汉字需要的宽度（拼音和汉字高度，加上拼音和汉字间距）
    private float mPinyinDrawOffsetH;           // 拼音绘制水平偏移（拼音和汉字不一定一样长，要居中需要偏移）
    private float mChineseDrawOffsetH;          // 汉字绘制水平偏移（拼音和汉字不一定一样长，要居中需要偏移）
    private List<Pinyin> mTexts;                // 拼音和对应的汉字
    private List<Integer> mColors;              // 每个字对应的颜色
    private List<Integer> mLineOffset;          // 每行首个文字在mTexts中的位置

    public PinyinTextView(Context context) {
        super(context);
        init(context, null);
    }

    public PinyinTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PinyinTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init (Context context, AttributeSet attrs) {
        float density = getResources().getDisplayMetrics().density;
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PinyinTextView);
            mTextSizePinyin = typedArray.getDimensionPixelSize(R.styleable.PinyinTextView_textSizePinyin, (int) (12*density));
            mTextSizeChinese = typedArray.getDimensionPixelSize(R.styleable.PinyinTextView_textSizeChinese, (int) (20*density));
            mTextSpaceHorizontal = typedArray.getDimensionPixelSize(R.styleable.PinyinTextView_textSpaceHorizontal, (int) (2*density));
            mTextSpaceVertical = typedArray.getDimensionPixelSize(R.styleable.PinyinTextView_textSpaceVertical, (int) (4*density));
            mTextSpaceLine = typedArray.getDimensionPixelSize(R.styleable.PinyinTextView_textSpaceLine, (int) (4*density));
            mTextColorPinyin = typedArray.getColor(R.styleable.PinyinTextView_textColorPinyin, Color.BLACK);
            mTextColorChinese = typedArray.getColor(R.styleable.PinyinTextView_textColorChinese, Color.BLACK);
            typedArray.recycle();
        }

        mTPaintPinyin = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTPaintPinyin.density = density;
        mTPaintPinyin.setTextSize(mTextSizePinyin);
        mTPaintPinyin.setColor(mTextColorPinyin);

        mTPaintChinese = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTPaintChinese.density = density;
        mTPaintChinese.setTextSize(mTextSizeChinese);
        mTPaintChinese.setColor(mTextColorChinese);

        Paint.FontMetrics pinyinFontMetrics = mTPaintPinyin.getFontMetrics();
        Paint.FontMetrics chineseFontMetrics = mTPaintChinese.getFontMetrics();
        mHeightPinyin = pinyinFontMetrics.bottom - pinyinFontMetrics.top;
        mHeightChinese = chineseFontMetrics.bottom - pinyinFontMetrics.top;
        mWidthPinyin = mTPaintPinyin.measureText("zhuong");
        mWidthChinese = mTPaintChinese.measureText("一");
        mSingleChineseWidth = Math.max(mWidthPinyin, mWidthChinese) + mTextSpaceHorizontal;
        mSingleChineseHeight = mHeightChinese + mHeightPinyin + mTextSpaceVertical;
        if (mWidthPinyin > mWidthChinese) {
            mPinyinDrawOffsetH = 0;
            mChineseDrawOffsetH = (mWidthPinyin - mWidthChinese) / 2;
        } else {
            mChineseDrawOffsetH = 0;
            mPinyinDrawOffsetH = (mWidthChinese - mWidthPinyin) / 2;
        }

        mTexts = new ArrayList<>();
        mLineOffset = new ArrayList<>();
    }

    public void setText (List<Pinyin> pinyins) {
        if (pinyins == null || pinyins.size() <= 0 || pinyins == mTexts) return;
        // 判别是否需要重新计算宽高
        if (pinyins == mTexts || isLengthEquals(pinyins)) {
            mTexts = pinyins;
            post(new Runnable() {
                @Override
                public void run() {
                    postInvalidate();
                }
            });
            return;
        }
        // 需要重新计算宽高
        mTexts = pinyins;
        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
    }

    public void setText (List<Pinyin> pinyins, List<Integer> colors) {
        if (pinyins == null || pinyins.size() <= 0) return;
        // 判别是否需要重新计算宽高
        if (pinyins == mTexts || isLengthEquals(pinyins)) {
            mTexts = pinyins;
            mColors = colors;
            post(new Runnable() {
                @Override
                public void run() {
                    postInvalidate();
                }
            });
            return;
        }
        // 需要重新计算宽高
        mTexts = pinyins;
        mColors = colors;
        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
    }

    private boolean isLengthEquals (List<Pinyin> pinyins) {
        if (pinyins == null || pinyins.size() != mTexts.size()) return false;
        for (int i = 0; i < mTexts.size(); i++) {
            if (!mTexts.get(i).lengthEquals(pinyins.get(i))) return false;
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (mTexts == null || mTexts.size() <= 0) {
            widthSize = (int) mSingleChineseWidth + getPaddingTop() + getPaddingBottom();
            heightSize = (int) mSingleChineseHeight + getPaddingLeft() + getPaddingRight();
        } else {
            //返回值是最长的一行的宽度，宽度不会超过最大宽度，所以如果非MeasureSpec.EXACTLY的话就用这个做宽度
            int maxLineWidth = (int) calculateLines(widthSize);
            if (widthMode != MeasureSpec.EXACTLY) {
                widthSize = maxLineWidth;
            }

            if (heightMode != MeasureSpec.EXACTLY) {
                //算出需要显示的行数，计算出高度（每行拼音和汉字高度+拼音和汉字间距）× 行数 + 上下padding
                int lines = mLineOffset.size();
                int totalHeight = (int)((mSingleChineseHeight + mTextSpaceLine) * lines + getPaddingTop() + getPaddingBottom());
                if (heightMode == MeasureSpec.AT_MOST) {
                    heightSize = Math.min(totalHeight, heightSize);
                } else {
                    heightSize = totalHeight;
                }
            }
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    private float calculateLines (int widthSize) {
        int padding = getPaddingLeft() + getPaddingRight();
        float totalWidth = 0;
        float maxDrawWidth = mSingleChineseWidth;
        float drawWidth = widthSize - padding;
        mLineOffset.clear();
        mLineOffset.add(0);
        int len = mTexts.size();
        for (int i = 0; i < len; i++) {
            Pinyin pinyin = mTexts.get(i);
            //换行,记录这行起始位置,如果是最长的一行,记录这行长度用做返回值
            if ("\n".equals(pinyin.chinese)) {
                if (totalWidth > maxDrawWidth) {
                    maxDrawWidth = totalWidth + padding;
                }
                totalWidth = 0;
                mLineOffset.add(i);
                continue;
            }
            float width = pinyin.isChinese? mSingleChineseWidth: mTPaintChinese.measureText(pinyin.chinese);
            totalWidth += width;

            //由于加上当前这个字符宽度后大于最大显示宽度,记录这行起始位置,如果是最长的一行,记录这行长度用做返回值
            if (totalWidth > drawWidth) {
                if (totalWidth > maxDrawWidth) {
                    maxDrawWidth = totalWidth + padding - width;
                }
                totalWidth = width;
                mLineOffset.add(i);
                continue;
            }

            //记录最长行宽
            if (totalWidth > maxDrawWidth) {
                maxDrawWidth = totalWidth + padding;
            }
        }
        return maxDrawWidth;
    }

    boolean textDrawDone = false;
    @Override
    protected void onDraw(Canvas canvas) {
        if (mTexts.size() <= 0) return;

        float x = 0;
        float y = 0;
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        boolean hasColors = mColors != null && mColors.size() == mTexts.size();
        for (int i = 0; i < mTexts.size(); i++) {
            int indexInLine  = mLineOffset.indexOf(i);
            if (indexInLine != -1) {
                x = paddingLeft;
                y = indexInLine * mSingleChineseHeight + indexInLine * mTextSpaceLine + paddingTop;
            }
            Pinyin pinyin = mTexts.get(i);
            if (pinyin.isChinese) {
                if (hasColors) {
                    int color = mColors.get(i);
                    mTPaintPinyin.setColor(color);
                    mTPaintChinese.setColor(color);
                } else {
                    mTPaintPinyin.setColor(mTextColorPinyin);
                    mTPaintChinese.setColor(mTextColorChinese);
                }
                float pinyinOffsetH = mPinyinDrawOffsetH + (mWidthPinyin - mTPaintPinyin.measureText(pinyin.pinyin)) / 2;
                canvas.drawText(pinyin.pinyin, x + pinyinOffsetH, y + mHeightPinyin, mTPaintPinyin);
                canvas.drawText(pinyin.chinese, x + mChineseDrawOffsetH, y + mSingleChineseHeight, mTPaintChinese);
                x += mSingleChineseWidth;
            } else {
                if ("\n".equals(pinyin.chinese)) continue;
                mTPaintPinyin.setColor(mTextColorPinyin);
                mTPaintChinese.setColor(mTextColorChinese);
                float w = mTPaintChinese.measureText(pinyin.chinese);
                canvas.drawText(pinyin.chinese, x, y + mSingleChineseHeight, mTPaintChinese);
                x += w + mTextSpaceHorizontal;
            }
        }
        textDrawDone = true;
    }

    public static class Pinyin {
        public String pinyin;
        public String chinese;
        public boolean isChinese;
        public boolean isChinesePunc;
        public Pinyin(String pinyin, char chinese) {
            if (TextUtils.isEmpty(pinyin)) pinyin = "";
            this.pinyin = formatPinyin(pinyin);
            this.chinese = ""+chinese;
            isChinese = isChineseByBlock(chinese);
            isChinesePunc = !isChinese && isChinesePunctuation(chinese);
        }

        private String formatPinyin (String pinyin) {
            if ("none".equals(pinyin)) return "";
            return pinyin;
        }

        /**
         * 判别字符是否是汉字
         */
        boolean isChineseByBlock(char c) {
            if (Build.VERSION.SDK_INT >= 24) {
                Character.UnicodeScript sc = Character.UnicodeScript.of(c);
                return sc == Character.UnicodeScript.HAN;
            }
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
            return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C
                    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D
                    || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT;
        }

        /**
         * 判别字符是否是中文标点符号
         */
        public boolean isChinesePunctuation(char c) {
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
            return ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                    || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                    || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                    || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS
                    || ub == Character.UnicodeBlock.VERTICAL_FORMS;
        }

        boolean lengthEquals (Pinyin pinyin) {
            return (isChinese && pinyin.isChinese) // 同为中文
                    || (isChinesePunc && pinyin.isChinesePunc) // 同为中文符号
                    || chinese.equals(pinyin.chinese); // 字符相同
        }
    }


}
