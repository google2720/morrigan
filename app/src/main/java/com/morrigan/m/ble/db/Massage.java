package com.morrigan.m.ble.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.morrigan.m.historyrecord.TodayRecord;
import com.morrigan.m.main.CenterData;
import com.morrigan.m.main.UploadHistoryDataService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import static com.morrigan.m.ble.db.DBHelper.TABLE_MASSAGE;

/**
 * 按摩
 * Created by y on 2016/10/31.
 */
public class Massage extends Data {

    public String userId;
    public String address;
    public long startTime;
    public long endTime;
    public String date;
    public int hour;
    public long duration;

    private void restore(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex("_id"));
        userId = cursor.getString(cursor.getColumnIndex("_userId"));
        address = cursor.getString(cursor.getColumnIndex("_address"));
        startTime = cursor.getLong(cursor.getColumnIndex("_startTime"));
        endTime = cursor.getLong(cursor.getColumnIndex("_endTime"));
        date = cursor.getString(cursor.getColumnIndex("_date"));
        hour = cursor.getInt(cursor.getColumnIndex("_hour"));
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
        values.put("_duration", duration);
        return values;
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

    public static TodayRecord queryTodayHistory(Context context, String userId) {
        TodayRecord todayRecord = new TodayRecord();
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
        Cursor cursor = null;
        try {
            String[] columns = new String[]{"sum(_duration) as _duration", "_hour"};
            String selection = "_userId=? AND _startTime>=? AND _endTime<=?";
            String[] selectionArgs = new String[]{userId, String.valueOf(todayStartTime), String.valueOf(todayEndTime)};
            cursor = getReadableDatabase(context).query(TABLE_MASSAGE, columns, selection, selectionArgs, "_hour", null, null);
            while (cursor != null && cursor.moveToNext()) {
                int h = cursor.getInt(cursor.getColumnIndex("_hour"));
                int d = cursor.getInt(cursor.getColumnIndex("_duration"));
                todayRecord.records[h] = d / 60000;
            }
        } finally {
            close(cursor);
        }
        return todayRecord;
    }

    public static List<CenterData> queryToday(Context context, String userId, boolean am) {
        Calendar toady = Calendar.getInstance();
        toady.set(Calendar.HOUR_OF_DAY, am ? 0 : 12);
        toady.set(Calendar.MINUTE, 0);
        toady.set(Calendar.SECOND, 0);
        toady.set(Calendar.MILLISECOND, 0);
        long todayStartTime = toady.getTimeInMillis();
        toady.set(Calendar.HOUR_OF_DAY, am ? 11 : 23);
        toady.set(Calendar.MINUTE, 59);
        toady.set(Calendar.SECOND, 59);
        toady.set(Calendar.MILLISECOND, 999);
        long todayEndTime = Math.min(Calendar.getInstance().getTimeInMillis(), toady.getTimeInMillis());

        Cursor cursor = null;
        HashSet<Integer> modeSet = new HashSet<>();
        int g = 600000;
        long timeOffset;
        int startMode;
        int endMode;
        Massage massage;
        try {
            String selection = "_userId=? AND _startTime>=? AND _endTime<=?";
            String[] selectionArgs = new String[]{userId, String.valueOf(todayStartTime), String.valueOf(todayEndTime)};
            cursor = getReadableDatabase(context).query(TABLE_MASSAGE, null, selection, selectionArgs, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                massage = new Massage();
                massage.restore(cursor);
                timeOffset = massage.startTime - todayStartTime;
                startMode = (int) timeOffset / g;
                if ((startMode + 1) * g - timeOffset > 60000) {
                    modeSet.add(startMode);
                }
                timeOffset = massage.endTime - todayStartTime;
                endMode = (int) timeOffset / g;
                if (timeOffset - endMode * g > 60000) {
                    modeSet.add(endMode);
                }
                for (int i = startMode; i < endMode; i++) {
                    modeSet.add(i);
                }
            }
        } finally {
            close(cursor);
        }
        Integer[] modes = modeSet.toArray(new Integer[modeSet.size()]);
        Arrays.sort(modes);
        List<CenterData> result = new ArrayList<>();
        CenterData data = null;
        for (int i = 0; i < modes.length; i++) {
            if (i != 0 && modes[i] - modes[i - 1] == 1) {
                data.endAngle = (modes[i] + 1) * 5;
            } else {
                data = new CenterData();
                data.startAngle = modes[i] * 5;
                data.endAngle = (modes[i] + 1) * 5;
                result.add(data);
            }
        }
        return result;
    }

    public static List<UploadHistoryDataService.Data> queryUploadData(Context context, String userId, String goal, long startTime) {
        List<UploadHistoryDataService.Data> result = new ArrayList<>();
        UploadHistoryDataService.Data data;
        Cursor cursor = null;
        try {
            String selection = "_userId=? AND _startTime<?";
            String[] selectionArgs = new String[]{userId, String.valueOf(startTime)};
            String[] columns = new String[]{"sum(_duration) as _duration", "_date"};
            cursor = getReadableDatabase(context).query(TABLE_MASSAGE, columns, selection, selectionArgs, "_date", null, null);
            while (cursor != null && cursor.moveToNext()) {
                data = new UploadHistoryDataService.Data();
                data.userId = userId;
                data.goalLong = goal;
                data.date = cursor.getString(cursor.getColumnIndex("_date"));
                final long duration = cursor.getLong(cursor.getColumnIndex("_duration"));
                data.timeLong = String.valueOf(duration / 60000);
                result.add(data);
            }
        } finally {
            close(cursor);
        }
        return result;
    }

    public static void deleteUploadData(Context context, String userId, long startTime) {
        String whereClause = "_userId=? AND _startTime<?";
        String[] whereArgs = new String[]{userId, String.valueOf(startTime)};
        getWritableDatabase(context).delete(TABLE_MASSAGE, whereClause, whereArgs);
    }

    public static int sum(Context context, String userId) {
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
        Cursor cursor = null;
        try {
            String[] columns = new String[]{"sum(_duration) as _duration"};
            String selection = "_userId=? AND _startTime>=? AND _endTime<=?";
            String[] selectionArgs = new String[]{userId, String.valueOf(todayStartTime), String.valueOf(todayEndTime)};
            cursor = getReadableDatabase(context).query(TABLE_MASSAGE, columns, selection, selectionArgs, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                return cursor.getInt(cursor.getColumnIndex("_duration")) / 60000;
            }
        } finally {
            close(cursor);
        }
        return 0;
    }
}
