package com.github.yzeaho.common;

import android.content.Context;
import android.support.annotation.MainThread;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

/**
 * toast工具类
 * Created by y on 2016/10/3.
 */
public class ToastUtils {

    private static Toast sToast;

    @MainThread
    public static void show(Context context, String text, int duration) {
        if (sToast == null) {
            sToast = Toast.makeText(context.getApplicationContext(), text, duration);
        }
        if (!TextUtils.isEmpty(text)) {
            sToast.setText(text);
            sToast.setDuration(duration);
            sToast.setGravity(Gravity.CENTER, 0, 0);
            sToast.show();
        }
    }

    @MainThread
    public static void show(Context context, int resId, int duration) {
        show(context, context.getString(resId), duration);
    }

    @MainThread
    public static void show(Context context, int resId) {
        show(context, context.getString(resId), Toast.LENGTH_SHORT);
    }

    @MainThread
    public static void show(Context context, String text) {
        show(context, text, Toast.LENGTH_SHORT);
    }
}
