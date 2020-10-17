package com.gy.utils.tcp.httpserver;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.gy.utils.tcp.SendItem;
import com.gy.utils.tcp.TcpClient;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.Map;

public class HttpClient implements TcpClient.TcpClientListener, IHttpClient {
    private static final String TAG = "HttpClient";

    private Context mCxt;
    private WeakReference<HttpServer> mHttpServer;
    private TcpClient mClient;
    private String unique;
    private String sendFinalTag;
    private RequestMap mRequestMap;

    public HttpClient(Context cxt, Socket socket, HttpServer server) {
        mCxt = cxt.getApplicationContext();
        mHttpServer = new WeakReference<>(server);
        unique = String.valueOf(System.currentTimeMillis());
        mClient = new TcpClient(socket, unique).addTcpClientListener(this);
        mClient.start();
    }

    public void release () {
        mClient.release();
        if (mHttpServer != null && mHttpServer.get() != null) {
            mHttpServer.get().removeClient(this);
        }

        if (mRequestMap != null && mRequestMap instanceof RequestMapWebSocket) {
            if (((RequestMapWebSocket)mRequestMap).getCallback() != null) {
                ((RequestMapWebSocket) mRequestMap).getCallback().onWebSocketClose(this);
            }
            mRequestMap = null;
        }
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
    public void onSendSuccess(String unique, SendItem item, long totalLen, String dstIp, int dstPort) {
        Log.d(TAG, "onSendSuccess --> dstIp=" + dstIp + ", dstPort=" + dstPort);
        if (item.unique.equals(sendFinalTag)) release();
    }

    @Override
    public void onSendFailed(String unique, SendItem item, Exception e, String dstIp, int dstPort) {
        Log.d(TAG, "onSendFailed --> dstIp=" + dstIp + ", dstPort=" + dstPort);
        release();
    }

    @Override
    public void onReceive(String unique, String msg, String fromIp, int fromPort) {
        Log.d(TAG, "onReceive --> msg=" + msg + ", unique=" + unique);
        if (mHttpServer == null || mHttpServer.get() == null) {
            release();
            return;
        }

        if (mRequestMap != null && mRequestMap instanceof RequestMapWebSocket) {
            if (((RequestMapWebSocket)mRequestMap).getCallback() != null) {
                ((RequestMapWebSocket) mRequestMap).getCallback().onWebSocketReceive(this, msg);
            }
            return;
        }

        RequestHttpHead head = RequestHttpHead.parseHead(msg);
        if (head == null) {
            release();
            return;
        }
        Map<String, RequestMap> map = mHttpServer.get().getRequestMap();
        mRequestMap = map.get(head.path);
        if (mRequestMap == null && !TextUtils.isEmpty(head.referPath)) {
            mRequestMap = map.get(head.referPath);
        }
        if (mRequestMap == null) {
            release();
            return;
        }

        try {
            processRequest(mRequestMap, head);
        } catch (Exception e) {
            e.printStackTrace();
            release();
        }
    }

    @Override
    public void onReceiveError(String unique, Exception e, String fromIp, int fromPort) {
        Log.d(TAG, "onReceiveError --> fromIp=" + fromIp + ", fromPort=" + fromPort);
        release();
    }
    // ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ tcp client callback ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ //

    private void processRequest (RequestMap requestMap, RequestHttpHead head) throws Exception {
        if (requestMap instanceof RequestMapAssetHtml) {
            processAssetHtml((RequestMapAssetHtml) requestMap, head);
        } else if (requestMap instanceof RequestMapAssetFile) {
            processAssetFile((RequestMapAssetFile) requestMap, head);
        } else if (requestMap instanceof RequestMapSdcardFile) {
            processSdcardFile((RequestMapSdcardFile) requestMap, head);
        } else if (requestMap instanceof RequestMapCustomHtmlResFromAssets) {
            processCustomHtmlResFromAssets((RequestMapCustomHtmlResFromAssets) requestMap, head);
        } else if (requestMap instanceof RequestMapWebSocket) {
            processWebSocket((RequestMapWebSocket) requestMap, head);
        }
    }

    private void processAssetHtml (RequestMapAssetHtml r, RequestHttpHead head) throws Exception {
        sendFinalTag = String.valueOf(System.currentTimeMillis());
        String sendFilePath;
        if (!TextUtils.isEmpty(head.referPath)) {
            sendFilePath = r.getRelativeRootPath() + head.path;
        } else {
            sendFilePath = r.filePath;
        }

        String sendHeadStr = r.getResponseHead(getContentType(sendFilePath));
        InputStream fIn = mCxt.getAssets().open(sendFilePath);
        mClient.sendString("", sendHeadStr);
        mClient.sendStream(sendFinalTag, fIn);
    }

    private void processAssetFile (RequestMapAssetFile r, RequestHttpHead head) throws Exception {
        sendFinalTag = String.valueOf(System.currentTimeMillis());
        InputStream fIn =  mCxt.getAssets().open(r.filePath);
        mClient.sendString("", r.getResponseHead());
        mClient.sendStream(sendFinalTag, fIn);
    }

    private void processSdcardFile (RequestMapSdcardFile r, RequestHttpHead head) throws Exception {
        sendFinalTag = String.valueOf(System.currentTimeMillis());
        InputStream fIn =  new FileInputStream(r.filePath);
        mClient.sendString("", r.getResponseHead());
        mClient.sendStream(sendFinalTag, fIn);
    }

    private void processCustomHtmlResFromAssets (RequestMapCustomHtmlResFromAssets r, RequestHttpHead head) throws Exception {
        sendFinalTag = String.valueOf(System.currentTimeMillis());
        String sendHtmlStr;
        if (!TextUtils.isEmpty(head.referPath)) {
            sendHtmlStr = r.getHtmlSupplier() == null? null: r.getHtmlSupplier().getReferHtml(head.referPath, head);
        } else {
            sendHtmlStr = r.getHtmlSupplier() == null? null: r.getHtmlSupplier().getHtml(head);
        }

        if (sendHtmlStr == null || sendHtmlStr.length() <= 0) {
            String sendFilePath = r.getRelativeRootPath() + head.path;
            String sendHeadStr = r.getResponseHead(getContentType(sendFilePath));
            InputStream fIn = mCxt.getAssets().open(sendFilePath);
            mClient.sendString("", sendHeadStr);
            mClient.sendStream(sendFinalTag, fIn);
        } else if (sendHtmlStr.startsWith("redirect:")) {
            redirect(sendHtmlStr, head);
        } else {
            mClient.sendString("", r.getResponseHead(null));
            mClient.sendString(sendFinalTag, sendHtmlStr);
        }
    }

    private void processWebSocket (RequestMapWebSocket r, RequestHttpHead head) throws Exception {
        if (r.getCallback() != null) r.getCallback().onWebSocketConnect(this);
    }

    private void redirect (String path, RequestHttpHead head) {
        if (path.startsWith("redirect:")) path = path.substring("redirect:".length()).trim();
        Map<String, RequestMap> map = mHttpServer.get().getRequestMap();
        RequestMap requestMap = map.get(path);
        if (requestMap == null) {
            release();
            return;
        }

        try {
            head.referPath = null;
            processRequest(requestMap, head);
        } catch (Exception e) {
            e.printStackTrace();
            release();
        }
    }

    private String getContentType (String path) {
        int index = path.lastIndexOf('.');
        if (index < 0) return null;
        String tail = path.substring(index);
        switch (tail) {
            case ".png":
            case ".jpg":
            case ".jpeg":
            case ".ico":
                return "image/*";
            case ".html":
                return "text/html";
            case ".css":
                return "text/css";
            case ".js":
                return "application/x-javascript";
            case ".map":
                return "text/*";
            case ".eot":
            case ".svg":
            case ".ttf":
            case ".woff":
            case ".woff2":
                return "application/octet-stream";
        }
        return "text/*";
    }

    @Override
    public void sendMsg(String msg) {
        mClient.sendString("", msg);
    }

    @Override
    public void close() {
        release();
    }
}
