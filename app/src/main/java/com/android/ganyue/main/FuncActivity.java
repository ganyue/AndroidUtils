package com.android.ganyue.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.android.ganyue.R;
import com.android.ganyue.controller.FuncCtrl;
import com.android.ganyue.frg.HorizontalIndicatorFrg;
import com.gy.appbase.activity.BaseFragmentActivity;
import com.gy.utils.reflect.ReflectUtil;

/**
 * Created by yue.gan on 2016/4/30.
 *
 */
public class FuncActivity extends BaseFragmentActivity{


    public static void startSelf (Context context, String type) {
        Intent intent = new Intent(context, FuncActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_func);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");

        try {
            Class clazz = Class.forName(HorizontalIndicatorFrg.class.getPackage().getName() + "." + type);
            getSupportFragmentManager().beginTransaction().replace(R.id.flyt_content, (Fragment) ReflectUtil.newInstance(clazz))
                    .disallowAddToBackStack().commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void fragmentCall(int type, Object extra) {

    }
}
