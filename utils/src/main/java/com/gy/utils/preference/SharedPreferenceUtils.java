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

    public String getStr (String key, String defVal) {
        SharedPreferences preferences = getPref();
        return preferences.getString(key, defVal);
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

    public int getInt (String key, int defVal) {
        SharedPreferences preferences = getPref();
        return preferences.getInt(key, defVal);
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

    public boolean getBoolean (String key, boolean defVal) {
        SharedPreferences preferences = getPref();
        return preferences.getBoolean(key, defVal);
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

    public long getLong (String key, long defVal) {
        SharedPreferences preferences = getPref();
        return preferences.getLong(key, defVal);
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

    public float getFloat (String key, float defVal) {
        SharedPreferences preferences = getPref();
        return preferences.getFloat(key, defVal);
    }

    /**
     * <p>存储一个对象，目前支持int，float， String， boolean， 可以存储多个（需不同昵称）</p>
     * @param nickname  昵称
     * @param obj       要存储的对象
     */
    public void save (String nickname, Object obj) {
        Class clazz = obj.getClass();
        String nameSpace = clazz.getSimpleName() + "_" + nickname + "_";
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

    /**
     *
     * @param nickname 昵称
     * @param clazz     类
     * @return          生成的指定类的对象，对象值从该昵称对应的preference中取的
     */
    public Object get (String nickname, Class clazz) {
        try {
            String nameSpace = clazz.getSimpleName() + "_" + nickname + "_";
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
