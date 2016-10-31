package com.morrigan.m.ble.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static com.morrigan.m.ble.db.DBHelper.TABLE_MASSAGE;

/**
 * Created by y on 2016/10/31.
 */
public class Massage {

    public long id;
    public String address;
    public long startTime;
    public long endTime;

    protected void restore(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex("_id"));
        address = cursor.getString(cursor.getColumnIndex("_address"));
        startTime = cursor.getLong(cursor.getColumnIndex("_startTime"));
        endTime = cursor.getLong(cursor.getColumnIndex("_endTime"));
    }

    protected ContentValues toValue() {
        ContentValues values = new ContentValues();
        values.put("_address", address);
        values.put("_startTime", startTime);
        values.put("_endTime", endTime);
        return values;
    }

    private static SQLiteDatabase getReadableDatabase(Context context) {
        return DBHelper.getInstance(context).getReadableDatabase();
    }

    private static SQLiteDatabase getWritableDatabase(Context context) {
        return DBHelper.getInstance(context).getWritableDatabase();
    }

    public static void close(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

    public void save(Context context) {
        save(getWritableDatabase(context));
    }

    public void save(SQLiteDatabase db) {
        if (isSaved()) {
            String whereClause = "_id=?";
            String[] whereArgs = new String[]{String.valueOf(id)};
            db.update(TABLE_MASSAGE, toValue(), whereClause, whereArgs);
        } else {
            id = db.insert(TABLE_MASSAGE, null, toValue());
        }
    }

    public boolean isSaved() {
        return id != -1;
    }

    public static Massage restoreById(Context context, long id) {
        Cursor cursor = null;
        try {
            String selection = "_id=?";
            String[] selectionArgs = new String[]{String.valueOf(id)};
            cursor = getReadableDatabase(context).query(TABLE_MASSAGE, null, selection, selectionArgs, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                Massage massage = new Massage();
                massage.restore(cursor);
                return massage;
            }
        } finally {
            close(cursor);
        }
        return null;
    }
}
