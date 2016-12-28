package com.gy.widget.popup;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.gy.widget.R;

/**
 * Created by ganyu on 2016/11/9.
 *
 */
public abstract class BaseBottomPopupWindow extends BasePopupWindow{

    protected boolean enableBackgroundDarkWhileShown;

    public BaseBottomPopupWindow(Context context) {
        super(context);
        setAnimationStyle(R.style.widget_popup_bottom_in_out);
    }

    public BaseBottomPopupWindow(Context context, int width, int height) {
        super(context, width, height);
        setAnimationStyle(R.style.widget_popup_bottom_in_out);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    public BaseBottomPopupWindow enableBackgroundDarkWhileShown (boolean enable) {
        enableBackgroundDarkWhileShown = enable;
        return BaseBottomPopupWindow.this;
    }


    public BaseBottomPopupWindow enableKeyBackDismiss () {
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
        return BaseBottomPopupWindow.this;
    }

    /** 从页面最底部弹出 */
    public void show () {
        showAtLocation(((ViewGroup)((Activity)mContext.get()).getWindow().getDecorView()
                .findViewById(android.R.id.content)).getChildAt(0), Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        if (enableBackgroundDarkWhileShown) {
            setBackgroundAlpha(Alpha_Default_Dark);
            setOnDismissListener(onDismissListener);
        }
    }

    protected OnDismissListener onDismissListener = new OnDismissListener() {
        @Override
        public void onDismiss() {
            if (enableBackgroundDarkWhileShown) {
                setBackgroundAlpha(Alpha_Default_Normal);
                BaseBottomPopupWindow.this.setOnDismissListener(null);
            }
        }
    };

}
