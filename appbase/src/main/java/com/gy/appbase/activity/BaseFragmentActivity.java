package com.gy.appbase.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.gy.appbase.controller.BaseFragmentActivityController;
import com.gy.appbase.fragment.BaseFragment;
import com.gy.appbase.inject.ViewInjectInterpreter;

/**
 * Created by yue.gan on 2016/5/19.
 *
 */
public abstract class BaseFragmentActivity extends FragmentActivity {
    protected BaseFragmentActivityController mController;
    private boolean transluentStatus = false;
    private boolean transluentNavigation = false;

    protected void setTransluentStatusAndNavigation (boolean transluentStatusAndNavigation) {
        transluentStatus = transluentStatusAndNavigation;
        transluentNavigation = transluentStatusAndNavigation;
    }

    protected void setTransluentStatus (boolean transluentStatus) {
        this.transluentStatus = transluentStatus;
    }

    protected void setTransluentNavigation (boolean transluentNavigation) {
        this.transluentNavigation = transluentNavigation;
    }

    @CallSuper
    public void setController (BaseFragmentActivityController controller) {
        mController = controller;
    }

    public BaseFragmentActivityController getController () {
        return mController;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mController == null) {
            instanceController();
        }

        setContent(savedInstanceState);
        findViews(savedInstanceState);
        initViews(savedInstanceState);

        /**
         * android4.4.4 (sdk 19) 以后可以实现沉浸式
         */
        if ((transluentStatus || transluentNavigation) && Build.VERSION.SDK_INT >= 19) {
            /**
             * 这里只是这样的话会导致view被拉上去，要避免的话需要在布局文件里头加入下面两句
             * android:clipToPadding="false"     //绘制到padding区域
             * android:fitsSystemWindows="true"  //4.4.4以后这个属性可以给跟布局添加通知栏高度的padding
             */
            if (transluentStatus) getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (transluentNavigation) getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mController == null) {
            instanceController();
        }
    }

    @CallSuper
    protected void findViews (Bundle savedInstanceState) {
        ViewInjectInterpreter.interpret(this);
    }

    protected abstract void setContent (Bundle savedInstanceState);
    protected abstract void initViews (Bundle savedInstanceState);
    protected abstract BaseFragmentActivityController instanceController ();


    /** 按键监听，主要为了方便fragment监听KeyDown消息 */
    protected OnKeyDownCallback onKeyDownCallback;
    public void setOnKeyDownCallback (OnKeyDownCallback callback) {
        onKeyDownCallback = callback;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (onKeyDownCallback != null) {
            if (onKeyDownCallback.onKeyDownListener(keyCode, event)) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
