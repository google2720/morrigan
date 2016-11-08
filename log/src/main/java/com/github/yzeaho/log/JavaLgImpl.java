package com.github.yzeaho.log;

/**
 * java的日志记录
 * Created by y on 2016/8/26.
 */
public class JavaLgImpl implements LgInterface {

    @Override
    public void setLevel(int level) {
    }

    @Override
    public void d(String tag, String msg) {
        System.out.println(tag + " " + msg);
    }

    @Override
    public void i(String tag, String msg) {
        System.out.println(tag + " " + msg);
    }

    @Override
    public void w(String tag, String msg, Throwable e) {
        System.out.println(tag + " " + msg);
        e.printStackTrace();
    }

    @Override
    public void w(String tag, String msg) {
        System.out.println(tag + " " + msg);
    }

    @Override
    public void e(String tag, String msg, Throwable e) {
        System.out.println(tag + " " + msg);
        e.printStackTrace();
    }
}
