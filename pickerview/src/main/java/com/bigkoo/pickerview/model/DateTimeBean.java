package com.bigkoo.pickerview.model;

import com.bigkoo.pickerview.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Sai on 15/11/22.
 */
public class DateTimeBean implements IPickerViewData {

    private Calendar calendar;
    private Calendar now;
    private String text;
    private int index;

    public DateTimeBean(Calendar calendar, Calendar now, int index) {
        this.calendar = calendar;
        this.now = now;
        this.index = index;
        this.text = createText();
    }

    //这个用来显示在PickerView上面的字符串,PickerView会通过IPickerViewData获取getPickerViewText方法显示出来。
    @Override
    public String getPickerViewText() {
        return text;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public int getIndex() {
        return index;
    }

    private String createText() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        int nowYear = now.get(Calendar.YEAR);
        int nowMonth = now.get(Calendar.MONTH);
        int nowDayOfMonth = now.get(Calendar.DAY_OF_MONTH);

        String txt;
        String week = TimeUtils.getCapsWeekDay(calendar.get(Calendar.DAY_OF_WEEK));
        if (year == nowYear && month == nowMonth && dayOfMonth == nowDayOfMonth) {
            txt = "今天";
        } else if (year == nowYear) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 " + week, Locale.CHINA);
            txt = sdf.format(calendar.getTime());
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 " + week, Locale.CHINA);
            txt = sdf.format(calendar.getTime());
        }
        return txt;
    }
}
