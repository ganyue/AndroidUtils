package com.gy.widget.fragment.tabfrag;

/**
 * Created by ganyu on 2016/8/29.
 *
 */
public class Tab {
    public String tabName;
    public Class fragClazz;
    public Class[] fragParamClazzs;
    public Object[] fragParamValues;

    public Tab (String tabName, Class fragClazz, Class[] fragParamClazzs, Object[] fragParamValues) {
        this.tabName = tabName;
        this.fragClazz = fragClazz;
        this.fragParamClazzs = fragParamClazzs;
        this.fragParamValues = fragParamValues;
    }
}
