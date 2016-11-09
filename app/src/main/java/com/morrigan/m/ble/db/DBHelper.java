package com.morrigan.m.ble.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite数据库的帮助类 该类属于扩展类,主要数据库创建和版本升级
 */
public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper instance;

    /**
     * 数据库名称
     */
    public static String DATABASE_NAME = "db004.db";

    /**
     * 数据库版本号
     */
    public static final int DB_VERSION = 1;

    public static final String TABLE_MASSAGE = "massage";
    public static final String TABLE_DEVICE = "device";

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    public static synchronized DBHelper getInstance(Context _context) {
        if (instance == null) {
            instance = new DBHelper(_context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        onUpgrade(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, final int newV) {
        for (int version = oldV + 1; version <= newV; version++) {
            upgradeTo(db, version);
        }
    }

    public void clear() {
        getWritableDatabase().execSQL("DELETE FROM " + TABLE_MASSAGE);
    }

    private void upgradeTo(SQLiteDatabase db, int version) {
        switch (version) {
            case 1:
                createMassageTable(db);
                createDeviceTable(db);
                break;
            default:
                throw new IllegalStateException("Don't know how to upgrade to " + version);
        }
    }

    private void createMassageTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MASSAGE);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_MASSAGE
                + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, _userId TEXT, _address TEXT, _startTime INTEGER, "
                + "_endTime INTEGER, _date TEXT, _hour INTEGER, _duration INTEGER)");
    }

    private void createDeviceTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICE);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_DEVICE
                + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, _userId TEXT, _address TEXT, _name TEXT)");
        createUnique(db, TABLE_DEVICE, "_userId", "_address");
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
}