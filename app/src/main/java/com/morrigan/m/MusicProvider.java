package com.morrigan.m;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.yzeaho.log.Lg;

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
    private static final String TAG = "MusicProvider";

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public AssetFileDescriptor openAssetFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException {
        Lg.d(TAG, "openAssetFile " + uri);
        Context context = getContext();
        if (context == null) {
            Lg.d(TAG, "open file but context is null.");
            return null;
        }
        String path = uri.getLastPathSegment();
        AssetManager am = context.getAssets();
        try {
            return am.openFd("music/" + path + ".mp3");
        } catch (IOException e) {
            Lg.w(TAG, "failed to open asset file " + path, e);
        }
        return null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Context context = getContext();
        if (context == null) {
            Lg.d(TAG, "query something but context is null.");
            return null;
        }

        MatrixCursor cursor = new MatrixCursor(COLUMN_NAME);
        MatrixCursor.RowBuilder builder1 = cursor.newRow();
        builder1.add(1);
        builder1.add("梦中的婚礼");
        builder1.add("梦中的婚礼");
        builder1.add("钢琴曲");
        builder1.add(3 * 60 * 1000 +2000);
        builder1.add(2915041);
        builder1.add("content://" + context.getPackageName() + "/1");

        MatrixCursor.RowBuilder builder2 = cursor.newRow();
        builder2.add(2);
        builder2.add("一念之间");
        builder2.add("一念之间");
        builder2.add("张杰、莫文蔚");
        builder2.add(4 * 60 * 1000 + 58000);
        builder2.add(4771020);
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
