package com.morrigan.m.ble.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库对象
 * Created by y on 2016/11/10.
 */
public abstract class Data {

    public static final long NO_SAVED = 1;
    public long id = NO_SAVED;

    public boolean isSaved() {
        return id != NO_SAVED;
    }

    protected static SQLiteDatabase getReadableDatabase(Context context) {
        return DBHelper.getInstance(context).getReadableDatabase();
    }

    protected static SQLiteDatabase getWritableDatabase(Context context) {
        return DBHelper.getInstance(context).getWritableDatabase();
    }

    public static void close(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }
}
