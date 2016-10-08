package com.bigkoo.pickerview.view;

import android.view.Gravity;
import android.view.View;

import com.bigkoo.pickerview.DatePickerView.Type;
import com.bigkoo.pickerview.R;
import com.bigkoo.pickerview.adapter.DateWheelAdapter;
import com.bigkoo.pickerview.adapter.NumericWheelAdapter2;
import com.bigkoo.pickerview.lib.WheelView;

import java.util.Calendar;


public class WheelDate {
    private View view;
    private WheelView wv_day;
    private WheelView wv_hours;
    private WheelView wv_mins;

    private Type type;
    private static final int DEFAULT_START_YEAR = 1990;
    private static final int DEFAULT_END_YEAR = 2100;
    private int startYear = DEFAULT_START_YEAR;
    private int endYear = DEFAULT_END_YEAR;
    private Calendar now;

    public WheelDate(View view, Type type) {
        super();
        this.view = view;
        this.type = type;
        setView(view);
    }

    public void setPicker(Calendar calendar, int h, int m, int minDayValue, int maxDayValue, int initDayValue) {
        this.now = Calendar.getInstance();
        // 日
        wv_day = (WheelView) view.findViewById(R.id.day);
        wv_day.setAdapter(new DateWheelAdapter(now, minDayValue, maxDayValue, initDayValue));
        wv_day.setGravity(Gravity.RIGHT);
        wv_day.setCurrentItem(daysBetween(now, calendar) + initDayValue);

        wv_hours = (WheelView) view.findViewById(R.id.hour);
        wv_hours.setAdapter(new NumericWheelAdapter2(0, 23));
        wv_hours.setCurrentItem(h);

        wv_mins = (WheelView) view.findViewById(R.id.min);
        wv_mins.setAdapter(new NumericWheelAdapter2(0, 59));
        wv_mins.setCurrentItem(m);
    }

    private static int daysBetween(Calendar c1, Calendar c2) {
        c1 = (Calendar) c1.clone();
        c1.set(Calendar.HOUR_OF_DAY, 0);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.SECOND, 0);
        c1.set(Calendar.MILLISECOND, 0);
        c2 = (Calendar) c2.clone();
        c2.set(Calendar.HOUR_OF_DAY, 0);
        c2.set(Calendar.MINUTE, 0);
        c2.set(Calendar.SECOND, 0);
        c2.set(Calendar.MILLISECOND, 0);
        long time1 = c1.getTimeInMillis();
        long time2 = c2.getTimeInMillis();
        long time3 = time2 - time1;
        long betweenDays = time3 / (1000 * 3600 * 24);
        return (int) betweenDays;
    }

    /**
     * 设置是否循环滚动
     */
    public void setCyclic(boolean dayCyclic, boolean hourCyclic, boolean minCyclic) {
        wv_day.setCyclic(dayCyclic);
        wv_hours.setCyclic(hourCyclic);
        wv_mins.setCyclic(minCyclic);
    }

    public int getYear() {
        Calendar c = (Calendar) now.clone();
        DateWheelAdapter adapter = (DateWheelAdapter) wv_day.getAdapter();
        c.add(Calendar.DAY_OF_MONTH, wv_day.getCurrentItem() - adapter.getInitValue());
        return c.get(Calendar.YEAR);
    }

    public int getMonth() {
        Calendar c = (Calendar) now.clone();
        DateWheelAdapter adapter = (DateWheelAdapter) wv_day.getAdapter();
        c.add(Calendar.DAY_OF_MONTH, wv_day.getCurrentItem() - adapter.getInitValue());
        return c.get(Calendar.MONTH);
    }

    public int getDayOfMonth() {
        Calendar c = (Calendar) now.clone();
        DateWheelAdapter adapter = (DateWheelAdapter) wv_day.getAdapter();
        c.add(Calendar.DAY_OF_MONTH, wv_day.getCurrentItem() - adapter.getInitValue());
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public int getHour() {
        return wv_hours.getCurrentItem();
    }

    public int getMinute() {
        return wv_mins.getCurrentItem();
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }
}
