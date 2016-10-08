package com.gy.widget.drawable;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

import com.gy.widget.R;

import java.lang.ref.WeakReference;

/**
 * Created by ganyu on 2016/10/8.
 * 刷新用的header或footer
 * TODO 尚未完成，还没想好怎么搞
 */
public class SunRefreshDrawable extends BaseRefreshDrawable{

    private ObjectAnimator objectAnimator;
    private Animation rotateAnim;
    private WeakReference<View> parentV;
    private Bitmap mSky;
    private Bitmap mTown;
    private Bitmap mSun;
    private int mWidth;
    private int mHeight;
    private float mYStep;
    private float mXStep;
    private int prevPercent = 0;
    private int percent = 0;
    private float rotate;

    public SunRefreshDrawable(View parent) {
        parentV = new WeakReference<>(parent);
    }

    private void createBitmaps() {
        if (getContext() == null) return;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int sunSize = (int) (getContext().getResources().getDisplayMetrics().density * 20);

        mSky = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.img_refresh_drawable_sky, options);
        mSky = Bitmap.createScaledBitmap(mSky, mWidth, mHeight, true);
        mTown = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.img_refresh_drawable_buildings, options);
        mTown = Bitmap.createScaledBitmap(mTown, mWidth, (int) (mHeight * 0.6), true);
        mSun = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.img_refresh_drawable_sun, options);
        mSun = Bitmap.createScaledBitmap(mSun, sunSize, sunSize, true);
    }

    private Context getContext () {
        return parentV == null? null: parentV.get().getContext();
    }

    @Override
    public void draw(Canvas canvas) {
        if (mWidth == 0) {
            mWidth = getBounds().width();
            mHeight = getBounds().height();
            mXStep = mWidth/100f;
            mYStep = mHeight/100f;
            createBitmaps();
        }
        canvas.drawBitmap(mSky, 0, 0, null);
        canvas.drawBitmap(mTown, 0, mHeight * 0.4f, null);

        int dp = percent - prevPercent;
        Matrix matrix = new Matrix();
        matrix.postTranslate(dp * mXStep, dp * mYStep);
        matrix.postRotate(rotate);
        canvas.drawBitmap(mSun, matrix, null);
    }

    @Override
    public void setAlpha(int alpha) {
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void start() {

        prevPercent = 0;
        percent = 0;

        /**旋转*/
        if (rotateAnim == null) {
            rotateAnim = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    rotate = 360 * interpolatedTime;
                    invalidateSelf();
                }
            };
            rotateAnim.setRepeatCount(Animation.INFINITE);
            rotateAnim.setRepeatMode(Animation.RESTART);
            rotateAnim.setInterpolator(new LinearInterpolator());
            rotateAnim.setDuration(1000);
        }

        if (parentV != null) {
            rotateAnim.reset();
            parentV.get().startAnimation(rotateAnim);
        }

        /**移动*/
        if (Build.VERSION.SDK_INT >= 11) {
            if (objectAnimator == null) {
                objectAnimator = ObjectAnimator.ofInt(this, "percent", 0, 100);
                objectAnimator.setDuration(5);
                objectAnimator.setRepeatCount(0);
                objectAnimator.setInterpolator(new LinearInterpolator());
            }
            if (!objectAnimator.isRunning()) {
                objectAnimator.start();
            }
        }
    }

    @Override
    public void stop() {

        prevPercent = 0;
        percent = 0;

        if (parentV != null) {
            parentV.get().clearAnimation();
        }

        if (objectAnimator != null && Build.VERSION.SDK_INT >= 11) {
            if (objectAnimator.isRunning()) {
                objectAnimator.end();
            }
        }
    }

    @Override
    public boolean isRunning() {
        return false;
    }
}
