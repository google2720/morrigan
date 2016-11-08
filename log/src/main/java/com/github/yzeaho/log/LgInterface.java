package com.github.yzeaho.log;

/**
 * 日志接口
 * Created by y on 2016/8/26.
 */
public interface LgInterface {

    void setLevel(int level);

    void d(String tag, String msg);

    void i(String tag, String msg);

    void w(String tag, String msg, Throwable e);

    void w(String tag, String msg);

    void e(String tag, String msg, Throwable e);
}
