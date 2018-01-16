package com.morrigan.m.ble.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.morrigan.m.ble.db.DBHelper.TABLE_DEVICE;

/**
 * 设备
 * Created by y on 2016/10/31.
 */
public class Device extends Data {

    public String userId;
    public String address;
    public String name;

    private void restore(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex("_id"));
        userId = cursor.getString(cursor.getColumnIndex("_userId"));
        address = cursor.getString(cursor.getColumnIndex("_address"));
        name = cursor.getString(cursor.getColumnIndex("_name"));
    }

    private ContentValues toValue() {
        ContentValues values = new ContentValues();
        values.put("_userId", userId);
        values.put("_address", address);
        values.put("_name", name);
        return values;
    }

    public void save(Context context) {
        save(getWritableDatabase(context));
    }

    public void save(SQLiteDatabase db) {
        if (isSaved()) {
            String whereClause = "_id=?";
            String[] whereArgs = new String[]{String.valueOf(id)};
            db.update(TABLE_DEVICE, toValue(), whereClause, whereArgs);
        } else {
            id = db.insert(TABLE_DEVICE, null, toValue());
        }
    }

    public static void save(Context context, String userId, ArrayList<Device> deviceList) {
        SQLiteDatabase db = getWritableDatabase(context);
        try {
            db.beginTransaction();
            String whereClause = "_userId=?";
            String[] whereArgs = new String[]{userId};
            db.delete(TABLE_DEVICE, whereClause, whereArgs);
            for (Device device : deviceList) {
                db.insert(TABLE_DEVICE, null, device.toValue());
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public static List<Device> query(Context context, String userId) {
        ArrayList<Device> deviceList = new ArrayList<>();
        Cursor cursor = null;
        try {
            String selection = "_userId=?";
            String[] selectionArgs = new String[]{userId};
            cursor = getReadableDatabase(context).query(TABLE_DEVICE, null, selection, selectionArgs, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                Device device = new Device();
                device.restore(cursor);
                deviceList.add(device);
            }
        } finally {
            close(cursor);
        }
        return deviceList;
    }

    public static void remove(Context context, String userId, String address) {
        String whereClause = "_userId=? AND _address=?";
        String[] whereArgs = new String[]{userId, address};
        getWritableDatabase(context).delete(TABLE_DEVICE, whereClause, whereArgs);
    }

    public static void update(Context context, String userId, String address, String name) {
        ContentValues values = new ContentValues();
        values.put("_name", name);
        String whereClause = "_userId=? AND _address=?";
        String[] whereArgs = new String[]{userId, address};
        getWritableDatabase(context).update(TABLE_DEVICE, values, whereClause, whereArgs);
    }

    public static Device restoreByAddress(Context context, String userId, String address) {
        Cursor cursor = null;
        try {
            String selection = "_userId=? AND _address=?";
            String[] selectionArgs = new String[]{userId, address};
            cursor = getReadableDatabase(context).query(TABLE_DEVICE, null, selection, selectionArgs, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                Device device = new Device();
                device.restore(cursor);
                return device;
            }
        } finally {
            close(cursor);
        }
        return null;
    }

    public static void add(Context context, String userId, String address, String deviceName) {
        Device device = new Device();
        device.name = deviceName;
        device.address = address;
        device.userId = userId;
        device.save(context);
    }
}
