package com.gy.utils.tcp.httpserver;

import android.content.Context;
import android.util.Log;

import com.gy.utils.tcp.SendItem;
import com.gy.utils.tcp.TcpClient;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.Map;

public class HttpClient implements TcpClient.TcpClientListener {
    private static final String TAG = "HttpClient";

    private Context mCxt;
    private WeakReference<HttpServer> mHttpServer;
    private TcpClient mClient;
    private String unique;
    private String sendFinalTag;

    public HttpClient(Context cxt, Socket socket, HttpServer server) {
        mCxt = cxt.getApplicationContext();
        mHttpServer = new WeakReference<>(server);
        unique = String.valueOf(System.currentTimeMillis());
        mClient = new TcpClient(socket, unique).addTcpClientListener(this);
        mClient.start();
    }

    public void release () {
        mClient.release();
    }

    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ tcp client callback ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ //
    @Override
    public void onSocketConnectFail(String unique, Exception e, String dstIp, int dstPort) {
        Log.d(TAG, "onSocketConnectSuccess --> dstIp=" + dstIp + ", dstPort=" + dstPort);
    }

    @Override
    public void onSocketConnectSuccess(String unique, String dstIp, int dstPort) {
        Log.d(TAG, "onSocketConnectSuccess --> dstIp=" + dstIp + ", dstPort=" + dstPort);
    }

    @Override
    public boolean onSendBefore(String unique, SendItem item, String dstIp, int dstPort) {
        Log.d(TAG, "onSendBefore --> dstIp=" + dstIp + ", dstPort=" + dstPort);
        return false;
    }

    @Override
    public void onSendSuccess(String unique, SendItem item, String dstIp, int dstPort) {
        Log.d(TAG, "onSendSuccess --> dstIp=" + dstIp + ", dstPort=" + dstPort);
        if (item.unique.equals(sendFinalTag)) {
            release();
            if (mHttpServer != null && mHttpServer.get() != null) mHttpServer.get().removeClient(this);
        }
    }

    @Override
    public void onSendFailed(String unique, SendItem item, Exception e, String dstIp, int dstPort) {
        Log.d(TAG, "onSendFailed --> dstIp=" + dstIp + ", dstPort=" + dstPort);
        if (mHttpServer != null && mHttpServer.get() != null) {
            release();
            mHttpServer.get().removeClient(this);
        }
    }

    @Override
    public void onReceive(String unique, String msg, String fromIp, int fromPort) {
        Log.d(TAG, "onReceive --> msg=" + msg);
        if (mHttpServer == null || mHttpServer.get() == null) {
            release();
            return;
        }
        Map<String, RequestMap> map = mHttpServer.get().getRequestMap();
        RequestHttpHead head = RequestHttpHead.parseHead(msg);
        if (head == null || !map.containsKey(head.path)) {
            release();
            mHttpServer.get().removeClient(this);
            return;
        }
        RequestMap requestMap = map.get(head.path);
        if (requestMap instanceof RequestMapAssetHtml) {
            RequestMapAssetHtml requestMapAssetHtml = (RequestMapAssetHtml) requestMap;
            sendFinalTag = String.valueOf(System.currentTimeMillis());
            try {
                InputStream fIn = mCxt.getAssets().open(requestMapAssetHtml.assetPath);
                mClient.sendString("", requestMapAssetHtml.getResponseHead());
                mClient.sendStream(sendFinalTag, fIn);
            } catch (Exception e) {
                e.printStackTrace();
                release();
                mHttpServer.get().removeClient(this);
            }
        }
    }

    @Override
    public void onReceiveError(String unique, Exception e, String fromIp, int fromPort) {
        Log.d(TAG, "onReceiveError --> fromIp=" + fromIp + ", fromPort=" + fromPort);
        if (mHttpServer != null && mHttpServer.get() != null) {
            release();
            mHttpServer.get().removeClient(this);
        }
    }
    // ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ tcp client callback ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ //
    public WeakReference<HttpServerCallback> mCallback;
    public void setServerCallback (HttpServerCallback callback) {
        if (callback == null) mCallback = null;
        mCallback = new WeakReference<>(callback);
    }
    public interface HttpServerCallback {
        void onStartFailed(Exception e);
        void onRequest(String path);//TODO
    }
}
