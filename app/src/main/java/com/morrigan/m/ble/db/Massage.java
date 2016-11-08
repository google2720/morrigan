package com.morrigan.m.ble.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.morrigan.m.main.UploadHistoryDataService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.morrigan.m.ble.db.DBHelper.TABLE_MASSAGE;

/**
 * 按摩
 * Created by y on 2016/10/31.
 */
public class Massage {

    public long id;
    public String address;
    public long startTime;
    public long endTime;
    public String date;
    public String hour;
    public long duration;
    public String userId;

    private void restore(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex("_id"));
        userId = cursor.getString(cursor.getColumnIndex("_userId"));
        address = cursor.getString(cursor.getColumnIndex("_address"));
        startTime = cursor.getLong(cursor.getColumnIndex("_startTime"));
        endTime = cursor.getLong(cursor.getColumnIndex("_endTime"));
        date = cursor.getString(cursor.getColumnIndex("_date"));
        hour = cursor.getString(cursor.getColumnIndex("_hour"));
        duration = cursor.getLong(cursor.getColumnIndex("_duration"));
    }

    private ContentValues toValue() {
        ContentValues values = new ContentValues();
        values.put("_userId", userId);
        values.put("_address", address);
        values.put("_startTime", startTime);
        values.put("_endTime", endTime);
        values.put("_date", date);
        values.put("_hour", hour);
        values.put("_duration", endTime - startTime);
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

    public static List<Massage> queryToday(Context context, String userId) {
        Calendar toady = Calendar.getInstance();
        toady.set(Calendar.HOUR_OF_DAY, 0);
        toady.set(Calendar.MINUTE, 0);
        toady.set(Calendar.SECOND, 0);
        toady.set(Calendar.MILLISECOND, 0);
        long todayStartTime = toady.getTimeInMillis();
        toady.set(Calendar.HOUR_OF_DAY, 23);
        toady.set(Calendar.MINUTE, 59);
        toady.set(Calendar.SECOND, 59);
        toady.set(Calendar.MILLISECOND, 999);
        long todayEndTime = toady.getTimeInMillis();
        List<Massage> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            String selection = "_userId=? AND _startTime<=? AND _endTime>=? AND _duration>=600000";
            String[] selectionArgs = new String[]{userId, String.valueOf(todayStartTime), String.valueOf(todayEndTime)};
            cursor = getReadableDatabase(context).query(TABLE_MASSAGE, null, selection, selectionArgs, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                Massage massage = new Massage();
                massage.restore(cursor);
                result.add(massage);
            }
        } finally {
            close(cursor);
        }
        return result;
    }

    public static List<UploadHistoryDataService.Data> queryUploadData(Context context, String userId, String goal) {
        List<UploadHistoryDataService.Data> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long todayStartTime = calendar.getTimeInMillis();
            String selection = "userId=? AND _startTime<?";
            String[] selectionArgs = new String[]{userId, String.valueOf(todayStartTime)};
            String[] columns = new String[]{"sum(_duration) as _total_duration", "_date"};
            cursor = getReadableDatabase(context).query(TABLE_MASSAGE, columns, selection, selectionArgs, "_date", null, null);
            while (cursor != null && cursor.moveToNext()) {
                UploadHistoryDataService.Data data = new UploadHistoryDataService.Data();
                data.userId = userId;
                data.goalLong = goal;
                data.date = cursor.getString(cursor.getColumnIndex("_date"));
                data.timeLong = cursor.getString(cursor.getColumnIndex("_total_duration"));
                result.add(data);
            }
        } finally {
            close(cursor);
        }
        return result;
    }
}
