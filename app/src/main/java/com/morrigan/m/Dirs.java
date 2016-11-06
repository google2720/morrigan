package com.morrigan.m;

import android.content.Context;

import java.io.File;

/**
 * Created by y on 2016/11/3.
 */
public class Dirs {

    public static File getCaptureDir(Context context) {
        return new File(context.getFilesDir(), "capture");
    }
}
