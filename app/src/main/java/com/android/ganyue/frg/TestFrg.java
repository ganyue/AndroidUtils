package com.android.ganyue.frg;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.ganyue.R;
import com.android.ganyue.kline.DayInfo;
import com.android.ganyue.kline.Stock;
import com.android.ganyue.kline.StockParser;
import com.gy.appbase.fragment.BaseFragment;
import com.gy.appbase.inject.ViewInject;
import com.gy.utils.tcp.httpserver.HttpServer;
import com.gy.utils.tcp.httpserver.RequestHttpHead;
import com.gy.utils.tcp.httpserver.RequestMapCustomHtmlResFromAssets;

import java.util.List;

/**
 * created by yue.gan 18-9-22
 */
public class TestFrg extends BaseFragment{

    @ViewInject(R.id.mVStart) TextView mVStart;
    @ViewInject(R.id.mVStop) TextView mVStop;
    @ViewInject(R.id.mVParse) TextView mVParse;

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

        mVParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StockParser parser = new StockParser();
                parser.init(v.getContext())
                        .setParseCallback(new StockParser.OnParseCallback() {
                            @Override
                            public void onResult(String path, Stock stock) {
                                Log.d("yue.gan", "parse result : " + path + " code=" + stock.code);

                                List<DayInfo> dayInfos = stock.getCustomFormulaResult();
//                                List<DayInfo> dayInfos = stock.dayInfos;
                                for (DayInfo info: dayInfos) {
                                    Log.d("yue.gan", info.toString());
                                }
                            }

                            @Override
                            public void onError(String path, String msg, Exception e) {
                                e.printStackTrace();
                            }
                        }).parseStockAsync("sz002083.day");
            }
        });
    }


    private RequestMapCustomHtmlResFromAssets.HtmlSupplier supplier = new RequestMapCustomHtmlResFromAssets.HtmlSupplier() {
        @Override
        public String getHtml(RequestHttpHead head) {
            return "this is test for CustomHtmlResFromAssets";
        }

        @Override
        public String getReferHtml(String refer, RequestHttpHead head) {
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
