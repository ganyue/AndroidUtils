package com.gy.utils.download;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.gy.utils.database.DBHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yue.gan on 2016/7/28.
 *
 */
public class DownloadManager {

    private static DownloadManager mInstance;

    private DBHelper dbHelper;
    private List<OnDownloadListener> listeners;
    private Map<DownloadBean, AsyncTask> downloadingTasks;
    private List<DownloadBean> unfinishedBeans; //未下载完成
    private List<DownloadBean> finishedBeans;   //下载完成
    private int maxDownloadNum = 1; //最多同时下载个数
    private boolean enable = true;//默认启用下载，在3g/4g下需要设置不启用，除非你是土豪

    public void enableDownload (boolean enable) {
        this.enable = enable;
        check();
    }

    public static DownloadManager getInstance (DBHelper dbHelper) {
        if (mInstance == null) {
            mInstance = new DownloadManager(dbHelper);
        }

        return mInstance;
    }

    private DownloadManager(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
        maxDownloadNum = 1;
        init();
    }

    public void setMaxDownloadNum (int maxDownloadNum) {
        this.maxDownloadNum = maxDownloadNum <= 1? 1 : maxDownloadNum;
    }

    private void init () {
        //如果下载的数据库没创建，则创建
        SQLiteDatabase db = dbHelper.getSQLiteDataBase();
        db.execSQL(dbHelper.getCreateSql(DownloadBean.class, getFinishedTableName()));      //已完成表
        db.execSQL(dbHelper.getCreateSql(DownloadBean.class, getUnfinishedTableName()));    //未完成表

        //使用Collections.synchronizedXXX封装的list或map防止异步操作导致的崩溃或者阻塞
        List qFinishedList = dbHelper.query(DownloadBean.class, "select * from " + getFinishedTableName(), null);
        List qUnfinishedList = dbHelper.query(DownloadBean.class, "select * from " + getUnfinishedTableName(), null);
        if (qFinishedList == null) {
            finishedBeans = Collections.synchronizedList(new ArrayList<DownloadBean>()) ;
        } else {
            finishedBeans = Collections.synchronizedList(qFinishedList) ;
            checkFinishedFiles();
        }
        if (qUnfinishedList == null) {
            unfinishedBeans = Collections.synchronizedList(new ArrayList<DownloadBean>()) ;
        } else {
            unfinishedBeans = Collections.synchronizedList(qUnfinishedList);
        }
        downloadingTasks = Collections.synchronizedMap(new HashMap<DownloadBean, AsyncTask>());

        check();
    }

    /** 检查已完成的列表，如果已完成的文件不存在，直接删除对应的数据 */
    public void checkFinishedFiles () {
        List<DownloadBean> fileUnExistBeans = new ArrayList<>();
        for (DownloadBean downloadBean: finishedBeans) {
            File file = new File(downloadBean.storePath, downloadBean.fileName);
            if (!file.exists() || file.isDirectory()) {
                fileUnExistBeans.add(downloadBean);
            }
        }

        for (DownloadBean downloadBean: fileUnExistBeans) {
            DownloadBean removedBean = removeBeanFromList(finishedBeans, downloadBean);
            if (removedBean == null) return;
            dbHelper.delete(getFinishedTableName(), "fileName=? and storePath=?", new String[]{removedBean.fileName, removedBean.storePath});
            finishedBeans.remove(downloadBean);
        }
    }

    private String getFinishedTableName () {
        return dbHelper.getTableName(DownloadBean.class) + "_finished";
    }

    private String getUnfinishedTableName () {
        return dbHelper.getTableName(DownloadBean.class) + "_unfinished";
    }

    public List<DownloadBean> getFinishedList() {
        return finishedBeans;
    }

    public List<DownloadBean> getUnfinishedList () {
        return unfinishedBeans;
    }

    public String getFinishedLocalPath(DownloadBean b) {
        for (DownloadBean bean: finishedBeans) {
            if (bean.fileName.equals(b.fileName) && bean.storePath.equals(b.storePath)) {
                File file = new File(bean.storePath, bean.fileName);
                if (file.exists() && !file.isDirectory()) {
                    return file.getAbsolutePath();
                }
            }
        }
        return null;
    }

    /**
     * 检查是否新建下载
     */
    private void check () {
        if (!enable) {
            if (downloadingTasks.size() <= 0) return;
            Set<DownloadBean> keys = downloadingTasks.keySet();
            for (DownloadBean key: keys) {
                AsyncTask task = downloadingTasks.remove(key);
                task.cancel(true);
            }
            return;
        }

        if (downloadingTasks.size() < maxDownloadNum) {
            int num = maxDownloadNum - downloadingTasks.size();
            for (int i = 0; i < num; i++) {
                for (DownloadBean bean: unfinishedBeans) {
                    if ((bean.state == DownloadState.WAITEING
                            || bean.state == DownloadState.DOWNLOADING)
                            && !downloadingTasks.containsKey(bean)) {
                        BreakPointDownloadTask task = new BreakPointDownloadTask(bean, onDownloadListener);
                        task.execute();
                        downloadingTasks.put(bean, task);
                    }
                }
            }
        }
    }

