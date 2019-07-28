package com.gy.widget.floatwindow;

import android.app.KeyguardManager;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.gy.widget.R;
import com.gy.widget.imageview.RoundRectImageView;

import java.lang.ref.WeakReference;

/**
 * Created by yue.gan on 2017/4/24.
 *
 */

public class ScreenLockFloatWindow {
    private WeakReference<Context> mContext;
    private WindowManager windowManager;
    private KeyguardManager keyguardManager;
    private KeyguardManager.KeyguardLock keyguardLock;
    private float minUnlockDy;

    private WindowManager.LayoutParams params;
    private View mView;
    private TextView mTvName;
    private TextView mTvDesc;
    private ImageView mIvPrev;
    private ImageView mIvPlayOrPause;
    private ImageView mIvNext;
    private ImageView mIvCoverBg;
    private RoundRectImageView roundRectImageView;
    private boolean isScreenLockShown = false;
    public boolean isScreenLockShown () {
        return windowManager != null && isScreenLockShown;
    }

    public ScreenLockFloatWindow (Context context) {
//        mContext = new WeakReference<>(context);
//        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
//
//        minUnlockDy = context.getResources().getDisplayMetrics().heightPixels / 5;
    }

    public void showWindow (boolean updateViews) {
//        if (windowManager == null) return;
//
//        keyguardManager.newKeyguardLock("player").disableKeyguard();
//
//        if (params == null) {
//            params = new WindowManager.LayoutParams();
//            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
//            params.format = 1;
//            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//            params.flags = params.flags | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
//            params.flags = params.flags | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
//            params.flags = params.flags | WindowManager.LayoutParams.FLAG_FULLSCREEN;
//            params.flags = params.flags | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
//            params.gravity = Gravity.LEFT | Gravity.TOP;
//            params.x = 0;
//            params.y = 0;
//            params.width = WindowManager.LayoutParams.MATCH_PARENT;
//            params.height = WindowManager.LayoutParams.MATCH_PARENT;
//        }
//
//        if (mView == null) {
//            mView = LayoutInflater.from(mContext.get()).inflate(R.layout.widget_screen_lock_float_window, null);
//            mTvName = (TextView) mView.findViewById(R.id.tv_title);
//            mTvDesc = (TextView) mView.findViewById(R.id.tv_descript);
//            mIvPrev = (ImageView) mView.findViewById(R.id.iv_prev);
//            mIvPlayOrPause = (ImageView) mView.findViewById(R.id.iv_playOrPause);
//            mIvNext = (ImageView) mView.findViewById(R.id.iv_next);
//            mIvCoverBg = (ImageView) mView.findViewById(R.id.iv_cover_bg);
//            roundRectImageView = (RoundRectImageView) mView.findViewById(R.id.iv_cover);
//
//            mIvPrev.setOnClickListener(onPrevClickListener);
//            mIvPlayOrPause.setOnClickListener(onPlayOrPauseClickListener);
//            mIvNext.setOnClickListener(onNextClickListener);
//            mView.setOnTouchListener(onTouchListener);
//
//            windowManager.addView(mView, params);
//        }
//
//        if (onScreenLockListener != null && updateViews) {
//            onScreenLockListener.onScreenLockShow(mTvName, mTvDesc, roundRectImageView, mIvCoverBg, mIvPlayOrPause);
//        }
//        isScreenLockShown = true;
    }

    private void removeScreenLock () {
//        isScreenLockShown = false;
////        keyguardLock.reenableKeyguard();
//        if (windowManager == null || mView == null) return;
//        windowManager.removeView(mView);
//        mView = null;
    }

    private View.OnClickListener onPrevClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
////            removeScreenLock();
//            if (onScreenLockListener != null) {
//                onScreenLockListener.onPrevClick((ImageView) v);
//            }
        }
    };

    private View.OnClickListener onPlayOrPauseClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            if (onScreenLockListener != null) {
//                onScreenLockListener.onPlayOrPauseClick((ImageView) v);
//            }
        }
    };

    private View.OnClickListener onNextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            if (onScreenLockListener != null) {
//                onScreenLockListener.onNextClick((ImageView) v);
//            }
        }
    };

    private float startY;
    private float dy;
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    startY = event.getY();
//                    dy = 0;
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    dy = event.getY() - startY;
//                    if (dy < 0) mView.setTranslationY(dy);
//                    break;
//                case MotionEvent.ACTION_UP:
//                    if (dy < -minUnlockDy) {
//                        removeScreenLock();
//                    } else {
//                        mView.setTranslationY(0);
//                    }
//                    break;
//            }
            return false;
        }
    };

    private OnScreenLockListener onScreenLockListener;
    public void setOnScreenLockListener (OnScreenLockListener listener) {
        this.onScreenLockListener = listener;
    }

    public interface OnScreenLockListener {
        void onScreenLockShow (TextView tvTitle, TextView tvDescription, ImageView ivCover,
                               ImageView ivCoverBg, ImageView ivPlayOrPause);
        void onPrevClick (ImageView ivPrev);
        void onPlayOrPauseClick (ImageView ivPlayOrPause);
        void onNextClick (ImageView ivNext);
    }
}
