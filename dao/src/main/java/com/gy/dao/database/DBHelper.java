package com.gy.dao.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.gy.dao.database.annotation.DBTable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganyu on 2016/4/2.
 *
 * 创建简单表，满足应用正常使用就好
 * 结合注解可以指定表名、主键
 * e.g. @DBTable(primaryKey="xxx")
 */
public class DBHelper extends SQLiteOpenHelper {

    private Class[] beans;

    public DBHelper (Context context, String dbName, int version, Class[] beans) {
        super(context, dbName, null, version);
        this.beans = beans;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (beans == null || beans.length <= 0) {
            throw new IllegalArgumentException("beans can not be null");
        }

        String createSql;
        for (Class bean : beans) {
            createSql = getCreateSql(bean);
            if (TextUtils.isEmpty(createSql)) {
                continue;
            }

            db.execSQL(createSql);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (beans == null || beans.length <= 0) {
            throw new IllegalArgumentException("beans can not be null");
        }
    }

    /**
     * 查询
     */
    public List query (Class bean, String sql, String[] selectionArgs) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, selectionArgs);

        List result = cursorToList(bean, cursor);

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return result;
    }

    public int update (Object obj, String sql, String[] selectionArgs) {
        Field[] fields = obj.getClass().getDeclaredFields();
        ContentValues contentValues = new ContentValues();
        for (Field field: fields) {
            try {
                field.setAccessible(true);

                if (getColumnType(field) == ColumnType.INTEGER) {
                    contentValues.put(field.getName(), field.getInt(obj));
                } else if (getColumnType(field) == ColumnType.FLOAT) {
                    contentValues.put(field.getName(), field.getFloat(obj));
                } else if (getColumnType(field) == ColumnType.TEXT) {
                    contentValues.put(field.getName(), ""+field.get(obj));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return -1;
            }
        }

        SQLiteDatabase db = getWritableDatabase();
        String tableName = getTableName(obj.getClass());
        int result = db.update(tableName, contentValues, sql, selectionArgs);
        db.close();
        return result;
    }

    /**
     * 删除
     */
    public int delete (Class bean, String sql, String[] selectionArgs) {
        SQLiteDatabase db = getWritableDatabase();
        String tableName = getTableName(bean);
        int result = db.delete(tableName, sql, selectionArgs);
        db.close();
        return result;
    }

    /**
     * 插入
     */
    public long insert (Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        ContentValues contentValues = new ContentValues();
        for (Field field: fields) {
            try {
                field.setAccessible(true);

                if (getColumnType(field) == ColumnType.INTEGER) {
                    contentValues.put(field.getName(), field.getInt(obj));
                } else if (getColumnType(field) == ColumnType.FLOAT) {
                    contentValues.put(field.getName(), field.getFloat(obj));
                } else if (getColumnType(field) == ColumnType.TEXT) {
                    contentValues.put(field.getName(), ""+field.get(obj));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return -1;
            }
        }

        SQLiteDatabase db = getWritableDatabase();
        String tableName = getTableName(obj.getClass());
        long result = db.insert(tableName, null, contentValues);
        db.close();
        return result;
    }

    /**
     * 查询数据总数
     */
    public int getColumnCount (Class bean) {
        return getColumnCount(getTableName(bean));
    }

    public int getColumnCount (String tableName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from " + tableName, null);
        if (cursor == null || cursor.getCount() <= 0) {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
            return 0;
        }

        cursor.moveToFirst();
        int count = cursor.getInt(0);

        cursor.close();
        db.close();
        return count;
    }

    /**
     * 根据查询得到的Cursor生成对应的实例列表
     */
    public List cursorToList (Class bean, Cursor cursor) {
        if (cursor == null || cursor.getCount() <= 0) {
            return null;
        }
        List result = new ArrayList();

        Field[] fields = bean.getDeclaredFields();
        int[] columnIndexs = new int[fields.length];
        for (int i = 0; i < fields.length; i++) {
            columnIndexs[i] = cursor.getColumnIndex(fields[i].getName());
        }

        int count = cursor.getCount();
        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            try {
                Object obj = bean.newInstance();
                for (int j = 0; j < columnIndexs.length; j++) {
                    if (columnIndexs[j] <= -1) {
                        continue;
                    }

                    if (getColumnType(fields[j]) == ColumnType.INTEGER) {
                        fields[j].setAccessible(true);
                        fields[j].set(obj, cursor.getInt(columnIndexs[j]));
                    } else if (getColumnType(fields[j]) == ColumnType.FLOAT) {
                        fields[j].setAccessible(true);
                        fields[j].set(obj, cursor.getFloat(columnIndexs[j]));
                    } else if (getColumnType(fields[j]) == ColumnType.TEXT) {
                        fields[j].setAccessible(true);
                        fields[j].set(obj, cursor.getString(columnIndexs[j]));
                    }
                }
                result.add(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * 生成创建表的sql语句
     */
    public String getCreateSql (Class bean) {
        String createSqlStr = "create table if not exists ";
        String tableName = getTableName(bean);
        String[] primaryKey = getTablePrimaryKeys(bean);
        createSqlStr += tableName;

        Field[] fields = bean.getDeclaredFields();

        if (fields == null || fields.length <= 0) {
            throw new IllegalArgumentException("there no filed in class" + bean.getSimpleName());
        }

        createSqlStr += " (";
        String fieldName;
        for (Field field : fields) {
            fieldName = field.getName();
            if (getColumnType(field) == ColumnType.INTEGER) {
                createSqlStr += fieldName + " integer,";
            } else if (getColumnType(field) == ColumnType.FLOAT) {
                createSqlStr += fieldName + " float,";
            } else if (getColumnType(field) == ColumnType.TEXT) {
                createSqlStr += fieldName + " text,";
            }
        }

        if (primaryKey != null && primaryKey.length > 0) {
            createSqlStr += " primary key (" + primaryKey[0];
            if (primaryKey.length > 1) {
                for (int i = 1; i < primaryKey.length; i++) {
                    createSqlStr += "," + primaryKey[i];
                }
            }
            createSqlStr += ")";
        }

        if (createSqlStr.charAt(createSqlStr.length() - 1) == ',') {
            createSqlStr = createSqlStr.substring(0, createSqlStr.length() - 1);
        }

        createSqlStr += ")";
        return createSqlStr;
    }

    /**
     * 获取数据表名字
     */
    public String getTableName (Class bean) {
        String name = null;
        if (bean.isAnnotationPresent(DBTable.class)) {
            DBTable dbTable = (DBTable) bean.getAnnotation(DBTable.class);
            name = dbTable.tableName();
        }
        if (TextUtils.isEmpty(name)) {
            name = bean.getSimpleName();
        }

        return name.toLowerCase();
    }

    /**
     * 获取数据表主键
     */
    public String[] getTablePrimaryKeys (Class bean) {
        String[] primaryKeys = null;
        if (bean.isAnnotationPresent(DBTable.class)) {
            DBTable dbTable = (DBTable) bean.getAnnotation(DBTable.class);
            primaryKeys = dbTable.primaryKey();
        }

        return primaryKeys;
    }

    /**
     * 目前只支持三种类型数据，int, float, text. 后期如需其他类型数据，再添加吧
     */
    public int getColumnType (Field field) {
        String fieldTypeName = field.getType().getSimpleName().toLowerCase();
        if (fieldTypeName.equals("int") || fieldTypeName.equals("integer")) {
            return ColumnType.INTEGER;
        } else if (fieldTypeName.equals("float")) {
            return ColumnType.FLOAT;
        } else if (fieldTypeName.equals("string")) {
            return ColumnType.TEXT;
        }
        return ColumnType.UNKOWN;
    }

    class ColumnType {
        public static final int UNKOWN = 0;
        public static final int INTEGER = 1;
        public static final int FLOAT = 2;
        public static final int TEXT = 3;
    }
}