    /**
     * 从列表中删除一个下载任务，防止传入的是用户新建的bean
     */
    private DownloadBean removeBeanFromList (List<DownloadBean> list, DownloadBean bean) {
        DownloadBean beanInList = null;
        if (list == null) return null;
        for (DownloadBean b: list) {
            if (bean.fileName.equals(b.fileName) && bean.storePath.equals(b.storePath)) {
                beanInList = b;
                break;
            }
        }
        if (beanInList != null) list.remove(beanInList);
        return beanInList;
    }

    /**
     * 从列表中找到一个下载任务，防止传入的是用户新建的bean
     */
    private DownloadBean getBeanFromList (List<DownloadBean> list, DownloadBean bean) {
        DownloadBean beanInList = null;
        for (DownloadBean b: list) {
            if (bean.fileName.equals(b.fileName) && bean.storePath.equals(b.storePath)) {
                beanInList = b;
                break;
            }
        }
        return beanInList;
    }

    public boolean isDownloaded (DownloadBean b) {
        for (DownloadBean downloadBean: finishedBeans) {
            if (downloadBean.fileName.equals(b.fileName)
                    && downloadBean.storePath.equals(b.storePath)) return true;
        }
        return false;
    }

    public boolean isDownloading (DownloadBean b) {
        for (DownloadBean downloadBean: unfinishedBeans) {
            if (downloadBean.fileName.equals(b.fileName)
                    && downloadBean.storePath.equals(b.storePath)) return true;
        }
        return false;
    }

    public int add (List<DownloadBean> beans) {
        if (beans == null) return 0;
        int count = 0;
        for (DownloadBean bean: beans) {
            if (add(bean)) {
                count++;
            }
        }
        return count;
    }

    public void add2FinishedList (DownloadBean bean) {
        dbHelper.insertOrReplace(getFinishedTableName(), bean);
        if (!isDownloaded(bean)) {
            finishedBeans.add(bean);
        }
    }

    public boolean add (DownloadBean bean) {
        //不能直接用contains查询是否已经有该下载任务
        List qFinishedList = dbHelper.query(bean.getClass(),
                "select * from " + getFinishedTableName() + " where fileName=? and storePath=?",
                new String[]{bean.fileName, bean.storePath});
        List qUnfinishedList = dbHelper.query(bean.getClass(),
                "select * from " + getUnfinishedTableName() + " where fileName=? and storePath=?",
                new String[]{bean.fileName, bean.storePath});

        if ( (qFinishedList == null || qFinishedList.size() <= 0)
                && (qUnfinishedList == null || qUnfinishedList.size() <= 0)) {
            //如果未下载和已下载表中都没有该任务，则加入任务，更新数据库
            bean.state = DownloadState.WAITEING;//防止使用者写错状态
            bean.storedLen = 0;
            unfinishedBeans.add(bean);
            dbHelper.insertOrReplace(getUnfinishedTableName(), bean);
            check();
            return true;
        }
        return false;
    }

    public void delete (DownloadBean bean) {
        DownloadBean removedBean = null;
        removedBean = removeBeanFromList(unfinishedBeans, bean);
        if (removedBean == null) removedBean = removeBeanFromList(finishedBeans, bean);
        if (removedBean == null) return;

        if (removedBean.state == DownloadState.DOWNLOADED) {
                //如果该任务已下载完成，从已下载列表和数据库中删除
            dbHelper.delete(getFinishedTableName(), "fileName=? and storePath=?",
                    new String[]{removedBean.fileName, removedBean.storePath});
        } else {
            //如果未下载完成，从未下载完成列表和数据库中删除
            //若正在下载，需要取消下载
            AsyncTask task = downloadingTasks.remove(removedBean);
            if (task != null) {
                task.cancel(true);
            }
            dbHelper.delete(getUnfinishedTableName(), "fileName=? and storePath=?",
                    new String[]{removedBean.fileName, removedBean.storePath});
        }

        //删除文件
        File file = new File(removedBean.storePath + File.separator + removedBean.fileName);
        file.delete();
        check();
        onDownloadListener.onDownloadDelete(removedBean);
    }

    public void pauseAll () {
        for (DownloadBean bean: unfinishedBeans) {
            if ((bean.state == DownloadState.DELETEED
                    || bean.state == DownloadState.DOWNLOADED)) continue;
            if (bean.state != DownloadState.PAUSE) {
                AsyncTask task = downloadingTasks.remove(bean);
                if (task != null) {
                    task.cancel(true);
                }
                bean.state = DownloadState.PAUSE;
                dbHelper.insertOrReplace(getUnfinishedTableName(), bean);
            }
        }
    }

