package com.gy.appbase.controller;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by yue.gan on 2017/5/27.
 *
 */

public class BaseHandlerController {

    private static HashMap<String, MHandler> mHandlers;

    public static Handler getHandler (Object obj, IBaseHandlerController callback) {
        String name = obj.getClass().getSimpleName() + callback;
        if (mHandlers.containsKey(name)) {
            return mHandlers.get(name);
        }
        MHandler handler = new MHandler();
        handler.tag = name;
        handler.ownner = new WeakReference<>(obj);
        handler.callback = new WeakReference<>(callback);
        mHandlers.put(name, handler);
        return handler;
    }

    private static class MHandler extends Handler {
        WeakReference<Object> ownner;
        WeakReference<IBaseHandlerController> callback;
        String tag = "";
        @Override
        public void handleMessage(Message msg) {
            if (ownner != null && ownner.get() != null
                    && callback != null && callback.get() != null) {
                callback.get().handleMessage(ownner.get(), msg);
            } else {
                mHandlers.remove(tag);
            }
        }
    }

    public interface IBaseHandlerController {
        void handleMessage(Object obj, Message msg);
    }
}
