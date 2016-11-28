package com.github.yzeaho.capture;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * android6.0的拍照
 * Created by y on 2016/11/28.
 */
public class CaptureMarshmallowHelper extends CaptureDefaultHelper {

    @Override
    public void start(Activity activity, int requestCode, File file) {
        Uri outputUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", file);
        startCaptureActivity(activity, requestCode, outputUri);
    }

    @Override
    public void startPhotoZoom(Activity activity, int requestCode, File captureOutFile, int size) {
        Uri outputUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", captureOutFile);
        startPhotoZoom(activity, requestCode, outputUri, size);
    }

    @Override
    public File createFile(Activity activity, String dirName) {
        return createFile(new File(activity.getFilesDir(), dirName));
    }
}
