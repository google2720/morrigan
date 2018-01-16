package com.github.yzeaho.log;

import android.content.Context;
import android.os.Process;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * java的日志记录
 * Created by y on 2016/8/26.
 */
public class AndroidLgImpl implements LgInterface {

    private Context context;
    private int level = Log.INFO;

    public AndroidLgImpl(Context _context) {
        this.context = _context.getApplicationContext();
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public void d(String tag, String msg) {
        if (level <= Log.DEBUG) {
            Log.d(tag, msg);
            write("D", tag, msg);
        }
    }

    @Override
    public void i(String tag, String msg) {
        if (level <= Log.INFO) {
            Log.i(tag, msg);
            write("I", tag, msg);
        }
    }

    @Override
    public void w(String tag, String msg, Throwable e) {
        if (level <= Log.WARN) {
            Log.w(tag, msg, e);
            write("W", tag, msg, e);
        }
    }

    @Override
    public void w(String tag, String msg) {
        if (level <= Log.WARN) {
            Log.w(tag, msg);
            write("W", tag, msg);
        }
    }

    @Override
    public void e(String tag, String msg, Throwable e) {
        if (level <= Log.ERROR) {
            Log.e(tag, msg, e);
            write("E", tag, msg, e);
        }
    }

    private void write(String level, String tag, String msg, Throwable tr) {
        write(level, tag, msg + "\r\n" + Log.getStackTraceString(tr));
    }

    private void write(String level, String tag, String text) {
        LogService.log(context, formatLog(level, tag, text == null ? "" : text));
    }

    /**
     * 格式化日志格式
     */
    private String formatLog(String level, String tag, String text) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);
        return String.format("%s %s/%s(%s) [%s] %s", sdf.format(new Date()), level, tag, Process.myPid(), Thread.currentThread().getName(), text);
    }
}
