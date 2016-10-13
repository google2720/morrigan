package com.morrigan.m.ble.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite数据库的帮助类 该类属于扩展类,主要数据库创建和版本升级
 */
public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper instance;

    /** 数据库名称 */
    public static String DATABASE_NAME = "db.db";

    /** 数据库版本号 */
    public static final int DB_VERSION = 1;

    public static final String TABLE_DEVICE_INFO = "device_info";
    public static final String TABLE_HISTORY = "history";
    public static final String TABLE_CURRENT_DATA = "current_data";

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    public static synchronized DBHelper getInstance(Context _context) {
        if (instance == null) {
            instance = new DBHelper(_context.getApplicationContext());
        }
        return instance;
    }

    public static synchronized void delete(Context context) {
        instance = null;
        context.deleteDatabase(DATABASE_NAME);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        onUpgrade(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, int oldV, final int newV) {
        for (int version = oldV + 1; version <= newV; version++) {
            upgradeTo(db, version);
        }
    }

    private void upgradeTo(SQLiteDatabase db, int version) {
        switch (version) {
            case 1:
                createDeviceInfoTable(db);
                createHistory(db);
                createCurrentData(db);
                break;
            default:
                throw new IllegalStateException("Don't know how to upgrade to " + version);
        }
    }

    /**
     * 创建索引
     */
    private void createIndex(SQLiteDatabase db, String tableName, String... colNames) {
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
        db.execSQL("create index " + indexNames.toString() + " on " + tableName + " (" + cols.toString() + ");");
    }

    /**
     * 创建UNIQUE约束
     */
    private void createUnique(SQLiteDatabase db, String tableName, String... colNames) {
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
        db.execSQL("create unique index " + uniqueNames.toString() + " on " + tableName + "(" + cols.toString() + ");");
    }

    /**
     * 创建设备信息表
     */
    private void createDeviceInfoTable(SQLiteDatabase db) {
        db.execSQL("drop table if exists " + TABLE_DEVICE_INFO);
        db.execSQL("create table if not exists " + TABLE_DEVICE_INFO
                + "(_address text, _main_version text, _minor_version text, primary key (_address))");
    }

    /**
     * 创建历史记录表
     */
    private void createHistory(SQLiteDatabase db) {
        db.execSQL("drop table if exists " + TABLE_HISTORY);
        db.execSQL("create table if not exists " + TABLE_HISTORY
                + "(_address text, _step integer, _cal integer, _sleep integer, _battery integer, _time integer, primary key (_address, _time))");
    }

    /**
     * 创建当前数据表
     */
    private void createCurrentData(SQLiteDatabase db) {
        db.execSQL("drop table if exists " + TABLE_CURRENT_DATA);
        db.execSQL("create table if not exists " + TABLE_CURRENT_DATA
                + "(_address text,_date text, _step integer, _distance integer, _cal integer, _gol integer, _battery integer, primary key (_address))");
    }

}