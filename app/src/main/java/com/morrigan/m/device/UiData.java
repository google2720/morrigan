package com.morrigan.m.device;

import android.support.annotation.Keep;

import java.io.Serializable;

/**
 * 设备信息
 * Created by y on 2016/10/12.
 */
public class UiData implements Serializable {
    public static final int TYPE_ADD = 0;
    public static final int TYPE_DEVICE = 1;
    public int type;
    public String name;
    public String address;
    public int num;
    public boolean showIcon;
}
