package com.morrigan.m.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by fei on 2016/10/16.
 */

public class DateUtils {
    public static String getMondayOfThisWeek(SimpleDateFormat df2) {
        Calendar c = Calendar.getInstance();
        int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0)
            day_of_week = 7;
        c.add(Calendar.DATE, -day_of_week + 1);
        return df2 .format(c.getTime());
    }



    /**
     * 得到本周周日
     *
     * @return yyyy-MM-dd
     */
    public static String getSundayOfThisWeek(SimpleDateFormat df2) {
        Calendar c = Calendar.getInstance();
        int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0)
            day_of_week = 7;
        c.add(Calendar.DATE, -day_of_week + 7);
        return df2.format(c.getTime());
    }
}
