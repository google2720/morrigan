package com.github.yzeaho.common;

import android.database.sqlite.SQLiteDatabase;

/**
 * SQLite数据库的帮助类
 */
public class SqlHelper {

    /**
     * 创建索引
     */
    public static void createIndex(SQLiteDatabase db, String tableName, String... colNames) {
        StringBuilder cols = new StringBuilder();
        StringBuilder indexNames = new StringBuilder(tableName);
        int index = 0;
        for (String col : colNames) {
            if (index != 0) {
                cols.append(",");
            }
            index++;
            cols.append(col);
            indexNames.append("_").append(col);
        }
        db.execSQL("CREATE INDEX " + indexNames.toString() + " ON " + tableName + " (" + cols.toString() + ");");
    }

    /**
     * 创建UNIQUE约束
     */
    public static void createUnique(SQLiteDatabase db, String tableName, String... colNames) {
        StringBuilder cols = new StringBuilder();
        StringBuilder uniqueNames = new StringBuilder(tableName);
        int index = 0;
        for (String col : colNames) {
            if (index != 0) {
                cols.append(",");
            }
            index++;
            cols.append(col);
            uniqueNames.append("_").append(col);
        }
        db.execSQL("CREATE UNIQUE INDEX " + uniqueNames.toString() + " ON " + tableName + "(" + cols.toString() + ");");
    }

    /**
     * 添加列
     */
    public static void addColumn(SQLiteDatabase db, String tableName, String column, String columnType) {
        db.execSQL("ALTER TABLE " + tableName + " ADD COLUMN " + column + " " + columnType);
    }
}