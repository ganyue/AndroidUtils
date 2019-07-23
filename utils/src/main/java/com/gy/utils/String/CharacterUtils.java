package com.gy.utils.String;

import android.os.Build;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;

/**
 * created by yue.gan 19-1-19
 */
public class CharacterUtils {

    /**
     * 判别字符是否是汉字
     */
    public static boolean isChineseByBlock (char c) {
        if (Build.VERSION.SDK_INT >= 24) {
            Character.UnicodeScript sc = Character.UnicodeScript.of(c);
            return sc == Character.UnicodeScript.HAN;
        }
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT;
    }

    /**
     * 判别字符是否是中文标点符号
     */
    public static boolean isChinesePunctuation(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS
                || ub == Character.UnicodeBlock.VERTICAL_FORMS;
    }

    // 存放国标一级汉字不同读音的起始区位码
    static final int[] secPosValueList = { 1601, 1637, 1833, 2078, 2274, 2302,
            2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858, 4027,
            4086, 4390, 4558, 4684, 4925, 5249, 5600 };
    // 存放国标一级汉字不同读音的起始区位码对应读音
    static final char[] firstLetter = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'w', 'x',
            'y', 'z' };
    /**
     * 获取字符串在a-g的首字母（仅限中英文）
     *
     * 获取一个汉字的拼音首字母。 GB码两个字节分别减去160，转换成10进制码组合就可以得到区位码
     * 例如汉字“你”的GB码是0xC4/0xE3，分别减去0xA0（160）就是0x24/0x43
     * 0x24转成10进制就是36，0x43是67，那么它的区位码就是3667，在对照表中读音为‘n’
     */
    public static char getFirstLetter (String str) {
        if (TextUtils.isEmpty(str)) return 'z';
        char c = str.charAt(0);
        if (isChineseByBlock(c)) {
            byte[] gbkBytes;
            try {
                gbkBytes = String.valueOf(c).getBytes("GBK");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return 'z';
            }

            int secPosValue = 0;
            int i;
            for (i = 0; i < gbkBytes.length; i++) {
                gbkBytes[i] -= 160;
            }
            secPosValue = gbkBytes[0] * 100 + gbkBytes[1];
            for (i = 0; i < 23; i++) {
                if (secPosValue >= secPosValueList[i]
                        && secPosValue < secPosValueList[i + 1]) {
                    return firstLetter[i];
                }
            }
            return 'z';

        }
        return c;
    }
}
