package com.android.ganyue.kline;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
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
    public StockParser init () {
        if (mInited) return this;
        mTaskQueue = new ArrayBlockingQueue<>(5120);
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
                Stock stock = parseSync(path);
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

    /**
     * 数据结构:
     *
     * 600056 中国医药 日线 前复权
     *       日期	    开盘	    最高	    最低	    收盘	    成交量	    成交额
     * 20080902,1.58,1.69,1.52,1.62,680179,6646526.00
     *
     * @param path .
     * @return .
     * @throws IOException .
     */
    public Stock parseSync (String path) throws IOException {
        Stock stock = new Stock();
        stock.path = path;
        BufferedReader reader = new BufferedReader(new FileReader(path));

        String stockHeadStr = reader.readLine();
        if (TextUtils.isEmpty(stockHeadStr)) return null;
        String[] stockHeads = stockHeadStr.split(" ");
        stock.code = stockHeads[0];
        stock.name = stockHeads[1];

        String stockListHeadStr = reader.readLine();
        if (TextUtils.isEmpty(stockListHeadStr)) return null;

        String line;
        List<DayInfo> dayInfos = new ArrayList<>();
        DayInfo prevInfo = null;
        while ((line = reader.readLine()) != null) {
            String[] stockInfos = line.split(",");
            DayInfo info = new DayInfo();
            info.date = Integer.parseInt(stockInfos[0]);
            info.open = Float.parseFloat(stockInfos[1]);
            info.high = Float.parseFloat(stockInfos[2]);
            info.low = Float.parseFloat(stockInfos[3]);
            info.close = Float.parseFloat(stockInfos[4]);
            info.volV = Integer.parseInt(stockInfos[5]);
            info.volA = Float.parseFloat(stockInfos[6]);

            if (prevInfo == null) {
                prevInfo = info;
                info.rate = 0;
                info.preDate = info.date;
            } else {
                info.rate = ((int)((info.close - prevInfo.close) / prevInfo.close * 10000)) * 100f;
                info.preDate = prevInfo.date;
            }
            dayInfos.add(info);
        }
        if (dayInfos.size() <= 0) return null;
        stock.dayInfos = dayInfos;

        return stock;
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
