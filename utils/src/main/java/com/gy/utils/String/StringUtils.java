package com.gy.utils.string;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ganyu on 2016/7/25.
 *
 */
public class StringUtils {


    private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    /**
     * 转换byte数组成16进制字符串
     * @param b byte数组
     * @return String byte数组处理后字符串
     */
    public static String toHexString(byte[] b) {// String to byte
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }
    /**
     * 转换byte数组成16进制字符串
     * @param b byte数组
     * @return String byte数组处理后字符串
     */
    public static String toHexString(byte[] b, String split) {// String to byte
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
            sb.append(split);
        }
        return sb.toString();
    }

    /**
     * 格式化容量
     * @param number file size (unit: byte)
     */
    public static String formatFileSize(long number) {
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
     * 格式化时间
     * @param second time
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
     *  转换为万或者亿为单位的数据
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

    /**
     * 判断字串是否是0-9的数字组成
     */
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

}
