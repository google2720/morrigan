package com.morrigan.m;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 自定义音乐
 * Created by y on 2016/12/10.
 */
public class MusicProvider extends ContentProvider {

    private static final String[] COLUMN_NAME = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATA
    };

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public AssetFileDescriptor openAssetFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException {
        String path = uri.getLastPathSegment();
        Context context = getContext();
        if (context != null) {
            AssetManager am = context.getAssets();
            try {
                return am.openFd("music/" + path + ".mp3");
            } catch (IOException e) {
                // ignore
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Context context = getContext();
        if (context == null) {
            return null;
        }

        MatrixCursor cursor = new MatrixCursor(COLUMN_NAME);
        MatrixCursor.RowBuilder builder1 = cursor.newRow();
        builder1.add(1);
        builder1.add("喜欢你");
        builder1.add("喜欢你");
        builder1.add("邓紫棋");
        builder1.add(4 * 60 * 1000 + 19000);
        builder1.add(4162718);
        builder1.add("content://" + context.getPackageName() + "/1");

        MatrixCursor.RowBuilder builder2 = cursor.newRow();
        builder2.add(2);
        builder2.add("卡农");
        builder2.add("卡农");
        builder2.add("纯钢琴曲");
        builder2.add(3 * 60 * 1000 + 17000);
        builder2.add(3152551);
        builder2.add("content://" + context.getPackageName() + "/2");
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return "audio/mpeg";
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
