package com.gy.widget.popup;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.PopupWindow;

import java.lang.ref.WeakReference;

/**
 * Created by yue.gan on 2016/11/9.
 *
 */
public abstract class BasePopupWindow extends PopupWindow{

    protected WeakReference<Context> mContext;

    public BasePopupWindow(Context context) {
        super(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mContext = new WeakReference<>(context);
        setContentView(initContentView());
    }

    public BasePopupWindow(Context context, int width, int height) {
        super(width, height);
        mContext = new WeakReference<>(context);
        setContentView(initContentView());
    }


    public BasePopupWindow enableKeyBackDismiss () {
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getContentView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        return BasePopupWindow.this;
    }

    public abstract void show();

    /**
     * create or inflate content view here
     */
    protected abstract View initContentView ();

    protected final float Alpha_Default_Dark = 0.7f;
    protected final float Alpha_Default_Normal = 1f;
    /** 设置背景透明，小于1的时候背景会变暗，比较合理的是0.6或0.7，1的时候背景正常 */
    protected void setBackgroundAlpha (float alpha) {
        WindowManager.LayoutParams lp = ((Activity)mContext.get()).getWindow().getAttributes();
        lp.alpha = alpha;
        ((Activity)mContext.get()).getWindow().setAttributes(lp);
    }
}
