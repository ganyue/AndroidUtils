package com.android.ganyue.kline;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class StockParser extends Thread {

    private ArrayBlockingQueue<String> mTaskQueue;
    private Context mContext;
    private boolean mStoped = false;
    private boolean mInited = false;
    public StockParser init (Context cxt) {
        if (mInited) return this;
        mContext = cxt.getApplicationContext();
        mTaskQueue = new ArrayBlockingQueue<String>(64);
        start();
        mStoped = false;
        mInited = true;
        return this;
    }

    public void release () {
        mStoped = true;
        mContext = null;
        mTaskQueue.clear();
        mCallback = null;
        interrupt();
    }

    public StockParser parseStockAsync(String path) {
        mTaskQueue.offer(path);
        return this;
    }

    @Override
    public void run() {
        while (!mStoped) {
            String path = "";
            try {
                path = mTaskQueue.take();
                List<DayInfo> dayInfos = parseDayInfoSync(path);
                Stock stock = new Stock(path, dayInfos);
                if (mCallback != null && mCallback.get() != null) {
                    mCallback.get().onResult(path, stock);
                }
            } catch (Exception e) {
                if (mCallback != null && mCallback.get() != null) {
                    mCallback.get().onError(path, "", e);
                }
            }
        }
    }


    private List<DayInfo> parseDayInfoSync(String path) throws IOException {
        InputStream in = mContext.getAssets().open(path);
        List<DayInfo> ret = new ArrayList<>();
        byte[] buf = new byte[32];
        float prevClose = 0;
        while (in.read(buf) == 32) {
            DayInfo info = new DayInfo(buf);
            if (prevClose == 0) info.rate = 0;
            else info.rate = ((int)((info.close - prevClose)/prevClose * 10000))/100f;
            prevClose = info.close;
            ret.add(info);
        }
        return ret;
    }

    public StockParser setParseCallback (OnParseCallback callback) {
        mCallback = new WeakReference<>(callback);
        return this;
    }
    private WeakReference<OnParseCallback> mCallback;
    public interface OnParseCallback {
        void onResult (String path, Stock stock);
        void onError(String path, String msg, Exception e);
    }
}
