package com.gy.utils.preference;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * Created by sam_gan on 2016/6/22.
 *
 */
public class SharedPreferenceUtils {

    private static SharedPreferenceUtils mInstance;
    private WeakReference<Context> mContext;

    public static SharedPreferenceUtils getInstance (Context context) {
        if (mInstance == null) {
            mInstance = new SharedPreferenceUtils(context);
        }
        mInstance.setContext(context);
        return mInstance;
    }

    public SharedPreferenceUtils(Context context) {
        super();
    }
    
    public void setContext (Context context) {
        mContext = new WeakReference<Context>(context);
    }

    public SharedPreferences getPref () {
        return mContext.get().getSharedPreferences("pref", Context.MODE_PRIVATE);
    }

    //string
    public void saveStr (String key, String value) {
        SharedPreferences preferences = getPref();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    
    public String getStr (String key) {
        SharedPreferences preferences = getPref();
        return preferences.getString(key, "");
    }

    //integer
    public void saveInt (String key, int value) {
        SharedPreferences preferences = getPref();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt (String key) {
        SharedPreferences preferences = getPref();
        return preferences.getInt(key, 0);
    }

    //boolean
    public void saveBoolean (String key, boolean value) {
        SharedPreferences preferences = getPref();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean (String key) {
        SharedPreferences preferences = getPref();
        return preferences.getBoolean(key, false);
    }

    //long
    public void saveLong (String key, long value) {
        SharedPreferences preferences = getPref();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public long getLong (String key) {
        SharedPreferences preferences = getPref();
        return preferences.getLong(key, 0);
    }

    //float
    public void saveFloat (String key, float value) {
        SharedPreferences preferences = getPref();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public float getFloat (String key) {
        SharedPreferences preferences = getPref();
        return preferences.getFloat(key, 0);
    }

    //save a object
    public void save (Class clazz, Object obj) {
        String nameSpace = clazz.getSimpleName() + "_";
        Field[] fields = clazz.getDeclaredFields();

        String fieldTypeName;
        String fieldName;
        for (Field field: fields) {
            fieldName = field.getName();
            fieldTypeName = field.getType().getSimpleName().toLowerCase();
            field.setAccessible(true);
            try {
                if (fieldTypeName.equals("int") || fieldTypeName.equals("integer")) {
                    saveInt(nameSpace + fieldName, obj == null ? 0 : field.getInt(obj));
                } else if (fieldTypeName.equals("long")) {
                    saveLong(nameSpace + fieldName, obj == null ? 0 : field.getLong(obj));
                } else if (fieldTypeName.equals("float")) {
                    saveFloat(nameSpace + fieldName, obj == null ? 0 : field.getFloat(obj));
                } else if (fieldTypeName.equals("string")) {
                    saveStr(nameSpace + fieldName, (String) (obj == null? "": field.get(obj)));
                } else if (fieldTypeName.equals("boolean") || fieldTypeName.equals("Boolean")) {
                    saveBoolean(nameSpace + fieldName, obj == null ? false : field.getBoolean(obj));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //get object
    public Object get (Class clazz) {
        try {
            String nameSpace = clazz.getSimpleName() + "_";
            Object object = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            String fieldTypeName;
            String fieldName;

            for (Field field: fields) {
                fieldName = field.getName();
                fieldTypeName = field.getType().getSimpleName().toLowerCase();
                field.setAccessible(true);
                try {
                    if (fieldTypeName.equals("int") || fieldTypeName.equals("integer")) {
                        field.setInt(object, getInt(nameSpace + fieldName));
                    } else if (fieldTypeName.equals("long")) {
                        field.setLong(object, getLong(nameSpace + fieldName));
                    } else if (fieldTypeName.equals("float")) {
                        field.setFloat(object, getFloat(nameSpace + fieldName));
                    } else if (fieldTypeName.equals("string")) {
                        field.set(object, getStr(nameSpace + fieldName));
                    } else if (fieldTypeName.equals("boolean") || fieldTypeName.equals("Boolean")) {
                        field.setBoolean(object, getBoolean(nameSpace + fieldName));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
