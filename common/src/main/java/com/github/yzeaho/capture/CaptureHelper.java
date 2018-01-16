package com.github.yzeaho.capture;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;

import java.io.File;

/**
 * 拍照
 * Created by y on 2016/11/28.
 */
public interface CaptureHelper {

    void start(Activity activity, int requestCode, File file);

    void startPhotoZoom(Activity activity, int requestCode, File captureOutFile, int size);

    void startPhotoZoom(Activity activity, int requestCode, Uri uri, int size);

    File createFile(Activity activity, String dirName);

    class Factory {

        public static CaptureHelper create() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return new CaptureMarshmallowHelper();
            } else {
                return new CaptureDefaultHelper();
            }
        }
    }
}
