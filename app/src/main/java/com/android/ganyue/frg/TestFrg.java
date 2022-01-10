package com.android.ganyue.frg;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Locale;

import link.zhidou.scan.translation.service.IScanEventCallback;
import link.zhidou.scan.translation.service.IScanEventDispatcherService;

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

    private IScanEventCallback.Stub scanEventCallback = new IScanEventCallback.Stub() {
        @Override
        public void onScanStart() throws RemoteException {
            Log.d("test", "onScanStart");
        }

        @Override
        public void onScanning(String intermediateResult) throws RemoteException {
            Log.d("test", "onScanning intermediateResult="+intermediateResult);

        }

        @Override
        public void onScanStop(String finalResult) throws RemoteException {
            Log.d("test", "onScanStop finalResult="+finalResult);

        }
    };

    private IScanEventDispatcherService scanEventDispatcherService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("test", "onServiceConnected");
            try {
                scanEventDispatcherService = IScanEventDispatcherService.Stub.asInterface(service);
                scanEventDispatcherService.registerScanCallback(scanEventCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("test", "onServiceDisconnected");
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("test", "onDetach");
        try {
            scanEventDispatcherService.unRegisterScanCallback(scanEventCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        getContext().unbindService(serviceConnection);
    }

    private HttpServer server;
    int count = 0;
    @Override
    protected void initViews(View view) {
        mVStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent("link.zhidou.scan.translation.PureScanText");
////                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivityForResult(intent, 0);


                Intent i = new Intent();
                i.setComponent(new ComponentName("link.zhidou.scan.translation",
                        "link.zhidou.scan.translation.service.ScanEventDispatcherService"));
                getContext().bindService(i, serviceConnection, Service.BIND_AUTO_CREATE);




//                server = new HttpServer(getContext(), 8080)
//                        .addAssetHtml("/test", "TestHttpServer/index.html")
//                        .addAssetFile("/file", "TestHttpServer/test.txt")
//                        .addCustomHtmlResFromAssets("/custom", "TestHttpServer", supplier)
//                        .start();
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

                try {
                    File f = new File(getContext().getExternalCacheDir(), "serial.txt");
                    FileOutputStream fout = new FileOutputStream(f);
                    int startNum = 0x617;
                    int i = startNum;
                    for (; i < startNum + 1229; i++) {
                        String formatStr = String.format(Locale.getDefault(), "%06xAA014E\n", i).toUpperCase();
                        fout.write(formatStr.getBytes());
                        Log.d("devel", "write : " + (i - startNum + 1) + ", " + formatStr);
                    }
                    fout.close();
                    Log.d("devel", "done");
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                String str = "\n";
//                for (int i = 361; i < 722; i++) {
//                    str+="<dimen name=\"dp_"+i+"\">"+ i +"dp</dimen>\n";
//                    if ((i - 361) % 100 == 0) {
//                        Log.d("devel", str);
//                        str = "\n";
//                    }
//                }
//                Log.d("devel", str);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            Toast.makeText(getContext(), "null", Toast.LENGTH_SHORT).show();
        } else
        Toast.makeText(getContext(), data.getStringExtra("scan_result"), Toast.LENGTH_SHORT).show();
    }
}
