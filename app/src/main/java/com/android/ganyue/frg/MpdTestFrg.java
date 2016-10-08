package com.android.ganyue.frg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.ganyue.R;
import com.android.ganyue.controller.FuncCtrl;
import com.gy.appbase.controller.BaseFragmentActivityController;
import com.gy.appbase.fragment.BaseFragment;
import com.gy.appbase.inject.ViewInject;
import com.gy.utils.audio.mpd.MpdConsts;
import com.gy.utils.tcp.TcpClient;

/**
 * Created by ganyu on 2016/4/30.
 * Nsd test
 */
public class MpdTestFrg extends BaseFragment implements View.OnClickListener{

    @ViewInject (R.id.tv_log)           TextView tvLog;
    @ViewInject (R.id.btn_clearLog)     Button btnClearLog;
    @ViewInject (R.id.edt_ip)           EditText edtIP;
    @ViewInject (R.id.edt_port)         EditText edtPort;
    @ViewInject (R.id.btn_connect)      Button btnConnect;
    @ViewInject (R.id.btn_sendMsg)      Button btnSend;
    @ViewInject (R.id.edt_msg)      EditText edtMsg;
    @ViewInject (R.id.btn_enableHart)      Button btnHart;
    @ViewInject (R.id.btn_enableReconnect)      Button btnReconnect;

    private boolean enableReconnect = false;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mpd, null);
    }

    private TcpClient tcpClient;

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        btnClearLog.setOnClickListener(this);
        btnConnect.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        btnHart.setOnClickListener(this);
        btnReconnect.setOnClickListener(this);
        edtPort.setText(""+6600);
        edtIP.setText("192.168.");
        edtMsg.setText("status");
    }

    @Override
    protected BaseFragmentActivityController instanceController() {
        return new FuncCtrl(mActivity);
    }

    private TcpClient.TcpClientListener tcpClientListener = new TcpClient.TcpClientListener() {
        @Override
        public void onSocketConnectFail(Exception e, String dstIp, int dstPort) {
            writeLog("connect fail : " + dstIp + ":" + dstPort);
        }

        @Override
        public void onSocketConnectSuccess(String dstIp, int dstPort) {
            writeLog("connect success : " + dstIp + ":" + dstPort);
        }

        @Override
        public boolean onSendBefore(String msg, String dstIp, int dstPort) {
            return false;
        }

        @Override
        public void onSendSuccess(String msg, String dstIp, int dstPort) {
            writeLog("send success : " + msg + " \nto " + dstIp + ":" + dstPort);
        }

        @Override
        public void onSendFailed(String msg, Exception e, String dstIp, int dstPort) {
            writeLog("send failed : " + msg + " \nto " + dstIp + ":" + dstPort + " \n--- it must be disconnected");
            if (enableReconnect) {
                boolean enableHart = false;
                if (tcpClient != null) {
                    enableHart = tcpClient.isHartEnabled();
                    tcpClient.release();
                }
                tcpClient = new TcpClient(""+edtIP.getText(), Integer.parseInt(""+edtPort.getText()));
                tcpClient.enableHart(enableHart);
                tcpClient.addTcpClientListener(tcpClientListener);
                tcpClient.start();
                writeLog("reconnect");
            }
        }

        @Override
        public void onReceive(String msg, String fromIp, int fromPort) {
            writeLog("receive : " + msg + " \nfrom " + fromIp + ":" + fromPort);
        }

        @Override
        public void onReceiveError(Exception e, String fromIp, int fromPort) {
            writeLog("receive error : from " + fromIp + ":" + fromPort + " \n--- it must be disconnected");
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_connect:
                String ip = edtIP.getText().toString();
                String port = edtPort.getText().toString();
                writeLog("connect : " + ip + ":"+port);

                if (tcpClient != null) tcpClient.release();
                tcpClient = new TcpClient(ip, Integer.parseInt(port));
                tcpClient.enableHart(false);
                tcpClient.addTcpClientListener(tcpClientListener);
                tcpClient.start();
                break;
            case R.id.btn_sendMsg:
                if (tcpClient != null) {
                    String cmdStr = ""+edtMsg.getText();
                    if (cmdStr.contains("volume")) {
                        cmdStr = MpdConsts.getCommandStr(cmdStr, "0");
                    } else {
                        cmdStr = MpdConsts.getCommandStr(cmdStr);
                    }
                    tcpClient.send(cmdStr);
                }
                break;
            case R.id.btn_enableHart:
                writeLog("enable Hart");
                if (tcpClient != null) {
                    tcpClient.enableHart(true);
                }
                break;
            case R.id.btn_enableReconnect:
                writeLog("enable reconnect");
                enableReconnect = true;
                break;
            case R.id.btn_clearLog:
                tvLog.setText("");
                break;

        }
    }

    private void writeLog (final String log) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvLog.setText(tvLog.getText()+"\n" + log);
            }
        });
    }
}
