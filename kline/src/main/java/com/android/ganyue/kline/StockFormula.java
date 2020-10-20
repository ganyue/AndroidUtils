package com.android.ganyue.kline;

public class StockFormula {

    public static float[] sumPercent (Stock stock, DayInfo.Type type, int n) {
        int size = stock.dayInfos.size();
        float[] ret = new float[size];
        float sum = 0;
        for (int i = 0; i < size; i++) {
            float curVal = stock.dayInfos.get(i).getValue(type);
            if (i < n) sum += curVal;
            else sum = sum + curVal - stock.dayInfos.get(i-n).getValue(type);
            ret[i] = curVal/sum;
        }
        return ret;
    }

    /**
     * DMA
     * 求动态移动平均.用法: DMA(X,A),求X的动态移动平均.算法:
     * 若Y=DMA(X,A)则 Y=A*X+(1-A)*Y',其中Y'表示上一周期Y值,A必须小于1.
     * 例如:DMA(CLOSE,VOL/CAPITAL)表示求以换手率作平滑因子的平均价
     * @param size 日线天数
     * @param paramSupplier 计算dma，paramSupplier需实现paramA和paramB,分别提供DMA(X,A)的参数
     * @return 返回size天数的dma计算结果
     */
    public static float[] dma (int size, ParamSupplier paramSupplier) {
        float[] ret = new float[size];
        for (int i = 0; i < size; i++) {
            float x = paramSupplier.paramI(i);
            float a = paramSupplier.paramII(i);
            ret[i] = a * x + (1 - a) * (i == 0? 0: ret[i - 1]);
        }
        return  ret;
    }

    public static float[] dma (final float[] paramI, final float[] paramII) {
        return dma(paramI.length, new ParamSupplier() {
            @Override
            public float paramI(int index) {
                return paramI[index];
            }

            @Override
            public float paramII(int index) {
                return paramII[index];
            }
        });
    }

    public static float[] dma (final Stock stock, final DayInfo.Type typeForParamI, final float[] paramII) {
        return dma(stock.dayInfos.size(), new ParamSupplier() {
            @Override
            public float paramI(int index) {
                return stock.dayInfos.get(index).getValue(typeForParamI);
            }

            @Override
            public float paramII(int index) {
                return paramII[index];
            }
        });
    }

    public interface ParamSupplier {
        float paramI (int index);
        float paramII (int index);
    }
}
