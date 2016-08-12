package com.android.ganyue.frg;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.ganyue.R;
import com.android.ganyue.controller.FuncCtrl;
import com.gy.appbase.controller.BaseFragmentActivityController;
import com.gy.appbase.fragment.BaseFragment;
import com.gy.appbase.inject.ViewInject;
import com.gy.utils.log.LogUtils;
import com.gy.utils.tcp.TcpClient;
import com.gy.utils.tcp.TcpServer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by ganyu on 2016/4/30.
 * Nsd test
 */
public class TcpTestFrg extends BaseFragment {

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tcp_test, null);
    }

    @ViewInject (R.id.btn_reconnect)    Button btnReconnect;
    @ViewInject (R.id.btn_clearLog)     Button btnClearLog;
    @ViewInject (R.id.tv_log)           TextView tvLog;

    private static int count = 0;
    private TcpClient client;
    private TcpServer server;
    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        view.findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (client == null) {
                    client = new TcpClient("192.168.0.102", 12346);
                    client.addTcpClientListener(tcpClientListener);
                    client.start();
                    tvLog.setText(""+tvLog.getText() + "\nclient created");
                } else {
                    client.send("test");
                    tvLog.setText(""+tvLog.getText() + "\nsend test");
                }
            }
        });
        view.findViewById(R.id.btn_server).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (server ==  null) {
                    server = new TcpServer(12346);
                    server.setTcpServerListener(tcpServerListener);
                    server.start();
                }
            }
        });

        btnReconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d("yue.gan", "isconnected : " + client.isConnected());
                if (!client.isConnected()) {
                    client.release();
                    if (!TextUtils.isEmpty(client.getDstIp())) {
                        client = new TcpClient(client.getDstIp(), client.getDstPort());
                        client.start();
                    }
                }
            }
        });

        btnClearLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvLog.setText("");
            }
        });
    }

    TcpServer.TcpServerListener tcpServerListener = new TcpServer.TcpServerListener() {
        @Override
        public void onSererStartFail(Exception e) {
            Log.d("yue.gan", "server start fail");
        }

        @Override
        public void onAccept(Socket socket) {
            Log.d("yue.gan", "accept");
            client = new TcpClient(socket);
            client.addTcpClientListener(tcpClientListener);
            client.start();
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvLog.setText(""+tvLog.getText() + "\naccept");
                }
            });
        }

        @Override
        public void onAcceptError(IOException e) {
            Log.d("yue.gan", "accept error : " + e);
        }
    };

    TcpClient.TcpClientListener tcpClientListener = new TcpClient.TcpClientListener() {
        @Override
        public void onSocketConnectFail(Exception e, String dstIp, int dstPort) {
            Log.d("yue.gan", "socket connect fail " + e);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvLog.setText(""+tvLog.getText() + "\nconnect fail");
                }
            });
        }

        @Override
        public void onSendBefore(String msg, String dstIp, int dstPort) {
        }

        @Override
        public void onSendSuccess(final String msg, String dstIp, int dstPort) {
            Log.d("yue.gan", "send " + msg);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvLog.setText(""+tvLog.getText() + "\nsend success " + msg);
                }
            });
        }

        @Override
        public void onSendFailed(final String msg, Exception e, String dstIp, int dstPort) {
            Log.d("yue.gan", "send fail " + msg);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvLog.setText(""+tvLog.getText() + "\nsend fail " + msg);
                }
            });
        }

        @Override
        public void onReceive(final String msg, String fromIp, int fromPort) {
            Log.d("yue.gan", "receive " + msg);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvLog.setText(""+tvLog.getText() + "\nreceive " + msg);
                }
            });
        }

        @Override
        public void onReceiveError(Exception e, String fromIp, int fromPort) {
            Log.d("yue.gan", "receive error " + e);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvLog.setText(""+tvLog.getText() + "\nreceive error ");
                }
            });
        }
    };

    @Override
    protected BaseFragmentActivityController instanceController() {
        return new FuncCtrl(mActivity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (server != null) {
            server.release();
        }

        if (client != null) {
            client.release();
        }
    }
}
