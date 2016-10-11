package com.gy.widget.viewpager.banner;

import android.widget.ImageView;

/**
 * Created by ganyu on 2016/10/12.
 *
 * <p>必须实现此接口，在displayImage方法中对imageView设置图片</p>
 * <p>在getIndicatorBottomMargin中返回indicator应该距离底部的margin</p>
 * <p>onItemClick方法，你懂的~</p>
 */
public interface BannerCallback {
    void displayImage(ImageView imageView, int pos);
    int getIndicatorBottomMargin();
    void onItemClick(ImageView imageView, int pos);
}
