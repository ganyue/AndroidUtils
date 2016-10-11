package com.gy.widget.viewpager.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gy.widget.R;
import com.gy.widget.imageview.RatioImageView;
import com.gy.widget.indicator.HCircleIndicator;
import com.gy.widget.viewpager.ScrollSpeedAvailablePager;

/**
 * Created by ganyu on 2016/10/8.
 *
 */
public class FirstStartGuideView extends RelativeLayout {

    public FirstStartGuideView(Context context) {
        super(context);
        init(context, null);
    }

    public FirstStartGuideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private float ratio = 0;
    private ScrollSpeedAvailablePager mViewPager;
    private HCircleIndicator indicator;
    private void init (Context context, AttributeSet attrs) {
        if (attrs == null) return;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioImageView);
        ratio = typedArray.getFloat(R.styleable.RatioImageView_ratio, 0);
        typedArray.recycle();

        mViewPager = new ScrollSpeedAvailablePager(context);
        indicator = new HCircleIndicator(context);

        mViewPager.setAdapter(new MPagerAdapter());
        mViewPager.setScrollSpeed(180);

        LayoutParams pagerParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        LayoutParams indicatorParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        indicatorParams.addRule(CENTER_HORIZONTAL);
        indicatorParams.addRule(ALIGN_PARENT_BOTTOM);
        indicatorParams.bottomMargin = (int) getResources().getDimension(R.dimen.widget_normal_padding);

        addView(mViewPager, pagerParams);
        addView(indicator, indicatorParams);
    }

    /**
     * 必须是宽度固定，且ratio是宽/高的结果
     */
    public void setRatio (float ratio) {
        this.ratio = ratio;
    }

    /**
     * 如果传入了ratio参数，（必须是宽度固定，且ratio是宽/高的结果），重新按比例适应高度
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (ratio <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = (int) (1f * width / ratio);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, widthMode);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    private int count;
    /**
     * 必须调用setCount 方法才能刷新banner
     */
    public void setCount (int count) {
        this.count = count;
        indicator.setCount(count);

        if (bannerCallback != null) {
            int margin = bannerCallback.getIndicatorBottomMargin();
            if (margin > 0) {
                LayoutParams params =
                        (LayoutParams) indicator.getLayoutParams();
                params.bottomMargin = margin;
                indicator.setLayoutParams(params);
            }
        }

        mViewPager.getAdapter().notifyDataSetChanged();
        mViewPager.addOnPageChangeListener(onPageChangeListener);
    }

    private BannerCallback bannerCallback;
    public void setBannerCallback (BannerCallback callback) {
        bannerCallback = callback;
    }

    /************************** pager inner view click listener ************************************/
    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (bannerCallback != null) {
                bannerCallback.onItemClick((ImageView) v, mViewPager.getCurrentItem());
            }
        }
    };

    /***************************** view pager scroll listener ***************************************/
    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (count > 0) {
                indicator.scrollToPosition(position, positionOffset);
            }
        }

        @Override
        public void onPageSelected(int position) {
            indicator.setCurrentIndicatorIndex(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE && count > 0) {
                indicator.setCurrentIndicatorIndex(mViewPager.getCurrentItem());
            }
        }
    };

    /**
     * 首尾各加了一个实现首尾平滑切换
     */
    /***************************** view pager adapter ***************************************/
    private class MPagerAdapter extends PagerAdapter {

        private SparseArray<RatioImageView> views;

        public MPagerAdapter() {
            super();
            views = new SparseArray<>();
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            RatioImageView ratioImageView = views.get(position);
            if (ratioImageView == null) {
                ViewGroup.LayoutParams imgVParams = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                ratioImageView = new RatioImageView(getContext());
                ratioImageView.setRatio(ratio);
                ratioImageView.setLayoutParams(imgVParams);
                views.put(position, ratioImageView);
                ratioImageView.setOnClickListener(onClickListener);

                if (bannerCallback != null) {
                    bannerCallback.displayImage(ratioImageView, position);
                }
            }
            container.addView(ratioImageView);
            return ratioImageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }
    }
}
