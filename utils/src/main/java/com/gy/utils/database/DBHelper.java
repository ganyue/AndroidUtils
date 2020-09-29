package com.gy.utils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.gy.utils.database.annotation.DBColume;
import com.gy.utils.database.annotation.DBTable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yue.gan on 2016/4/2.
 *
 * 创建简单表，满足应用正常使用就好
 * 结合注解可以指定表名、主键
 * e.g. @DBTable(name="xxx") 成员变量用 @DBColumn(name="xxx", primaryKey=true)
 */
public class DBHelper extends SQLiteOpenHelper {

    private String[] createSqls;

    public DBHelper (Context context, String dbName,
                     int version, Class[] beans) {
        super(context, dbName, null, version);
        if (beans != null) {
            createSqls = new String[beans.length];
            for (int i = 0; i < beans.length; i++) {
                createSqls[i] = getCreateSql(beans[i]);
            }
        }
    }

    public SQLiteDatabase getWritableDB () {
        return getWritableDatabase();
    }
    public SQLiteDatabase getReadableDB () {
        return getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (createSqls == null || createSqls.length <= 0) return;
        for (String sql : createSqls) {
            if (TextUtils.isEmpty(sql)) continue;
            db.execSQL(sql);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO need to be override by child class to update database of certain version
    }

    public <T> List<T> query (Class<T> classOfT) {
        return query(classOfT ,"select * from " + getTableName(classOfT), null);
    }

    public <T> List<T> query (Class<T> classOfT, int num, int offset) {
        return query(classOfT ,"select * from " + getTableName(classOfT), null, num, offset);
    }

        /*** 查询* <p> e.g: dbHelper.query(cls,"select * from " + xxx + " where xxx=?", new String[]{xxx});*/
    public <T> List<T> query (Class<T> classOfT, String sql, String[] selectionArgs) {
        try {
            SQLiteDatabase db = getReadableDB();
            Cursor cursor = db.rawQuery(sql, new String[]{});

            if (cursor == null) {
                db.close();
                return new ArrayList<>();
            }

            cursor.moveToFirst();
            List<T> result = cursorToList(classOfT, cursor);

            cursor.close();
            db.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /*** 分页查询* <p> e.g: dbHelper.query(cls,"select * from " + xxx + " where xxx=xxx", null, 10, 10);*/
    public <T> List<T> query (Class<T> classOfT, String sql, String[] selectionArgs, int num, int offset) {
        SQLiteDatabase db = getReadableDB();
        sql += " limit " + num + " OFFSET " + offset;
        Cursor cursor = db.rawQuery(sql, selectionArgs);

        if (cursor == null) {
            db.close();
            return new ArrayList<>();
        }

        cursor.moveToFirst();
        List<T> result = cursorToList(classOfT, cursor);

        cursor.close();
        db.close();
        return result;
    }

    public int update (Object obj) {
        String whereClause = getTableDefaultWhereClause(obj);
        if (TextUtils.isEmpty(whereClause)) return 0;
        return update(obj, whereClause, null);
    }

    /*** 更新到表名是Object相应类名的表，该表名获取方法是{@link #getTableName(Class)}* <p> e.g: dbHelper.update(obj, "xxx=xxx", null);*/
    public int update (Object obj, String sql, String[] selectionArgs) {
        return update(getTableName(obj.getClass()), obj, sql, selectionArgs);
    }

    /*** 更新到指定名字的表* <p> e.g: dbHelper.update(xxx, obj, "xxx=xxx", null);*/
    public int update (String tableName, Object obj, String sql, String[] selectionArgs) {
        Field[] fields = obj.getClass().getDeclaredFields();
        ContentValues contentValues = new ContentValues();
        for (Field field: fields) {
            String colName = getColumnName(field);
            if (TextUtils.isEmpty(colName)) continue;
            try {
                field.setAccessible(true);

                switch (getColumnType(field)) {
                    case INTEGER:
                        contentValues.put(field.getName(), field.getInt(obj));
                        break;
                    case LONG:
                        contentValues.put(field.getName(), field.getLong(obj));
                        break;
                    case BOOLEAN:
                        contentValues.put(field.getName(), field.getBoolean(obj)? 1: 0);
                        break;
                    case REAL:
                        contentValues.put(field.getName(), field.getFloat(obj));
                        break;
                    case TEXT:
                        contentValues.put(field.getName(), ""+field.get(obj));
                        break;
                    case BLOB:
                        // TODO 图片等二进制数据
                        break;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return -1;
            }
        }

        SQLiteDatabase db = getWritableDB();
        int ret = db.update(tableName, contentValues, sql, selectionArgs);
        db.close();
        return ret;
    }

    /*** 删除的表名见方法：{@link #getTableName(Class)}* <p> e.g: dbHelper.delete(cls, "xxx=xxx", null);*/
    public <T> int delete (Class<T> classOfT, String sql, String[] selectionArgs) {
        return delete(getTableName(classOfT), sql, selectionArgs);
    }

    /*** 删除* <p> e.g: dbHelper.delete(xxx, "xxx=xxx", null);*/
    public int delete (String tableName, String sql, String[] selectionArgs) {
        SQLiteDatabase db = getWritableDB();
        int ret = db.delete(tableName, sql, selectionArgs);
        db.close();
        return ret;
    }

    public <T> int delete (T objOfT) {
        String whereClause = getTableDefaultWhereClause(objOfT);
        if (TextUtils.isEmpty(whereClause)) return 0;
        return delete(objOfT.getClass(), getTableDefaultWhereClause(objOfT), null);
    }

    private String getTableDefaultWhereClause (Object obj) {
        StringBuilder sqlPrimaryKey = new StringBuilder();
        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field: fields) {
            String colName = getColumnName(field);
            if (TextUtils.isEmpty(colName)) continue;
            if (!isPrimaryKey(field)) continue;
            try {
                field.setAccessible(true);
                if (sqlPrimaryKey.length() <= 0) {
                    sqlPrimaryKey.append(colName).append("=").append(field.get(obj));
                } else {
                    sqlPrimaryKey.append(" AND ").append(colName).append("=").append(field.get(obj));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return sqlPrimaryKey.toString();
    }

    /*** 插入* <p>如果插入不成功，直接替换掉</p>*/
    public long insertOrReplace(Object obj) {
        String tableName = getTableName(obj.getClass());
        Field[] fields = obj.getClass().getDeclaredFields();
        ContentValues contentValues = new ContentValues();
        for (Field field: fields) {
            String colName = getColumnName(field);
            if (TextUtils.isEmpty(colName)) continue;
            try {
                field.setAccessible(true);
                switch (getColumnType(field)) {
                    case INTEGER:
                        contentValues.put(field.getName(), field.getInt(obj));
                        break;
                    case LONG:
                        contentValues.put(field.getName(), field.getLong(obj));
                        break;
                    case BOOLEAN:
                        contentValues.put(field.getName(), field.getBoolean(obj)? 1: 0);
                        break;
                    case REAL:
                        contentValues.put(field.getName(), field.getFloat(obj));
                        break;
                    case TEXT:
                        contentValues.put(field.getName(), ""+field.get(obj));
                        break;
                    case BLOB:
                        // TODO 图片等二进制数据
                        break;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return -1;
            }
        }

        SQLiteDatabase db = getWritableDB();
        long ret = db.insertWithOnConflict(tableName, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return ret;
    }

    /** e.g: isExist(tableName, "xx=?", new String[]{xx}) */
    public boolean isExist (String tableName, String whereClaus, String[] args) {
        return getColumnCount(tableName, whereClaus, args) > 0;
    }

    public <T> boolean isExist (Class<T> classOfT, String whereClaus, String[] args) {
        return isExist(getTableName(classOfT), whereClaus, args);
    }

    /*** 查询数据总数*/
    public <T> int getColumnCount (Class<T> classOfT) {
        return getColumnCount(getTableName(classOfT));
    }

    /** e.g: isExist(TTT, "xx=?", new String[]{xx}) */
    public <T> int getColumnCount (Class<T> classOfT, String whereClaus, String[] args) {
        return getColumnCount(getTableName(classOfT), whereClaus, args);
    }

    public int getColumnCount(String tableName, String whereClaus, String[] args) {
        SQLiteDatabase db = getReadableDB();
        Cursor cursor = db.rawQuery("select count(*) from " + tableName + " where " + whereClaus, args);
        if (cursor == null || cursor.getCount() <= 0) {
            if (cursor != null) {
                cursor.close();
            }
            return 0;
        }

        cursor.moveToFirst();
        int count = cursor.getInt(0);

        cursor.close();
        db.close();
        return count;
    }

    public int getColumnCount (String tableName) {
        SQLiteDatabase db = getReadableDB();
        Cursor cursor = db.rawQuery("select count(*) from " + tableName, null);
        if (cursor == null || cursor.getCount() <= 0) {
            if (cursor != null) {
                cursor.close();
            }
            return 0;
        }

        cursor.moveToFirst();
        int count = cursor.getInt(0);

        cursor.close();
        db.close();
        return count;
    }

    /*** 根据查询得到的Cursor生成对应的实例列表*/
    public <T> List<T> cursorToList (Class<T> classOfT, Cursor cursor) {
        List<T> result = new ArrayList<>();
        if (cursor == null || cursor.getCount() <= 0) {
            return result;
        }

        Field[] fields = classOfT.getDeclaredFields();
        List<Field> columnFields = new ArrayList<>(fields.length);
        List<Integer> columnIndexes = new ArrayList<>(fields.length);
        for (Field field : fields) {
            String colName = getColumnName(field);
            if (TextUtils.isEmpty(colName)) continue;
            field.setAccessible(true);
            columnFields.add(field);
            columnIndexes.add(cursor.getColumnIndex(colName));
        }

        int count = cursor.getCount();
        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            try {
                T obj = classOfT.newInstance();
                for (int j = 0; j < columnIndexes.size(); j++) {
                    if (columnIndexes.get(j) <= -1) {
                        continue;
                    }
                    Field field = columnFields.get(j);
                    switch (getColumnType(field)) {
                        case INTEGER:
                            field.set(obj, cursor.getInt(columnIndexes.get(j)));
                            break;
                        case LONG:
                            field.set(obj, cursor.getLong(columnIndexes.get(j)));
                            break;
                        case BOOLEAN:
                            field.set(obj, cursor.getInt(columnIndexes.get(j)) > 0);
                            break;
                        case REAL:
                            field.set(obj, cursor.getFloat(columnIndexes.get(j)));
                            break;
                        case TEXT:
                            field.set(obj, cursor.getString(columnIndexes.get(j)));
                            break;
                        case BLOB:
                            // TODO 图片等二进制数据
                            break;
                    }
                }
                result.add(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /*** 生成创建表的sql语句*/
    public <T> String getCreateSql (Class<T> classOfT) {
        String tableName = getTableName(classOfT);
        StringBuilder createSqlStr = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        List<String> primaryKeys = new ArrayList<>();
        createSqlStr.append(tableName);

        Field[] fields = classOfT.getDeclaredFields();

        if (fields.length <= 0) {
            throw new IllegalArgumentException("there no filed in class" + classOfT.getSimpleName());
        }

        createSqlStr.append(" (");
        for (Field field : fields) {
            String colName = getColumnName(field);
            if (TextUtils.isEmpty(colName)) continue;
            if (isPrimaryKey(field)) primaryKeys.add(colName);
            createSqlStr.append(colName).append(" ").append(getColumnType(field).name).append(",");
        }

        if (primaryKeys.size() > 0) {
            createSqlStr.append(" PRIMARY KEY (").append(primaryKeys.get(0));
            for (int i = 1; i < primaryKeys.size(); i++) {
                createSqlStr.append(",").append(primaryKeys.get(i));
            }
            createSqlStr.append(")");
        }

        if (createSqlStr.charAt(createSqlStr.length() - 1) == ',') {
            createSqlStr = new StringBuilder(createSqlStr.substring(0, createSqlStr.length() - 1));
        }

        createSqlStr.append(")");
        return createSqlStr.toString();
    }

    /*** 获取数据表名字*/
    public static <T> String getTableName (Class<T> classOfT) {
        String name = "";
        if (classOfT.isAnnotationPresent(DBTable.class)) {
            DBTable dbTable = classOfT.getAnnotation(DBTable.class);
            name = dbTable == null? null: dbTable.name().toLowerCase();
        }
        if (TextUtils.isEmpty(name)) {
            name = classOfT.getSimpleName().toLowerCase();
        }
        return name;
    }

    private String getColumnName (Field field) {
        if (field.isAnnotationPresent(DBColume.class)) {
            DBColume col = field.getAnnotation(DBColume.class);
            if (col == null) return null;
            String colName = col.name();
            if (TextUtils.isEmpty(colName)) colName = field.getName();
            return colName;
        }
        return null;
    }

    private boolean isPrimaryKey (Field field) {
        if (field.isAnnotationPresent(DBColume.class)) {
            DBColume col = field.getAnnotation(DBColume.class);
            return col != null && col.primaryKey();
        }
        return false;
    }

    /*** 目前只支持三种类型数据，int, float, text. 后期如需其他类型数据，再添加吧*/
    private ColumnType getColumnType (Field field) {
        String fieldTypeName = field.getType().getSimpleName().toLowerCase();
        switch (fieldTypeName) {
            case "int":
            case "integer":
                return ColumnType.INTEGER;
            case "boolean":
                return ColumnType.BOOLEAN;
            case "float":
            case "double":
                return ColumnType.REAL;
            case "long":
                return ColumnType.LONG;
            case "string":
                return ColumnType.TEXT;
            default:
                return ColumnType.BLOB;
        }
    }

    private enum ColumnType {
        INTEGER("INTEGER"), // short int long
        LONG("INTEGER"), // short int long
        BOOLEAN("INTEGER"), // boolean 用integer false=0, true=1
        REAL("REAL"),       // 浮点型数据
        TEXT("TEXT"),       // 字符数组 字符串
        BLOB("BLOB");       // 主要保存二进制数据，譬如图片、文件等
        final String name;
        ColumnType (String name) {
            this.name = name;
        }
    }
}
