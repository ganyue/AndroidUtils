package com.gy.appbase.inject;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;

import java.lang.reflect.Field;

/**
 * Created by sam_gan on 2016/6/20.
 *
 */
public class ViewInjectInterpreter {

    public static void interpret (Activity activity) {

        Field[] fields = activity.getClass().getDeclaredFields();
        int id = 0;
        ViewInject viewInject;
        for (Field field: fields) {
            if (field.isAnnotationPresent(ViewInject.class)) {
                viewInject = field.getAnnotation(ViewInject.class);
                id = viewInject.value();
                if (id > 0) {
                    try {
                        field.setAccessible(true);
                        field.set(activity, activity.findViewById(id));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void interpret (Fragment fragment, View view) {

        Field[] fields = fragment.getClass().getDeclaredFields();
        int id = 0;
        ViewInject viewInject;
        for (Field field: fields) {
            if (field.isAnnotationPresent(ViewInject.class)) {
                viewInject = field.getAnnotation(ViewInject.class);
                id = viewInject.value();
                if (id > 0) {
                    try {
                        field.setAccessible(true);
                        field.set(fragment, view.findViewById(id));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
