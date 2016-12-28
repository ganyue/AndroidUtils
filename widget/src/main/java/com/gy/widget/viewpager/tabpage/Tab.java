package com.gy.widget.viewpager.tabpage;

import android.os.Bundle;

/**
 * Created by ganyu on 2016/9/13.
 */
public class Tab {
    public String tabName;
    public Class fragClazz;
    public Bundle arguments;

    public Tab (String tabName, Class fragClazz, Bundle arguments) {
        this.tabName = tabName;
        this.fragClazz = fragClazz;
        this.arguments = arguments;
    }
}
