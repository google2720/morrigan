package com.morrigan.m.music;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;


import com.github.yzeaho.file.Closeables;

import java.util.ArrayList;
import java.util.List;

public class MusicLoader {
    private static final String TAG = MusicInfo.class.toString();
    private static MusicLoader musicLoader;
    private static Context context;
    private List<MusicInfo> musicList = new ArrayList<>();

    //Uri，指向external的database
    private Uri contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private Uri contentUri1 = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
    //projection：选择的列; where：过滤条件; sortOrder：排序。
    private String[] projection = {
            Media._ID,
            Media.DISPLAY_NAME,
            Media.DATA,
            Media.ALBUM,
            Media.ARTIST,
            Media.DURATION,
            Media.SIZE
    };
//	private String where =  "mime_type in ('audio/mpeg','audio/x-ms-wma') and bucket_display_name <> 'audio' and is_music > 0 " ;
//private String where =  "mime_type in ('audio/mpeg','audio/x-ms-wma')  and is_music > 0 " ;

    private static final String sortOrder = Media.ARTIST;

    public static MusicLoader instance(Context _context) {
        synchronized (MusicLoader.class) {
            if (musicLoader == null) {
                musicLoader = new MusicLoader(_context);
            }
            return musicLoader;
        }
    }

    private MusicLoader(Context _context) {
        context = _context.getApplicationContext();
    }

    public void loadContentMusic() {
        musicList = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = null;
        // 查询自定义的音乐数据
        try {
            Uri uri = Uri.parse("content://" + context.getPackageName());
            cursor = resolver.query(uri, null, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                MusicInfo musicInfo = new MusicInfo(cursor.getLong(cursor.getColumnIndex(Media._ID)));
                musicInfo.setTitle(cursor.getString(cursor.getColumnIndex(Media.DISPLAY_NAME)));
                musicInfo.setAlbum(cursor.getString(cursor.getColumnIndex(Media.ALBUM)));
                musicInfo.setDuration(cursor.getInt(cursor.getColumnIndex(Media.DURATION)));
                musicInfo.setSize(cursor.getLong(cursor.getColumnIndex(Media.SIZE)));
                musicInfo.setArtist(cursor.getString(cursor.getColumnIndex(Media.ARTIST)));
                musicInfo.setUrl(Uri.parse(cursor.getString(cursor.getColumnIndex(Media.DATA))));
                musicList.add(musicInfo);
            }
        } finally {
            Closeables.close(cursor);
        }

        // query system db
        try {
            cursor = resolver.query(contentUri, projection, null, null, sortOrder);
            while (cursor != null && cursor.moveToNext()) {
                MusicInfo musicInfo = new MusicInfo(cursor.getLong(cursor.getColumnIndex(Media._ID)));
                musicInfo.setTitle(cursor.getString(cursor.getColumnIndex(Media.DISPLAY_NAME)));
                musicInfo.setAlbum(cursor.getString(cursor.getColumnIndex(Media.ALBUM)));
                musicInfo.setDuration(cursor.getInt(cursor.getColumnIndex(Media.DURATION)));
                musicInfo.setSize(cursor.getLong(cursor.getColumnIndex(Media.SIZE)));
                musicInfo.setArtist(cursor.getString(cursor.getColumnIndex(Media.ARTIST)));
                musicInfo.setUrl(getMusicUriById(musicInfo.getId()));
                musicList.add(musicInfo);
            }
        } finally {
            Closeables.close(cursor);
        }
    }

    public List<MusicInfo> getMusicList() {
        return musicList;
    }

    private Uri getMusicUriById(long id) {
        return ContentUris.withAppendedId(contentUri, id);
    }

    public static String toTime(int time) {
        int minute = time / 1000 / 60;
        int s = time / 1000 % 60;
        String mm = null;
        String ss = null;
        if (minute < 10) mm = "0" + minute;
        else mm = minute + "";
        if (s < 10) ss = "0" + s;
        else ss = "" + s;

        return mm + ":" + ss;
    }

    public static class MusicInfo {
        private long id;
        private String title;
        private String album;
        private int duration;
        private long size;
        private String artist;
        private Uri url;
        private boolean isAssertsMusic;

        public boolean isAssertsMusic() {
            return isAssertsMusic;
        }

        public void setAssertsMusic(boolean assertsMusic) {
            isAssertsMusic = assertsMusic;
        }

        public MusicInfo(long pId) {
            id = pId;
        }

        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAlbum() {
            return album;
        }

        public void setAlbum(String album) {
            this.album = album;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public Uri getUrl() {
            return url;
        }

        public void setUrl(Uri url) {
            this.url = url;
        }
    }
}


