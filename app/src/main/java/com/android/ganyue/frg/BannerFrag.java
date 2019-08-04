package com.android.ganyue.frg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.ganyue.R;
import com.gy.appbase.fragment.BaseFragment;
import com.gy.appbase.inject.ViewInject;
import com.gy.widget.viewpager.banner.BannerCallback;
import com.gy.widget.viewpager.banner.BannerView;
import com.gy.widget.viewpager.banner.FirstStartGuideView;

/**
 * Created by yue.gan on 2016/10/8.
 *
 */
public class BannerFrag extends BaseFragment {

    @ViewInject(R.id.banner)    private BannerView mBanner;
    @ViewInject(R.id.guide)     private FirstStartGuideView mGuide;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_banner, container, false);
    }

    @Override
    protected void initViews(View view) {
        mBanner.setBannerCallback(bannerCallback);
        mBanner.setCount(3);
        mBanner.setAutoStart(true, 3000);

        mGuide.setBannerCallback(bannerCallback);
        mGuide.setCount(3);

        int[] array = new int[2];
        array[3] = 2;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void activityCall(int type, Object extra) {

    }

    private BannerCallback bannerCallback = new BannerCallback() {
        @Override
        public void displayImage(ImageView imageView, int pos) {
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            if (pos == 0) {
                imageView.setImageResource(R.mipmap.ic_launcher);
            } else if (pos == 1) {
                imageView.setImageResource(R.mipmap.img_refresh_drawable_buildings);
            } else if (pos == 2) {
                imageView.setImageResource(R.mipmap.img_refresh_drawable_sky);
            }
        }

        @Override
        public int getIndicatorBottomMargin() {
            return (int) (getResources().getDisplayMetrics().density * 20);
        }

        @Override
        public void onItemClick(ImageView imageView, int pos) {
            Toast.makeText(mActivity, "clicked : " + pos, Toast.LENGTH_SHORT).show();
        }
    };
}
