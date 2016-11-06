package com.github.yzeaho.log;

/**
 * 日志记录类
 * Created by y on 2016/8/26.
 */
public class Lg {

    private static LgInterface sImpl = new AndroidLgImpl();

    public static void setLg(LgInterface impl) {
        sImpl = impl;
    }

    public static void d(String tag, String msg) {
        sImpl.d(tag, msg);
    }

    public static void i(String tag, String msg) {
        sImpl.i(tag, msg);
    }

    public static void w(String tag, String msg, Throwable e) {
        sImpl.w(tag, msg, e);
    }

    public static void w(String tag, String msg) {
        sImpl.w(tag, msg);
    }

    public static void e(String tag, String msg, Throwable e) {
        sImpl.e(tag, msg, e);
    }
}
