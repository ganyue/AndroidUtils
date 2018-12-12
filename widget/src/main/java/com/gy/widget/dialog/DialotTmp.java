package com.gy.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Window;

/**
 * created by yue.gan 18-12-12
 */
public class DialotTmp extends Dialog {
    public DialotTmp(@NonNull Context context) {
        super(context);
    }

    private void init () {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
}
