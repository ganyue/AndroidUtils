package com.gy.utils.tcp.httpserver;

import android.text.TextUtils;

public class RequestMapCustomHtmlResFromAssets extends RequestMap {
    private HtmlSupplier mHtmlSupplier;
    private String filePath;

    public RequestMapCustomHtmlResFromAssets(String path, String assetsResPath, HtmlSupplier htmlSupplier) {
        super(path);
        filePath = assetsResPath;
        mHtmlSupplier = htmlSupplier;
    }

    public String getRelativeRootPath () {
        return filePath;
    }

    public HtmlSupplier getHtmlSupplier () {
        return mHtmlSupplier;
    }

    public String getResponseHead(String contentType) {
        return "HTTP/1.1 " + 200 + "\r\n" +
                "Content-Type: " + (TextUtils.isEmpty(contentType)? "text/html" : contentType) + "\r\n" +
                "\r\n";
    }

    public interface HtmlSupplier {
        String getHtml ();
        String getReferHtml ();
    }
}
