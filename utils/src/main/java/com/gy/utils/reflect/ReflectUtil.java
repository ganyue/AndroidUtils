package com.gy.utils.reflect;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by yue.gan. 2019/8/4
 */
public class ReflectUtil {

    private static final String Tag = "ReflectUtil";

    public static Object newInstance (Class clazz) {
        return newInstance(clazz, null, null);
    }

    public static Object newInstance (Class clazz, Class[] paramTypes, Object[] params) {
        try {
            Constructor constructor = clazz.getConstructor(paramTypes);
            return constructor.newInstance(params);
        } catch (Exception e) {
            Log.e(Tag, "failed to create instance of class : " + clazz.getName());
        }
        return null;
    }

    private static Method getMethod(Class clazz, String methodName, Class[] paramTypes) {
        try{
            return clazz.getDeclaredMethod(methodName, paramTypes);
        } catch (Exception e) {
            Log.e(Tag, "cannot find method : " + methodName + " from class : " + clazz.getName());
        }
        return null;
    }

    private static void invokeMethod (Class clazz, String methodName, Class[] paramTypes, Object target, Object[] params) {
        Method method = getMethod(clazz, methodName, paramTypes);
        if (method == null) return;
        try {
            method.invoke(target, params);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
