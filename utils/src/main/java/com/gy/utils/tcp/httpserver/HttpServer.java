package com.gy.utils.tcp.httpserver;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

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
    private String ip;
    private List<HttpClient> mClients = new ArrayList<>();
    private Map<String, RequestMap> mRequestMap = new HashMap<>();

    public HttpServer (Context cxt, int port) {
        this.port = port;
        mCxt = cxt.getApplicationContext();
    }

    public String getHostAddress () {
        return TextUtils.isEmpty(ip)? null: ip + ":" + port;
    }

    public boolean hasPath (String path) {
        return mRequestMap.containsKey(path);
    }

    public HttpServer addCustomHtmlResFromAssets (String path, String resAssetsPath,
                                                  RequestMapCustomHtmlResFromAssets.HtmlSupplier supplier) {
        mRequestMap.put(path, new RequestMapCustomHtmlResFromAssets(path, resAssetsPath, supplier));
        return this;
    }

    public HttpServer addAssetHtml (String path, String assetPath) {
        mRequestMap.put(path, new RequestMapAssetHtml(path, assetPath));
        return this;
    }

    public HttpServer addAssetFile (String path, String assetPath) {
        mRequestMap.put(path, new RequestMapAssetFile(path, assetPath));
        return this;
    }

    public HttpServer addSdcardFile (String path, String sdcardPath) {
        mRequestMap.put(path, new RequestMapSdcardFile(path, sdcardPath));
        return this;
    }

    public HttpServer addWebSocket (String path, RequestMapWebSocket.OnWebSocketCallback callback) {
        mRequestMap.put(path, new RequestMapWebSocket(path, callback));
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
        if (mServer != null) mServer.release();
        mServer = null;
        return this;
    }

    public void release () {
        for (RequestMap r: mRequestMap.values()) {
            r.release();
        }
        List<HttpClient> clients = new ArrayList<>(mClients);
        for (HttpClient client: clients) {
            try {
                client.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        clients.clear();
        stop();
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
        this.ip = ip;
        this.port = port;
        if (mCallback != null && mCallback.get() != null) {
            mCallback.get().onStartSuccess(ip, port);
        }
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
    public HttpServer setServerCallback (HttpServerCallback callback) {
        if (callback == null) mCallback = null;
        else mCallback = new WeakReference<>(callback);
        return this;
    }
    public interface HttpServerCallback {
        void onStartFailed (Exception e);
        void onStartSuccess (String ip, int port);//TODO
    }
}
