package com.gy.appbase.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;

import com.gy.utils.log.LogUtils;

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

    private final String Tag = BaseFragmentActivityController.class.getSimpleName();

    public BaseFragmentActivityController(FragmentActivity activity) {
        bindActivity(activity);
    }

    public void bindActivity (FragmentActivity activity) {
        if (activity == null) return;
        FragmentManager mFragmentManager = activity.getSupportFragmentManager();

        setController(activity);
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

    public Fragment createFragment (Class fragClazz) {
        return createFragment(fragClazz, null, null);
    }

    public Fragment createFragment (Class fragClazz, Bundle arguments) {
        Fragment fragment = createFragment(fragClazz, null, null);
        if (fragment != null) {
            setArgument(fragment, arguments);
        }
        return fragment;
    }

    public Fragment createFragment (Class fragClazz, Class[] paramTypes, Object[] params) {
        try {
            Constructor constructor = fragClazz.getConstructor(paramTypes);
            return (Fragment) constructor.newInstance(params);
        } catch (Exception e) {
            Log.e(Tag, "failed to create instance of class : " + fragClazz.getName());
        }

        return null;
    }

    public void setController (Object object) {
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

    public String getTag (Class clazz) {
        return clazz.getSimpleName();
    }

    public Fragment replaceFragment (FragmentManager fragmentManager, int holderId, Class fragClazz) {
        return replaceFragment(fragmentManager, holderId, fragClazz, null, null);
    }

    public Fragment replaceFragment (FragmentManager fragmentManager, int holderId, Class fragClazz, Bundle arguments) {
        Fragment fragment = replaceFragment(fragmentManager, holderId, fragClazz, null, null);
        setArgument(fragment, arguments);
        return fragment;
    }

    public Fragment replaceFragment (FragmentManager fragmentManager, int holderId, Class fragClazz, Class[] paramTypes, Object[] params) {
        if (fragmentManager == null) {
            return null;
        }

        Fragment fragment = fragmentManager.findFragmentByTag(getTag(fragClazz));

        if (fragment == null && (fragment = createFragment(fragClazz, paramTypes, params)) == null) {
            return null;
        }
        setController(fragment);
        fragmentManager.beginTransaction().replace(holderId, fragment, getTag(fragClazz))
                .disallowAddToBackStack().disallowAddToBackStack().commitAllowingStateLoss();

        return fragment;
    }

    public Fragment addFragment (FragmentManager fragmentManager, int holderId, Class fragClazz) {
        return addFragment(fragmentManager, holderId, fragClazz, null, null);
    }

    public Fragment addFragment (FragmentManager fragmentManager, int holderId, Class fragClazz, Bundle arguments) {
        Fragment fragment = addFragment(fragmentManager, holderId, fragClazz, null, null);
        if (fragment != null) {
            setArgument(fragment, arguments);
        }
        return fragment;
    }

    public Fragment addFragment (FragmentManager fragmentManager, int holderId, Class fragClazz, Class[] paramTypes, Object[] params) {
        if (fragmentManager == null) {
            return null;
        }

        Fragment fragment = fragmentManager.findFragmentByTag(getTag(fragClazz));
        if (fragment == null) {
            if ((fragment = createFragment(fragClazz, paramTypes, params)) != null) {
                fragmentManager.beginTransaction().add(holderId, fragment, getTag(fragClazz))
                        .disallowAddToBackStack().commitAllowingStateLoss();
            }
        }

        if (fragment != null) {
            setController(fragment);
        }

        return fragment;
    }

    public Fragment hideFragment (FragmentManager fragmentManager, Class fragClazz) {
        Fragment fragment = fragmentManager.findFragmentByTag(getTag(fragClazz));
        if (fragment != null) {
            fragmentManager.beginTransaction().hide(fragment).disallowAddToBackStack().commitAllowingStateLoss();
        }
        return fragment;
    }

    public Fragment showFragment (FragmentManager fragmentManager,
                                  boolean hideOthers,
                                  List<String> whiteTagList,
                                  int holderId,
                                  Class fragClazz) {
        return showFragment(fragmentManager, hideOthers, whiteTagList, holderId, fragClazz, null, null);
    }

    public Fragment showFragment (FragmentManager fragmentManager,
                                  boolean hideOthers,
                                  List<String> whiteTagList,
                                  int holderId,
                                  Class fragClazz,
                                  Bundle argument) {
        Fragment fragment = showFragment(fragmentManager, hideOthers, whiteTagList, holderId, fragClazz, null, null);
        setArgument(fragment, argument);
        return fragment;
    }

    public Fragment showFragment (FragmentManager fragmentManager,
                                  boolean hideOthers,
                                  List<String> whiteTagList,
                                  int holderId,
                                  Class fragClazz,
                                  Class[] paramTypes,
                                  Object[] params) {

        Fragment fragment = addFragment(fragmentManager, holderId, fragClazz, paramTypes, params);
        if (fragment == null) {
            return null;
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (hideOthers) {
            List<Fragment> fragments = fragmentManager.getFragments();
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
        transaction.disallowAddToBackStack().commitAllowingStateLoss();
        return fragment;
    }

    private void setArgument (Fragment fragment, Bundle arg) {
        if (fragment != null) {
            if (fragment.getArguments() != null) {
                fragment.getArguments().clear();
                fragment.getArguments().putAll(arg);
            } else {
                try {
                    fragment.setArguments(arg);
                } catch (IllegalStateException e) {
                    LogUtils.e("yue.gan", e.toString());
                }
            }
        }
    }

}