    public void startAll () {
        for (DownloadBean bean: unfinishedBeans) {
            if ((bean.state == DownloadState.DELETEED
                    || bean.state == DownloadState.DOWNLOADED)) continue;
            if (bean.state != DownloadState.DOWNLOADING &&
                    bean.state != DownloadState.WAITEING) {

                bean.state = DownloadState.WAITEING;
                dbHelper.insertOrReplace(getUnfinishedTableName(), bean);
            }
        }
        check();
    }

    public DownloadBean pauseOrStart (int index) {
        if (index >= unfinishedBeans.size()) return null;
        DownloadBean bean = unfinishedBeans.get(index);
        if (bean.state == DownloadState.DELETEED || bean.state == DownloadState.DOWNLOADED) {
            return null;
        } else if (bean.state == DownloadState.WAITEING) {
            bean.state = DownloadState.PAUSE;
        } else if (bean.state == DownloadState.DOWNLOADING) {
            AsyncTask task = downloadingTasks.remove(bean);
            task.cancel(true);
            bean.state = DownloadState.PAUSE;
        } else if (bean.state == DownloadState.PAUSE) {
            bean.state = DownloadState.WAITEING;
        } else {
            //如果下载失败，需要删除之前的文件，重新下载，并把状态置为等待下载
            bean.state = DownloadState.WAITEING;
            File file = new File(bean.storePath + File.separator + bean.fileName);
            file.delete();
        }

        dbHelper.insertOrReplace(getUnfinishedTableName(), bean);
        check();
        return bean;
    }

    public DownloadBean pauseOrStart (DownloadBean bean) {
        bean = getBeanFromList(unfinishedBeans, bean);
        if (bean == null) return null;
        if (bean.state == DownloadState.DELETEED || bean.state == DownloadState.DOWNLOADED) {
            return null;
        } else if (bean.state == DownloadState.WAITEING) {
            bean.state = DownloadState.PAUSE;
        } else if (bean.state == DownloadState.DOWNLOADING) {
            AsyncTask task = downloadingTasks.remove(bean);
            task.cancel(true);
            bean.state = DownloadState.PAUSE;
        } else if (bean.state == DownloadState.PAUSE) {
            bean.state = DownloadState.WAITEING;
        } else {
            bean.state = DownloadState.WAITEING;
            File file = new File(bean.storePath + File.separator + bean.fileName);
            file.delete();
        }

        dbHelper.insertOrReplace(getUnfinishedTableName(), bean);
        check();
        return bean;
    }

    //支持设置多个回调
    public void addOnDownloadListener (OnDownloadListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeOnDownloadListener (OnDownloadListener listener) {
        if (listeners != null && listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    //防止多个地方都需要设置回调，在此作多个回调的分发
    private OnDownloadListener onDownloadListener = new OnDownloadListener() {
        @Override
        public void onDownloadStart(DownloadBean bean) {
            dbHelper.insertOrReplace(getUnfinishedTableName(), bean);
            if (listeners != null) {
                for (OnDownloadListener listener: listeners) {
                    listener.onDownloadStart(bean);
                }
            }
        }

        @Override
        public void onDownloadFinished(DownloadBean bean) {
            unfinishedBeans.remove(bean);
            finishedBeans.add(bean);
            dbHelper.insertOrReplace(getFinishedTableName(), bean);
            dbHelper.delete(getUnfinishedTableName(), "fileName=? and storePath=?", new String[]{bean.fileName, bean.storePath});
            downloadingTasks.remove(bean);
            check();
            if (listeners != null) {
                for (OnDownloadListener listener: listeners) {
                    listener.onDownloadFinished(bean);
                }
            }
        }

        @Override
        public void onDownloadPause(DownloadBean bean) {
            if (listeners != null) {
                for (OnDownloadListener listener: listeners) {
                    listener.onDownloadPause(bean);
                }
            }
        }

        @Override
        public void onDownloadError(DownloadBean bean) {
            dbHelper.insertOrReplace(getUnfinishedTableName(), bean);
            downloadingTasks.remove(bean);
            File file = new File(bean.storePath + File.separator + bean.fileName);
            file.delete();
            check();
            if (listeners != null) {
                for (OnDownloadListener listener: listeners) {
                    listener.onDownloadError(bean);
                }
            }
        }

        @Override
        public void onDownloadDelete(DownloadBean bean) {
            bean.state = DownloadState.DELETEED;
            if (listeners != null) {
                for (OnDownloadListener listener: listeners) {
                    listener.onDownloadDelete(bean);
                }
            }
        }

        @Override
        public void onDownloadProgress(DownloadBean bean) {
            //不能在此使用数据库或是其他耗时操作，会拖慢下载速度
            if (listeners != null) {
                for (OnDownloadListener listener: listeners) {
                    listener.onDownloadProgress(bean);
                }
            }
        }
    };
}
