package com.android.ganyue.kline;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.gy.utils.log.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * VAR1:=DMA(CLOSE,VOL/SUM(VOL,34));
 * VAR2:=DMA(CLOSE,VOL/SUM(VOL,13));
 * VAR3:=(CLOSE-VAR1)/VAR1*100;
 * VAR4:=(CLOSE-VAR2)/VAR2*100;
 * 买入: VAR3<=-19 AND VAR4<=-12 ,COLORRED,LINETHICK2;
 *
 * 公式共4个参数 VAR1中的34 (P1)， VAR2中的13 (P2)，VAR3的-19 (P3)，VAR4的-12 (P4)
 * 再加上止损 5%-10% (P5)， 5-20 (P6)天后卖出这两个参数
 * 共6个变量，不停变动几个变量，找出最优解
 */
public class OptimizerDMACV extends Thread {

    private Context cxt;
    public OptimizerDMACV (Context cxt) {
        this.cxt = cxt.getApplicationContext();
        LogUtils.enableLogToFile(cxt, true);
    }

    @Override
    public void run() {
        try {
            AssetManager am = cxt.getAssets();
            List<Stock> stocks = new ArrayList<>();
            String[] szDay = am.list("tdx/sz/lday");
            String[] shDay = am.list("tdx/sh/lday");
            StockParser parser = new StockParser();
            parser.init(cxt);
            for (String p: szDay) {
                stocks.add(parser.parseSync("tdx/sz/lday/" + p));
            }
            for (String p: shDay) {
                stocks.add(parser.parseSync("tdx/sh/lday/" + p));
            }

            // 运算量太大，只算 p5 = 5; p6 = 20;
//            int p1MIN = 30, p1MAX = 40;
//            int p2MIN = 10, p2MAX = 20;
//            int p3MIN = -25, p3MAX = -15;
//            int p4MIN = -20, p4MAX = -10;
            int p1MIN = 32, p1MAX = 37;
            int p2MIN = 12, p2MAX = 17;
            int p3MIN = -23, p3MAX = -17;
            int p4MIN = -15, p4MAX = -10;
            int p5 = 10;
            int p6 = 20;
            int p1, p2, p3, p4;

            p1 = p1MIN;
            while(p1 < p1MAX) {
                p2 = p2MIN;
                while(p2 < p2MAX) {
                    p3 = p3MIN;
                    while(p3 < p3MAX) {
                        p4 = p4MIN;
                        while(p4 < p4MAX) {
                            int count = 0;
                            int success = 0;
                            for (Stock stock: stocks) {
                                List<DayInfo> infos = getDMACVResult(stock, p1, p2, p3, p4);
                                int size = stock.dayInfos.size();
                                DayInfo prevInfo = null;
                                DayInfo prevBuyInfo = null;
                                int prevBuyIndex = 0;
                                for (DayInfo info: infos) {
                                    String infoLogStr = stock.code;

                                    // 信号出来时候距离最后一条日线数据不足止损天数不考虑
                                    int index = stock.dayInfos.indexOf(info);
                                    if (index + p6 >= size) continue;

                                    // 本次出现信号地点距离上次信号地点不足止损天数不考虑
                                    if(prevBuyInfo != null && prevBuyIndex != 0 &&
                                            index - prevBuyIndex < p6) {
                                        continue;
                                    }

                                    // 连续信号不买卖
                                    if (prevInfo != null && info.preDate == prevInfo.date) {
                                        prevInfo = info;
                                        continue;
                                    }
                                    prevInfo = info;
                                    prevBuyInfo = info;
                                    prevBuyIndex = index;

                                    // 买入
                                    count++;

                                    infoLogStr += " buy --> " + info.date + " params --> ";
                                    infoLogStr += String.format(Locale.getDefault(),
                                            "p1=%02d, p2=%02d, p3=%02d, p4=%02d, p5=%02d, p6=%02d ret --> ",
                                            p1, p2, p3, p4, p5, p6);

                                    // 计算是否有止损出现
                                    float salePrice = info.close * (1 - p5 / 100f);
                                    boolean sold = false;
                                    for (int i = 1; i <= p6; i++) {
                                        DayInfo tmp = stock.dayInfos.get(index + i);
                                        if (tmp.close < salePrice) {
                                            infoLogStr += "failed sold " + tmp.date;
                                            sold = true;
                                            break;
                                        }
                                    }
                                    if (sold) {
                                        LogUtils.dd(infoLogStr);
                                        continue;
                                    }

                                    // 计算到止损日时，是否有涨2个点
                                    salePrice = info.close * 1.02f;
                                    DayInfo endInfo = stock.dayInfos.get(index + p6);
                                    if (endInfo.close > salePrice) {
                                        success ++;
                                        infoLogStr += "success!!! " + endInfo.date;
                                    } else {
                                        infoLogStr += "failed loss " + endInfo.date;
                                    }

                                    LogUtils.dd(infoLogStr);
                                }
                            }

                            float result = count == 0? 0: 1f*success/count;
                            LogUtils.d(String.format(Locale.getDefault(),
                                    "result=%.2f, p1=%02d, p2=%02d, p3=%02d, p4=%02d, p5=%02d, p6=%02d",
                                    result, p1, p2, p3, p4, p5, p6));


                            p4++;
                        }
                        p3++;
                    }
                    p2++;
                }
                p1++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<DayInfo> getDMACVResult (Stock stock, int p1, int p2, int p3, int p4) {
        float[] var1 = stock.DMA(DayInfo.Type.CLOSE, stock.SUMP(DayInfo.Type.VOL, p1));
        float[] var2 = stock.DMA(DayInfo.Type.CLOSE, stock.SUMP(DayInfo.Type.VOL, p2));
        float[] var3 = new float[stock.dayInfos.size()];
        float[] var4 = new float[stock.dayInfos.size()];

        for (int i = 0; i < stock.dayInfos.size(); i++) {
            float close = stock.dayInfos.get(i).close;
            var3[i] = (close - var1[i])/var1[i] * 100;
            var4[i] = (close - var2[i])/var2[i] * 100;
        }

        List<DayInfo> infos = new ArrayList<>();
        for (int i = 0; i < stock.dayInfos.size(); i++) {
            if (var3[i] <= -p3 && var4[i] <= -p4) infos.add(stock.dayInfos.get(i));
        }
        return infos;
    }
}
