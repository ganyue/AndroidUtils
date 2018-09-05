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
import com.gy.utils.tcp.TcpClient;
import com.gy.utils.tcp.TcpServer;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by ganyu on 2016/4/30.
 * Nsd test
 */
public class TcpTestFrg extends BaseFragment implements View.OnClickListener{

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tcp_test, null);
    }

    @ViewInject (R.id.btn_start_server)     Button btnStartServer;
    @ViewInject (R.id.edt_port)             EditText edtPort;
    @ViewInject (R.id.btn_connect)          Button btnConnect;
    @ViewInject (R.id.edt_ip_dst)           EditText edtIpDst;
    @ViewInject (R.id.edt_port_dst)         EditText edtPortDst;
    @ViewInject (R.id.btn_send)             Button btnSend;
    @ViewInject (R.id.edt_msg)              EditText edtMsg;
    @ViewInject (R.id.btn_clearLog)         Button btnClearLog;
    @ViewInject (R.id.tv_log)               TextView tvLog;

    private TcpClient tcpClient;
    private TcpServer tcpServer;
    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        btnStartServer.setOnClickListener(this);
        btnConnect.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        btnClearLog.setOnClickListener(this);
        edtPort.setText(""+6600);
        edtPortDst.setText(""+6600);
        edtIpDst.setText("192.168.");
    }

    private TcpServer.TcpServerListener tcpServerListener = new TcpServer.TcpServerListener() {
        @Override
        public void onSererStartFail(Exception e) {
            writeLog("server start fail");
        }

        @Override
        public void onAccept(Socket socket) {
            writeLog("accept connect");
            if (tcpClient != null) tcpClient.release();
            tcpClient = new TcpClient(socket);
            tcpClient.enableHart(false);
            tcpClient.addTcpClientListener(tcpClientListener);
            tcpClient.start();
        }

        @Override
        public void onAcceptError(IOException e) {
            writeLog("accept error");
        }
    };

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
            writeLog("send failed : " + msg + " \nto " + dstIp + ":" + dstPort);
        }

        @Override
        public void onReceive(String msg, String fromIp, int fromPort) {
            writeLog("receive : " + msg + " \nfrom " + fromIp + ":" + fromPort);
        }

        @Override
        public void onReceiveError(Exception e, String fromIp, int fromPort) {
            writeLog("receive error : from " + fromIp + ":" + fromPort);
        }
    };
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_server:
                if (tcpServer!=null) tcpServer.release();
                tcpServer = new TcpServer(Integer.parseInt(""+edtPort.getText()));
                tcpServer.setTcpServerListener(tcpServerListener);
                tcpServer.start();
                writeLog("server start");
                break;
            case R.id.btn_connect:
                if (tcpClient != null) tcpClient.release();
                tcpClient = new TcpClient(""+edtIpDst.getText(), Integer.parseInt(""+edtPort.getText()));
                tcpClient.enableHart(false);
                tcpClient.addTcpClientListener(tcpClientListener);
                tcpClient.start();
                writeLog("client connecting");
                break;
            case R.id.btn_send:
                if (tcpClient != null) {
                    tcpClient.send(""+edtMsg.getText());
                }
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

    @Override
    protected BaseFragmentActivityController instanceController() {
        return new FuncCtrl(mActivity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (tcpServer != null) {
            tcpServer.release();
        }

        if (tcpClient != null) {
            tcpClient.release();
        }
    }

}
