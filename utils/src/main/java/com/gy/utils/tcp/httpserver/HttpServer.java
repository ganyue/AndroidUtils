package com.gy.utils.tcp.httpserver;

import android.content.Context;
import android.util.Log;

import com.gy.utils.tcp.TcpClient;
import com.gy.utils.tcp.TcpServer;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpServer implements TcpServer.TcpServerListener {
    private static final String TAG = "HttpServer";

    private Context mCxt;
    private TcpServer mServer;
    private int port = 8088;
    private List<HttpClient> mClients = new ArrayList<>();
    private Map<String, RequestMap> mRequestMap = new HashMap<>();

    public HttpServer (Context cxt, int port) {
        this.port = port;
        mCxt = cxt.getApplicationContext();
    }

    public HttpServer addAssetHtml (String path, String assetPath) {
        mRequestMap.put(path, new RequestMapAssetHtml(path, assetPath));
        return this;
    }

    public HttpServer addAssetFile (String path, String assetPath) {
        mRequestMap.put(path, new RequestMapAssetFile(path, assetPath));
        return this;
    }

    public HttpServer start () {
        if (mServer == null) {
            mServer = new TcpServer(mCxt, port);
        }
        mServer.start();
        mServer.setTcpServerListener(this);
        return this;
    }

    public HttpServer stop () {
        mServer.release();
        mServer = null;
        return this;
    }

    public Map<String, RequestMap> getRequestMap () {
        return mRequestMap;
    }

    public void removeClient (HttpClient client) {
        mClients.remove(client);
    }

    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ tcp server callback ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ //
    @Override
    public void onServerStartSuccess(String ip, int port) {
        Log.d(TAG, "onServerStartSuccess --> ip=" + ip + ", port=" + port);
    }

    @Override
    public void onSererStartFail(Exception e) {
        if (mCallback != null && mCallback.get() != null) {
            mCallback.get().onStartFailed(e);
        }
    }

    @Override
    public void onAccept(Socket socket) {
        Log.d(TAG, "onAccept --> socket=" + socket);
        mClients.add(new HttpClient(mCxt, socket, this));
    }

    @Override
    public void onAcceptError(IOException e) {
        Log.d(TAG, "onAcceptError --> e=" + e);
    }
    // ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ tcp server callback ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ //

    public WeakReference<HttpServerCallback> mCallback;
    public void setServerCallback (HttpServerCallback callback) {
        if (callback == null) mCallback = null;
        mCallback = new WeakReference<>(callback);
    }
    public interface HttpServerCallback {
        void onStartFailed (Exception e);
        void onRequest (String path);//TODO
    }
}
