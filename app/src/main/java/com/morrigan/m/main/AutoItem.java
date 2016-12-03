package com.morrigan.m.main;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.morrigan.m.R;

import java.io.Serializable;

/**
 * 自动模式的javabean
 * Created by y on 2016/10/19.
 */
public class AutoItem implements Serializable {

    public static final int TYPE_SOFT = 0;
    public static final int TYPE_WAVE = 1;
    public static final int TYPE_DYNAMIC = 2;
    public static final int TYPE_GENTLY = 3;
    public static final int TYPE_INTENSE = 4;

    public int type;
    public int resId;

    public AutoItem(int type, int resId) {
        this.type = type;
        this.resId = resId;
    }

    public static byte getMassageMode(int type) {
        switch (type) {
            case AutoItem.TYPE_WAVE:
                return 0x02;
            case AutoItem.TYPE_DYNAMIC:
                return 0x05;
            case AutoItem.TYPE_GENTLY:
                return 0x03;
            case AutoItem.TYPE_INTENSE:
                return 0x04;
            case AutoItem.TYPE_SOFT:
            default:
                return 0x01;
        }
    }

    public static int getModeDrawableResId(int type) {
        switch (type) {
            case AutoItem.TYPE_WAVE:
                return R.drawable.massage_wave_s;
            case AutoItem.TYPE_DYNAMIC:
                return R.drawable.massage_dynamic_s;
            case AutoItem.TYPE_GENTLY:
                return R.drawable.massage_gently_s;
            case AutoItem.TYPE_INTENSE:
                return R.drawable.massage_intense_s;
            case AutoItem.TYPE_SOFT:
            default:
                return R.drawable.massage_soft_s;
        }
    }

    public Drawable getDrawable(Context context) {
        return ContextCompat.getDrawable(context, getModeDrawableResId(type));
    }
}
