package com.android.ganyue.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.ganyue.R;
import com.android.ganyue.controller.FuncCtrl;
import com.android.ganyue.frg.HorizontalIndicatorFrg;
import com.gy.appbase.activity.BaseFragmentActivity;
import com.gy.appbase.controller.BaseFragmentActivityController;

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

    private FuncCtrl controller;

    public void setController (BaseFragmentActivityController controller) {
        super.setController(controller);
        this.controller = (FuncCtrl) controller;
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
            this.controller = new FuncCtrl(this);
            controller.showFragment(getSupportFragmentManager(), true, null, R.id.flyt_content, clazz, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected BaseFragmentActivityController instanceController() {
        return new FuncCtrl(this);
    }
}
