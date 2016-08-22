package com.gy.utils.String;

/**
 * Created by ganyu on 2016/7/25.
 *
 */
public class StringUtils {


    private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    /**
     * @param b byte数组
     * @return String byte数组处理后字符串
     */
    public static String toHexString(byte[] b) {// String to byte
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
            sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * @param number file size (unit: byte)
     */
    public static String formatFileSize(int number) {
        float result = number;
        String suffix = "B";
        if (result > 900) {
            suffix = "KB";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = "MB";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = "GB";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = "TB";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = "PB";
            result = result / 1024;
        }
        if (result < 100) {
            return String.format("%.2f%s", result, suffix);
        } else {
            return String.format("%.0f%s", result, suffix);
        }
    }

    /**
     * @param second
     */
    public static String formatTimeSecond(int  second) {
        if (second < 3600) {
            return String.format("%02d:%02d", second / 60,
                    (second % 60));
        } else {
            return String.format("%02d:%02d:%02d", second/3600, (second % 3600) / 60,
                    (second % 60));
        }
    }

    /**
     *
     */
    public static String formatCountNumber(long cntNum) {
        if (cntNum < 10000) {
            return String.valueOf(cntNum);
        }
        if (cntNum < 100000000) {
            return String.format("%d万", cntNum/10000);
        }
        return String.format("%.1f亿", new Long(cntNum/100000000).intValue());
    }

}
