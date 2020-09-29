package com.gy.utils.database;


import com.google.gson.Gson;

public class TableBase {

    private static Gson mGson;
    protected static Gson getGson () {
        return mGson = (mGson == null? new Gson(): mGson);
    }

    @Override
    public String toString() {
        return getGson().toJson(this);
    }
}
