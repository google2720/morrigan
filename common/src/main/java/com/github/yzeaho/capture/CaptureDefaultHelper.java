package com.github.yzeaho.capture;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.github.yzeaho.file.FileApi;

import java.io.File;
import java.io.IOException;

/**
 * android6.0以下的拍照
 * Created by y on 2016/11/28.
 */
public class CaptureDefaultHelper implements CaptureHelper {

    @Override
    public void start(Activity activity, int requestCode, File file) {
        startCaptureActivity(activity, requestCode, Uri.fromFile(file));
    }

    @Override
    public void startPhotoZoom(Activity activity, int requestCode, Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void startPhotoZoom(Activity activity, int requestCode, File captureOutFile, int size) {
        startPhotoZoom(activity, requestCode, Uri.fromFile(captureOutFile), size);
    }

    @Override
    public File createFile(Activity activity, String dirName) {
        File dir = activity.getExternalFilesDir(dirName);
        return createFile(dir);
    }

    protected void startCaptureActivity(Activity activity, int requestCode, Uri uri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        activity.startActivityForResult(intent, requestCode);
    }

    protected File createFile(File dir) {
        try {
            FileApi.checkDir(dir);
            File file = new File(dir, "tmp.png");
            if (!file.exists()) {
                // noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            }
            return file;
        } catch (IOException e) {
            Log.w("CaptureHelper", "", e);
        }
        return null;
    }
}
