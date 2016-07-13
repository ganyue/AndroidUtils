package com.android.ganyue.frg;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.ganyue.R;
import com.android.ganyue.controller.FuncCtrl;
import com.gy.appbase.controller.BaseFragmentActivityController;
import com.gy.appbase.fragment.BaseFragment;
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

    private static int count = 0;
    private TcpClient client;
    private TcpServer server;
    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        view.findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (client == null) {
                    client = new TcpClient("192.168.0.104", 12346);
                    client.addTcpClientListener(tcpClientListener);
                    client.start();
                } else {
                    client.send("test");
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
        }

        @Override
        public void onSendBefore(String msg, String dstIp, int dstPort) {
        }

        @Override
        public void onSendSuccess(String msg, String dstIp, int dstPort) {
            Log.d("yue.gan", "send " + msg);
        }

        @Override
        public void onSendFailed(String msg, Exception e, String dstIp, int dstPort) {
            Log.d("yue.gan", "send fail " + msg);
        }

        @Override
        public void onReceive(String msg, String fromIp, int fromPort) {
            Log.d("yue.gan", "receive " + msg);
        }

        @Override
        public void onReceiveError(Exception e, String fromIp, int fromPort) {
            Log.d("yue.gan", "receive error " + e);
        }
    };

    @Override
    protected BaseFragmentActivityController instanceController() {
        return new FuncCtrl(mActivity);
    }

    class ServerThread extends Thread {
        ServerSocket serverSocket;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(12346);

                while (true) {
                    Socket socket = serverSocket.accept();
                    Log.d("yue.gan", "server socket accept");
                    socket.getOutputStream().write("test".getBytes(), 0, 4);
                    socket.getOutputStream().flush();
                    socket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d("yue.gan", "onHiddenChanged server null " + (server == null));
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("yue.gan", "onStart server null " + (server == null));
    }

    class ClientThread extends Thread {
        Socket socket;

        @Override
        public void run() {
            try {
                socket = new Socket("192.168.0.104", 12346);
                BufferedInputStream bin = new BufferedInputStream(socket.getInputStream());
                byte[] buff = new byte[1024];
                int len = bin.read(buff);
                String str = new String(buff, 0, len);
                Log.d("yue.gan", "socket receive : " + str);
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
