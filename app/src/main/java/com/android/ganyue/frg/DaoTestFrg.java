package com.android.ganyue.frg;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.ganyue.bean.Dao1;
import com.android.ganyue.R;
import com.android.ganyue.controller.FuncCtrl;
import com.gy.appbase.controller.BaseFragmentActivityController;
import com.gy.appbase.fragment.BaseFragment;
import com.gy.utils.database.DBHelper;
import com.gy.utils.log.LogUtils;
import com.gy.utils.preference.SharedPreferenceUtils;

/**
 * Created by ganyu on 2016/4/30.
 * Nsd test
 */
public class DaoTestFrg extends BaseFragment {

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dao_test, null);
    }

    DBHelper dbHelper;
    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        Class[] beans = {Dao1.class};
//        dbHelper = new DBHelper(getActivity(), "test", 1, beans);
//        dbHelper.insertOrReplace(new Dao1());
//        Dao1 dao1 = new Dao1();
//        dao1.integ = 4;
//        dao1.flt = 4.1f;
//        dao1.str = "haha1";
//        dbHelper.insertOrReplace(dao1);
//        int count = dbHelper.getColumnCount(beans[0]);
//        List list = dbHelper.query(beans[0], "select * from "+dbHelper.getTableName(beans[0]), null);
//        dbHelper.delete(beans[0], "str=?", new String[]{"haha1"});
//        dao1.integ = 5;dao1.flt = 5.1f;dao1.str = "haha2";
//        dbHelper.update(dao1, "str=?", new String[]{"haha"});
//        int count1 = dbHelper.getColumnCount(beans[0]);
//        List list1 = dbHelper.query(beans[0], "select * from "+dbHelper.getTableName(beans[0]), null);
//        Log.d("yue.gan", "empty");

//        Dao1 dao1 = new Dao1();
//        dao1.flt = 1;
//        dao1.integ = 2;
//        dao1.str = "haha";
//        SharedPreferenceUtils.getInstance(mActivity).save(dao1.getClass(), dao1);
//        Dao1 dao12 = (Dao1) SharedPreferenceUtils.getInstance(mActivity).get(Dao1.class);
//        Log.d("yue.gan", "empty");

        SharedPreferenceUtils sharedPreferenceUtils = SharedPreferenceUtils.getInstance(mActivity);
        Dao1 dao1 = new Dao1();dao1.flt = 1f; dao1.integ = 3; dao1.str = "haha";
        sharedPreferenceUtils.save("1", dao1);
        dao1 = new Dao1();dao1.flt = 2f; dao1.integ = 2; dao1.str = "hehe";
        sharedPreferenceUtils.save("2", dao1);
        Dao1 d = (Dao1) sharedPreferenceUtils.get("1", Dao1.class);
        Dao1 d1 = (Dao1) sharedPreferenceUtils.get("2", Dao1.class);
        LogUtils.d("yue.gan", "done");

    }

    @Override
    protected BaseFragmentActivityController instanceController() {
        return new FuncCtrl(mActivity);
    }
}
