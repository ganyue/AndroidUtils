package com.gy.widget.seekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.gy.widget.R;

public class TimerSeekBar extends View implements OnTouchListener {
	private float nowX;
	private OnChangedListener listener;
	private int split = 2;
	private float lineWidth;
	private float lineHeight;
	private int lineColor;
	private float normalPointR;
	private int normalPointColor;
	private float movePointR;
	private int movePointColor;
	private float maxPadding;
	private float textMarginTop;
	private float textSizeNormal;
	private float textSizeSelected;
	private int textColorNormal;
	private int textColorSelected;
	private String[] splitDes;
	private String[] defaultSplitDes;
	private int index;
	private Typeface fontBold = Typeface.create(Typeface.SANS_SERIF,
			Typeface.BOLD);
	private Typeface fontNormal = Typeface.create(Typeface.SANS_SERIF,
			Typeface.NORMAL);

	public TimerSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray array = context.obtainStyledAttributes(attrs,
				R.styleable.TimerSeekBar);
		lineWidth = array.getDimension(R.styleable.TimerSeekBar_line_width, 720);
		lineHeight = array
				.getDimension(R.styleable.TimerSeekBar_line_height, 10);
		lineColor = array
				.getColor(R.styleable.TimerSeekBar_line_color, 0XEED5D2);
		normalPointR = array.getDimension(
				R.styleable.TimerSeekBar_normal_point_r, 20);
		normalPointColor = array.getColor(
				R.styleable.TimerSeekBar_normal_point_color, 0XEEC591);
		movePointR = array.getDimension(R.styleable.TimerSeekBar_move_point_r,
				30);
		movePointColor = array.getColor(
				R.styleable.TimerSeekBar_move_point_color, 0XEE9A00);
		textMarginTop = array.getDimension(
				R.styleable.TimerSeekBar_text_marginTop, 0);
		textSizeNormal = array.getDimensionPixelSize(
				R.styleable.TimerSeekBar_text_size_normal, 20);
		textSizeSelected = array.getDimensionPixelSize(
				R.styleable.TimerSeekBar_text_size_selected, 20);
		textColorNormal = array.getColor(
				R.styleable.TimerSeekBar_text_color_normal, 0X000000);
		textColorSelected = array.getColor(
				R.styleable.TimerSeekBar_text_color_selected, 0X000000);
		split = array.getInt(R.styleable.TimerSeekBar_split, 3);
		splitDes = array.getString(R.styleable.TimerSeekBar_text_splitDes)
				.split("#");
		array.recycle();
		init();
	}

	public void moveTo(int index) {
		this.index = index;
		nowX = lineWidth / split * index;
	}

	private void init() {
		maxPadding = Math.max(normalPointR, movePointR);
		maxPadding = Math.max(maxPadding, lineHeight);
		// maxPadding = 50;
		setOnTouchListener(this);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int measuredHeight = measureHeight(heightMeasureSpec);
		int measuredWidth = measureWidth(widthMeasureSpec);
		setMeasuredDimension(measuredWidth, measuredHeight);
	}

	private int measureWidth(int widthMeasureSpec) {

		return (int) (lineWidth + maxPadding * 2);
	}

	private int measureHeight(int heightMeasureSpec) {

		return (int) (maxPadding * 3 + textMarginTop);
	}

	public String getSplitText(int index) {
		if (splitDes != null && splitDes.length == split + 1) {
			return splitDes[index];
		}
		return defaultSplitDes[index];
	}

	public int getSplit() {
		return split;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		// 画背景线
		paint.setColor(lineColor);
		paint.setStrokeWidth(lineHeight);
		paint.setStyle(Style.FILL);
		canvas.drawLine(maxPadding, maxPadding * 1f + textMarginTop
				+ maxPadding, maxPadding + lineWidth, maxPadding * 1f
				+ textMarginTop + maxPadding, paint);
		// 画分割点
		paint.setStyle(Style.FILL);
		float perWidth = lineWidth / split;
		String str = "";
		float w = 0;
		for (int i = 0; i <= split; i++) {
			paint.setColor(normalPointColor);
			canvas.drawCircle(perWidth * i + maxPadding, maxPadding * 1f
					+ textMarginTop + maxPadding, normalPointR, paint);
			// 写字
			if (splitDes != null && splitDes.length == split + 1) {
				str = splitDes[i];
			} else {
				if (defaultSplitDes == null) {
					defaultSplitDes = new String[split + 1];
				}
				str = "测试" + (i + 1);
				defaultSplitDes[i] = str;
			}
			if (i == index) {
				paint.setTextSize(textSizeSelected);
				paint.setColor(textColorSelected);
				paint.setTypeface(fontBold);
			} else {
				paint.setTextSize(textSizeNormal);
				paint.setColor(textColorNormal);
				paint.setTypeface(fontNormal);
			}
			w = paint.measureText(str);

			if (i == 0) {
				canvas.drawText(str, 0, textMarginTop, paint);
			} else if (i == split) {
				canvas.drawText(str, getWidth() - w, textMarginTop, paint);
			} else {
				canvas.drawText(str, perWidth * i + maxPadding - w / 2,
						textMarginTop, paint);
			}
		}
		// 画拖动 的圆点
		paint.setColor(movePointColor);
		paint.setStyle(Style.FILL);
		if (nowX < maxPadding + movePointR) {
			canvas.drawCircle(maxPadding, maxPadding * 1f + textMarginTop
					+ maxPadding, movePointR, paint);
		} else if (nowX > maxPadding + lineWidth - movePointR) {
			canvas.drawCircle(maxPadding + lineWidth, maxPadding * 1f
					+ textMarginTop + maxPadding, movePointR, paint);
		} else {
			canvas.drawCircle(nowX + maxPadding, maxPadding * 1f
					+ textMarginTop + maxPadding, movePointR, paint);
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (event.getX() > lineWidth + maxPadding
					|| event.getY() > maxPadding * 1f + textMarginTop
							+ maxPadding * 2
					|| event.getY() < maxPadding * 1f + textMarginTop) {
				return false;
			} else {
				nowX = event.getX();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			nowX = event.getX();
			break;
		case MotionEvent.ACTION_UP:
			float perWidth = lineWidth / (split * 2);
			index = (int) ((event.getX() - maxPadding) / perWidth);
			index = index % 2 == 0 ? index / 2 : index / 2 + 1;
			if (index < 0) {
				index = 0;
			}
			if (index > split) {
				index = split;
			}
			nowX = index * perWidth * 2;
			if (event.getX() > lineWidth) {
				nowX = lineWidth;
			}

			if (listener != null) {
				listener.OnChanged(TimerSeekBar.this, index);
			}
		}
		invalidate();
		return true;

	}

	public void setOnChangedListener(OnChangedListener listener) {
		this.listener = listener;
	}

	public interface OnChangedListener {
		public void OnChanged(TimerSeekBar stepSeekBar, int index);
	}
}
