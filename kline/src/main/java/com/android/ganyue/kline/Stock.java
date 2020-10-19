package com.android.ganyue.kline;

import java.util.ArrayList;
import java.util.List;


public class Stock {

    public String path;
    public String code;
    public String name;
    public List<DayInfo> dayInfos;

    public Stock() {
    }

    public Stock (String path, List<DayInfo> infos) {
        this.path = path;
        this.dayInfos = infos;
        int codeStartIndex = path.lastIndexOf('/');
        int codeEndIndex = path.indexOf(".");
        codeStartIndex = codeStartIndex < 0? 0: codeStartIndex + 1;
        this.code = path.substring(codeStartIndex, codeEndIndex);
    }

    public float[] SUMP (DayInfo.Type T, int N) {
        return StockFormula.sumPercent(this, T, N);
    }

    /** DMA CLOSE & SUMP(VOL, N)*/
    public float[] DMA (DayInfo.Type type, float[] paramII) {
        return StockFormula.dma(this, type, paramII);
    }

    public List<DayInfo> getCustomFormulaResult () {
        float[] var1 = DMA(DayInfo.Type.CLOSE, SUMP(DayInfo.Type.VOL, 34));
        float[] var2 = DMA(DayInfo.Type.CLOSE, SUMP(DayInfo.Type.VOL, 13));
        float[] var3 = new float[dayInfos.size()];
        float[] var4 = new float[dayInfos.size()];

        for (int i = 0; i < dayInfos.size(); i++) {
            float close = dayInfos.get(i).close;
            var3[i] = (close - var1[i])/var1[i] * 100;
            var4[i] = (close - var2[i])/var2[i] * 100;
        }

        List<DayInfo> infos = new ArrayList<>();
        for (int i = 0; i < dayInfos.size(); i++) {
            if (var3[i] <= -19 && var4[i] <= -12) infos.add(dayInfos.get(i));
        }
        return infos;
    }
}
