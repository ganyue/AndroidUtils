package com.gy.utils.tcp;

import java.io.InputStream;

public class SendItem {
    public enum Type {
        STRING,
        STREAM,
    }

    public Type type;
    public InputStream in;
    public String msg;
    public String unique;

    static SendItem getStreamItem (String unique, InputStream in) {
        SendItem item = new SendItem();
        item.type = Type.STREAM;
        item.in = in;
        item.unique = unique;
        return item;
    }

    static SendItem getStrItem (String unique, String msg) {
        SendItem item = new SendItem();
        item.type = Type.STRING;
        item.msg = msg;
        item.unique = unique;
        return item;
    }

    static SendItem getStrAndStream (String unique, String msg, InputStream in) {
        SendItem item = new SendItem();
        item.type = Type.STRING;
        item.msg = msg;
        item.in = in;
        item.unique = unique;
        return item;
    }

}
