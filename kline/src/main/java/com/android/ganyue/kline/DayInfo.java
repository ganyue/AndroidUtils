package com.android.ganyue.kline;

import java.util.Locale;

public class DayInfo {

    public int date;    // 00 ~ 03 字节：年月日, 整型
    public float open;  // 04 ~ 07 字节：开盘价
    public float high;  // 08 ~ 11 字节：最高价
    public float low;   // 12 ~ 15 字节：最低价
    public float close; // 16 ~ 19 字节：收盘价
    public double volA;    // 20 ~ 23 字节：成交额（元）
    public long volV;    // 24 ~ 27 字节：成交量（手）
    public float extra;   // 28 ~ 31 字节：保留，一般都是0

    public float rate;  // 涨幅 （当天收盘价 - 前天收盘价）/ 前天收盘价
    public int preDate; // 上个交易日的日期
    public int index; // 本次日线数据在所有数据List中的位置

    public DayInfo() {
    }

    public DayInfo(byte[] data) {
        int tmp;
        tmp = (data[3] & 0x000000ff) << 24;
        tmp += (data[2] & 0x000000ff) << 16;
        tmp += (data[1] & 0x000000ff) << 8;
        tmp += (data[0] & 0x000000ff);
        date = tmp;

        tmp = (data[7] & 0x000000ff) << 24;
        tmp += (data[6] & 0x000000ff) << 16;
        tmp += (data[5] & 0x000000ff) << 8;
        tmp += (data[4] & 0x000000ff);
        open = tmp/100f;

        tmp = (data[11] & 0x000000ff) << 24;
        tmp += (data[10] & 0x000000ff) << 16;
        tmp += (data[9] & 0x000000ff) << 8;
        tmp += (data[8] & 0x000000ff);
        high = tmp/100f;

        tmp = (data[15] & 0x000000ff) << 24;
        tmp += (data[14] & 0x000000ff) << 16;
        tmp += (data[13] & 0x000000ff) << 8;
        tmp += (data[12] & 0x000000ff);
        low = tmp/100f;

        tmp = (data[19] & 0x000000ff) << 24;
        tmp += (data[18] & 0x000000ff) << 16;
        tmp += (data[17] & 0x000000ff) << 8;
        tmp += (data[16] & 0x000000ff);
        close = tmp/100f;

        tmp = (data[23] & 0x000000ff) << 24;
        tmp += (data[22] & 0x000000ff) << 16;
        tmp += (data[21] & 0x000000ff) << 8;
        tmp += (data[20] & 0x000000ff);
        volA = tmp;

        tmp = (data[27] & 0x000000ff) << 24;
        tmp += (data[26] & 0x000000ff) << 16;
        tmp += (data[25] & 0x000000ff) << 8;
        tmp += (data[24] & 0x000000ff);
        volV = tmp;

        tmp = (data[31] & 0x000000ff) << 24;
        tmp += (data[30] & 0x000000ff) << 16;
        tmp += (data[29] & 0x000000ff) << 8;
        tmp += (data[28] & 0x000000ff);
        extra = tmp;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "date=%d, rate=%.2f, open=%.2f, close=%.2f, " +
                        "high=%.2f, low=%.2f, volA=%d, volV=%d, extra=%d",
                date, rate, open, close, high, low, volA, volV, extra);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Integer) return (int)obj == date;
        if (!(obj instanceof DayInfo)) return false;
        return date == ((DayInfo) obj).date;
    }

    public float getValue (Type type) {
        switch (type) {
            case OPEN:
                return open;
            case HIGH:
                return high;
            case LOW:
                return low;
            case CLOSE:
                return close;
            case VOL:
                return volV;
            case RATE:
                return rate;
        }
        return 0;
    }

    public enum Type {
        OPEN,
        HIGH,
        LOW,
        CLOSE,
        VOL,
        RATE
    }
}
