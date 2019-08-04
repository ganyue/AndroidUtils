package com.gy.appbase.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.gy.appbase.inject.ViewInjectInterpreter;
import com.gy.utils.weakreference.EWeakReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yue.gan on 2016/5/19.
 *
 */
public abstract class BaseFragmentActivity extends FragmentActivity implements View.OnClickListener, IBaseFragmentActivity {
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

    /** 不保存状态，如果activity因为内存、切换横竖屏等原因销毁，fragment也随着销毁 */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.remove("android:support:fragments");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContent(savedInstanceState);
        findViews();
        initViews(savedInstanceState);

        /*
         * android4.4.4 (sdk 19) 以后可以实现沉浸式
         */
        if ((transluentStatus || transluentNavigation) && Build.VERSION.SDK_INT >= 19) {
            /*
             * 这里只是这样的话会导致view被拉上去，要避免的话需要在布局文件里头加入下面两句
             * android:clipToPadding="false"     //绘制到padding区域
             * android:fitsSystemWindows="true"  //4.4.4以后这个属性可以给跟布局添加通知栏高度的padding
             */
            if (transluentStatus) getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (transluentNavigation) getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    @CallSuper
    protected void findViews () {
        ViewInjectInterpreter.interpret(this);
    }

    protected abstract void setContent (Bundle savedInstanceState);
    protected abstract void initViews (Bundle savedInstanceState);
    public abstract void onClick(View v);
    public abstract void fragmentCall(int type, Object extra);

    /** 按键监听，主要为了方便fragment监听KeyDown消息 */
    protected List<EWeakReference<OnKeyListener>> onKeyListeners;
    public void addOnKeyDownListener(OnKeyListener listener) {
        if (listener == null) return;
        if (onKeyListeners == null) onKeyListeners = new ArrayList<>();
        EWeakReference<OnKeyListener> reference = new EWeakReference<>(listener);
        if (onKeyListeners.contains(reference)) return;
        onKeyListeners.add(reference);
    }

    public void removeOnKeyDownListener (OnKeyListener listener) {
        if (onKeyListeners == null) return;
        onKeyListeners.remove(new EWeakReference<>(listener));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean ret = false;
        if (onKeyListeners != null) {
            for (EWeakReference<OnKeyListener> listener: onKeyListeners) {
                if (listener.get() != null) {
                    boolean result = listener.get().onKeyDown(keyCode, event);
                    ret = ret || result;
                }
            }
        }
        return ret || super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean ret = false;
        if (onKeyListeners != null) {
            for (EWeakReference<OnKeyListener> listener: onKeyListeners) {
                if (listener.get() != null) {
                    boolean result = listener.get().onKeyUp(keyCode, event);
                    ret = ret || result;
                }
            }
        }
        return ret || super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        onKeyListeners.clear();
        super.onDestroy();
    }
}
