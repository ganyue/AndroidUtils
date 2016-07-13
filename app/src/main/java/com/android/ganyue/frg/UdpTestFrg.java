package com.android.ganyue.frg;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.ganyue.R;
import com.android.ganyue.controller.FuncCtrl;
import com.gy.appbase.controller.BaseFragmentActivityController;
import com.gy.appbase.fragment.BaseFragment;
import com.gy.utils.udp.UdpSpeaker;
import com.gy.utils.udp.UdpSpeakerCallback;

import java.lang.reflect.Field;

/**
 * Created by ganyu on 2016/4/30.
 * Nsd test
 */
public class UdpTestFrg extends BaseFragment {


    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_udp_test, null);
    }

    private static int count = 0;
    private UdpSpeaker udpSpeaker18000;
    private UdpSpeaker udpSpeaker17000;
    private UdpSpeaker udpSpeaker18001;
    private TextView logText;

    Float a;
    Integer b;
    boolean c;
    String d;
    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName();
            Class type = field.getType();
            String typeName = type.getName();
            boolean isPrim = type.isPrimitive();
            Log.d("haha", "haha");
        }
        logText = (TextView) view.findViewById(R.id.tv_log);
        udpSpeaker18000 = new UdpSpeaker(18000);
        udpSpeaker18000.addCallback(udpSpeakerCallback);
        udpSpeaker17000 = new UdpSpeaker(17000);
        udpSpeaker17000.addCallback(udpSpeakerCallback);
        udpSpeaker18001 = new UdpSpeaker(18001);
        udpSpeaker18001.addCallback(udpSpeakerCallback);

        view.findViewById(R.id.btn_keepAlive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                udpSpeaker17000.send(getKeepAliveStr(), "255.255.255.255", 18000);
            }
        });
        view.findViewById(R.id.btn_sdcardInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                udpSpeaker17000.send(getSDcardInfoStr(), "255.255.255.255", 18000);
            }
        });
        view.findViewById(R.id.btn_battery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                udpSpeaker17000.send(getBatteryInfoStr(), "255.255.255.255", 18000);
            }
        });

        view.findViewById(R.id.btn_clearLog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logText.setText("");
            }
        });
    }

    public static String getKeepAliveStr () {
        return "req:keepalive\r\ncseq:" + count++ + "\r\n\r\n";
    }

    public static String getSDcardInfoStr(){
        StringBuffer sb = new StringBuffer();
        sb.append("req:/opt/ipnc/IPCShell GETSD ");
        sb.append("\r\n\r\n");
        return  sb.toString();
    }

    public static String getBatteryInfoStr(){
        StringBuffer sb = new StringBuffer();
        sb.append("req:/opt/ipnc/IPCShell GETBAT ");
        sb.append("\r\n\r\n");
        return  sb.toString();
    }

    private UdpSpeakerCallback udpSpeakerCallback = new UdpSpeakerCallback() {
        @Override
        public boolean onSendBefore(String msg, String ip, int port) {
            return false;
        }

        @Override
        public void onSendSuccess(String msg, String ip, int port) {
            log("sended : " + msg + " tport:" + port);
        }

        @Override
        public void onSendFailed(String msg, String ip, int port, Exception e) {
            log("send fail : " + msg);
        }

        @Override
        public void onReceive(String msg, String ip, int port) {
            log("receive : " + msg + " from：" + ip + ":"+port);
        }

        @Override
        public void onReceiveError(Exception e) {
            log("receive error : " + e);
        }
    };

    private void log (String msg) {
        Message message = Message.obtain();
        message.obj = msg;
        handler.sendMessage(message);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            logText.setText(msg.obj+"\n"+logText.getText()+"\n");
        }
    };

    @Override
    protected BaseFragmentActivityController instanceController() {
        return new FuncCtrl(mActivity);
    }
}
