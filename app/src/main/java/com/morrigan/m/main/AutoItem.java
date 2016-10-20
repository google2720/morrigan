package com.morrigan.m.main;

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
}
