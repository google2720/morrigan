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
    public static String DATABASE_NAME = "db.db";

    /**
     * 数据库版本号
     */
    public static final int DB_VERSION = 1;

    public static final String TABLE_MASSAGE = "massage";

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

    private void upgradeTo(SQLiteDatabase db, int version) {
        switch (version) {
            case 1:
                createRecodeTable(db);
                break;
            default:
                throw new IllegalStateException("Don't know how to upgrade to " + version);
        }
    }

    /**
     * 创建当前数据表
     */
    private void createRecodeTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MASSAGE);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_MASSAGE
                + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, _address TEXT, _startTime INTEGER, _endTime INTEGER)");
    }
}