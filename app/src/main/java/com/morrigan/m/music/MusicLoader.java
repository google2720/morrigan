package com.morrigan.m.music;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;

import com.morrigan.m.utils.FileUtils;

public class MusicLoader {
    static Context context;
    private static final String TAG = MusicInfo.class.toString();

    private static List<MusicInfo> musicList = new ArrayList<MusicInfo>();

    private static MusicLoader musicLoader;

    private static ContentResolver contentResolver;
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

    private String sortOrder = Media.DATA;

    public static MusicLoader instance(Context pContext) {
        context = pContext;
        if (musicLoader == null) {
            contentResolver = context.getContentResolver();
            musicLoader = new MusicLoader();
        }
        return musicLoader;
    }

    private void loaderAssertMusic() {
        try {
            String[] fileNasmes = context.getAssets().list("music");
            if (fileNasmes != null) {
                for (int i = 0; i < fileNasmes.length; i++) {
                    String filename = fileNasmes[i];
                    MusicInfo info = new MusicInfo();
                    info.setAssertsMusic(true);
                    String absoluteName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename;
                    File file = new File(absoluteName);
                    if (!file.exists()) {
                        FileUtils.copy(context.getAssets().open("music/" + filename), file);
                    }

                    // Tell the media scanner about the new file so that it is

                    // immediately available to the user.

                    MediaScannerConnection.scanFile(context,

                            new String[]{file.toString()}, null,

                            new MediaScannerConnection.OnScanCompletedListener() {

                                public void onScanCompleted(String path, Uri uri) {

                                    Log.i("ExternalStorage", "Scanned " + path + ":");

                                    Log.i("ExternalStorage", "-> uri=" + uri);

                                }

                            });


                }
            }
        } catch (IOException e) {

            Log.e("musicLoader", e.getMessage());
        }


    }


    private MusicLoader() {

    }

    public void init() {
        musicList = new ArrayList<>();
        loaderAssertMusic();
        loadContentMusic(contentUri);
    }

    private void loadContentMusic(Uri contentUri) {
        //利用ContentResolver的query函数来查询数据，然后将得到的结果放到MusicInfo对象中，最后放到数组中
        Cursor cursor = contentResolver.query(contentUri, projection, null, null, sortOrder);
        if (cursor == null) {
            Log.v(TAG, "Line(37	)	Music Loader cursor == null.");
        } else if (!cursor.moveToFirst()) {
            Log.v(TAG, "Line(39	)	Music Loader cursor.moveToFirst() returns false.");
        } else {
            int displayNameCol = cursor.getColumnIndex(Media.DISPLAY_NAME);
            int albumCol = cursor.getColumnIndex(Media.ALBUM);
            int idCol = cursor.getColumnIndex(Media._ID);
            int durationCol = cursor.getColumnIndex(Media.DURATION);
            int sizeCol = cursor.getColumnIndex(Media.SIZE);
            int artistCol = cursor.getColumnIndex(Media.ARTIST);
            int urlCol = cursor.getColumnIndex(Media.DATA);
            do {
                String title = cursor.getString(displayNameCol);
                String album = cursor.getString(albumCol);
                long id = cursor.getLong(idCol);
                int duration = cursor.getInt(durationCol);
                long size = cursor.getLong(sizeCol);
                String artist = cursor.getString(artistCol);
                String url = cursor.getString(urlCol);

                MusicInfo musicInfo = new MusicInfo(id, title);
                musicInfo.setAlbum(album);
                musicInfo.setDuration(duration);
                musicInfo.setSize(size);
                musicInfo.setArtist(artist);
                musicInfo.setUrl(url);
                musicList.add(musicInfo);

            } while (cursor.moveToNext());
        }
        List<MusicInfo> musicInfoList1 = new ArrayList<>();
        List<MusicInfo> musicInfoList2 = new ArrayList<>();
        List<MusicInfo> musicInfoList3 = new ArrayList<>();
        for (int i = 0; i < musicList.size(); i++) {
            MusicInfo info = musicList.get(i);
            if ("G.E.M.邓紫棋 - 喜欢你.mp3".equals(info.getTitle())) {
                musicInfoList1.add(info);
            } else if ("卡农 - 钢琴小提琴二重奏.mp3".equals(info.getTitle())) {
                musicInfoList2.add(info);
            } else {
                musicInfoList3.add(info);
            }
        }
        musicList = new ArrayList<>();
        musicList.addAll(musicInfoList1);
        musicList.addAll(musicInfoList2);
        musicList.addAll(musicInfoList3);
    }


    public List<MusicInfo> getMusicList() {
        return musicList;
    }

    public Uri getMusicUriById(long id) {
        Uri uri = ContentUris.withAppendedId(contentUri, id);
        return uri;
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

    public static class MusicInfo implements Comparator<MusicInfo> {
        private long id;
        private String title;
        private String album;
        private int duration;
        private long size;
        private String artist;
        private String url;
        public String fileName;
        private boolean isAssertsMusic;
        int sort = 1;

        public boolean isAssertsMusic() {
            return isAssertsMusic;
        }

        public void setAssertsMusic(boolean assertsMusic) {
            isAssertsMusic = assertsMusic;
        }

        public MusicInfo() {

        }

        public MusicInfo(long pId, String pTitle) {
            id = pId;
            title = pTitle;
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

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }


        @Override
        public int compare(MusicInfo musicInfo, MusicInfo t1) {
            if ("G.E.M.邓紫棋 - 喜欢你".equals(musicInfo.getTitle())) {
                return -1;
            } else if ("卡农 - 钢琴小提琴二重奏".equals(t1.getTitle())) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}


