package com.morrigan.m.main;

/**
 * 日历的持续时间
 * <p/>
 * Created by y on 2016/5/20.
 */
public class Duration {

    /**
     * 将时间转换为字符串
     *
     * @param time 时间，单位秒
     */
    public String toValue(long time) {
        return h(time);
    }

    private String d(long time) {
        if (time < 24 * 3600) {
            return h(time);
        } else {
            return addZero(time / (24 * 3600)) + ":" + h(time % (24 * 3600));
        }
    }

    private String h(long time) {
        if (time < 3600) {
            return m(time);
        } else {
            return addZero(time / 3600) + ":" + m(time % 3600);
        }
    }

    private String m(long time) {
        if (time < 60) {
            return s(time, true);
        } else {
            return addZero(time / 60) + ":" + s(time % 60, false);
        }
    }

    private String s(long time, boolean b) {
        if (b) {
            return "00:" + s(time, false);
        } else {
            return time == 0 ? "00" : addZero(time);
        }
    }

    private String addZero(long time) {
        if (time < 10) {
            return "0" + time;
        } else {
            return String.valueOf(time);
        }
    }
}
