package com.android.ganyue.frg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.ganyue.R;
import com.gy.appbase.fragment.BaseFragment;
import com.gy.appbase.inject.ViewInject;
import com.gy.utils.tcp.httpserver.HttpServer;
import com.gy.utils.tcp.httpserver.RequestMapCustomHtmlResFromAssets;

/**
 * created by yue.gan 18-9-22
 */
public class TestFrg extends BaseFragment{

    @ViewInject(R.id.mVStart) TextView mVStart;
    @ViewInject(R.id.mVStop) TextView mVStop;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    private HttpServer server;
    @Override
    protected void initViews(View view) {
        mVStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                server = new HttpServer(getContext(), 8080)
                        .addAssetHtml("/test", "TestHttpServer/index.html")
                        .addAssetFile("/file", "TestHttpServer/test.txt")
                        .addCustomHtmlResFromAssets("/custom", "TestHttpServer", supplier)
                        .start();
            }
        });
        mVStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                server.stop();
            }
        });


    }


    private RequestMapCustomHtmlResFromAssets.HtmlSupplier supplier = new RequestMapCustomHtmlResFromAssets.HtmlSupplier() {
        @Override
        public String getHtml() {
            return "this is test for CustomHtmlResFromAssets";
        }

        @Override
        public String getReferHtml() {
            return null;
        }
    };

    @Override
    public void onClick(View v) {

    }

    @Override
    public void activityCall(int type, Object extra) {

    }
}
