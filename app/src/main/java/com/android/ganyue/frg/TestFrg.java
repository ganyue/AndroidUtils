package com.android.ganyue.frg;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.ganyue.R;
import com.android.ganyue.kline.DayInfo;
import com.android.ganyue.kline.OptimizerDMACV;
import com.android.ganyue.kline.Stock;
import com.android.ganyue.kline.StockParser;
import com.android.ganyue.logcat.LogUtils;
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
    int count = 0;
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

                String str = "\n";
                for (int i = 361; i < 722; i++) {
                    str+="<dimen name=\"dp_"+i+"\">"+ i +"dp</dimen>\n";
                    if ((i - 361) % 100 == 0) {
                        Log.d("devel", str);
                        str = "\n";
                    }
                }
                Log.d("devel", str);

//                LogUtils.enableLogServer(v.getContext(),true, 8088);
//                LogUtils.d("test " + count++);
//                new OptimizerDMACV(v.getContext()).start();


//                StockParser parser = new StockParser();
//                parser.init(v.getContext())
//                        .setParseCallback(new StockParser.OnParseCallback() {
//                            @Override
//                            public void onResult(String path, Stock stock) {
//                                Log.d("yue.gan", "parse result : " + path + " code=" + stock.code);
//
//                                List<DayInfo> dayInfos = stock.getCustomFormulaResult();
////                                List<DayInfo> dayInfos = stock.dayInfos;
//                                for (DayInfo info: dayInfos) {
//                                    Log.d("yue.gan", info.toString());
//                                }
//                            }
//
//                            @Override
//                            public void onError(String path, String msg, Exception e) {
//                                e.printStackTrace();
//                            }
//                        }).parseStockAsync("sz/lday/sz002083.day");
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
