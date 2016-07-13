package com.gy.appbase.controller;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by sam_gan on 2015/03/31.
 *
 * <p>FragmentActivity 使用这个管理类要求：
 * <p>1: Fragment中需要有名为newInstance的静态方法来实例化该Fragment
 * <p>2: FragmentActivity需要一个名为setController的方法，这个实例将用setController方法把本实例传
 * 到Activity中去这是为了在Fragment中创建Controller的时候，可以把这个Controller同步到Activity中去
 *
 */
public class BaseFragmentActivityController {

    protected class MethodNames {
        public static final String M_SET_CONTROLLER = "setController";
    }

    protected FragmentActivity mActivity;
    protected FragmentManager mFragmentManager;
    private final String Tag = BaseFragmentActivityController.class.getSimpleName();

    public BaseFragmentActivityController(FragmentActivity activity) {
        mActivity = activity;
        mFragmentManager = mActivity.getSupportFragmentManager();

        setController(mActivity);
        List<Fragment> fragments = mFragmentManager.getFragments();
        if (fragments != null && fragments.size() > 0) {
            for (Fragment fragment : fragments) {
                setController(fragment);
            }
        }
    }

    protected Method getMethod(Class clazz, String methodName, Class[] paramTypes) {
        try{
            return clazz.getMethod(methodName, paramTypes);
        } catch (Exception e) {
            Log.e(Tag, "cannot find method : " + methodName + " from class : " + clazz.getName());
        }
        return null;
    }

    protected Fragment createFragment (Class fragClazz, Class[] paramTypes, Object[] params) {
        try {
            Constructor constructor = fragClazz.getConstructor(paramTypes);
            return (Fragment) constructor.newInstance(params);
        } catch (Exception e) {
            Log.e(Tag, "failed to create instance of class : " + fragClazz.getName());
        }

        return null;
    }

    protected void setController (Object object) {
        Method method = getMethod(object.getClass(), MethodNames.M_SET_CONTROLLER,
                new Class[]{BaseFragmentActivityController.class});
        if (method != null) {
            try {
                method.invoke(object, this);
            } catch (Exception e) {
                Log.e(Tag, "failed to setController() to object : " + object.getClass().getName());
            }
        }
    }

    protected String getTag (Class clazz) {
        return clazz.getSimpleName();
    }

    public Fragment replaceFragment (int holderId, Class fragClazz, Class[] paramTypes, Object[] params) {
        if (mFragmentManager == null) {
            return null;
        }

        Fragment fragment = mFragmentManager.findFragmentByTag(getTag(fragClazz));
        if (fragment != null) {
            return fragment;
        }

        if ((fragment = createFragment(fragClazz, paramTypes, params)) != null) {
            mFragmentManager.beginTransaction().replace(holderId, fragment, getTag(fragClazz)).commit();
        }

        if (fragment != null) {
            setController(fragment);
        }

        return fragment;
    }

    public Fragment addFragment (int holderId, Class fragClazz, Class[] paramTypes, Object[] params) {
        if (mFragmentManager == null) {
            return null;
        }

        Fragment fragment = mFragmentManager.findFragmentByTag(getTag(fragClazz));
        if (fragment == null) {
            if ((fragment = createFragment(fragClazz, paramTypes, params)) != null) {
                mFragmentManager.beginTransaction().add(holderId, fragment, getTag(fragClazz)).commit();
            }
        }

        if (fragment != null) {
            setController(fragment);
        }

        return fragment;
    }

    public Fragment showFragment (boolean hideOthers,
                                  List<String> whiteTagList,
                                  int holderId,
                                  Class fragClazz,
                                  Class[] paramTypes,
                                  Object[] params) {

        Fragment fragment = addFragment(holderId, fragClazz, paramTypes, params);
        if (fragment == null) {
            return null;
        }

        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        if (hideOthers) {
            List<Fragment> fragments = mFragmentManager.getFragments();
            if (fragments != null) {
                for (Fragment frag : fragments) {
                    String tag = frag.getTag();
                    if (!TextUtils.isEmpty(tag) && whiteTagList != null && whiteTagList.contains(tag)) {
                        continue;
                    }
                    transaction.hide(frag);
                }
            }
        }

        transaction.show(fragment);
        transaction.commit();
        return fragment;
    }

    public void destroy () {
        mActivity = null;
        mFragmentManager = null;
    }
}
