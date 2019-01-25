package com.gy.pinyin;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.system.Os;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.io.IIOAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * created by yue.gan 19-1-25
 */
public class PinyinInit {

    public static void initHanLP(Context context) {
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                Os.setenv("HANLP_ROOT", "", true);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } else {
            System.setProperty("HANLP_ROOT", "");
        }
        final AssetManager assetManager = context.getAssets();
        HanLP.Config.IOAdapter = new IIOAdapter() {
            @Override
            public InputStream open(String path) throws IOException {
                return assetManager.open(path);
            }

            @Override
            public OutputStream create(String path) throws IOException {
                throw new IllegalAccessError("不支持写入" + path + "！请在编译前将需要的数据放入app/src/main/assets/data");
            }
        };
    }

    // 汉语转拼音： HALP.convert..., 拼音转换是比较耗时的，转换字符越多耗时越长
}
