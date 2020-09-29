package com.android.ganyue.bean;

import com.gy.utils.database.annotation.DBTable;

/**
 * Created by sam_gan on 2016/6/22.
 *
 */

@DBTable(name = "Dao1")
public class Dao1 {
    public int integ;
    public String str;
    public float flt;

    public Dao1 () {
        integ = 1;
        flt = 2;
        str = "haha";
    }
}
