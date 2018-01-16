package com.bigkoo.pickerview.view;

import android.content.Context;
import android.view.View;

import com.bigkoo.pickerview.R;
import com.bigkoo.pickerview.YearMonthDayPickerView.Type;
import com.bigkoo.pickerview.adapter.NumericUnitWheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class YearMonthDayWheelTime {
    private View view;
    private WheelView wv_year;
    private WheelView wv_month;
    private WheelView wv_day;
    private WheelView wv_hours;
    private WheelView wv_mins;

    private Type type;
    private static final int DEFAULT_START_YEAR = 1990;
    private static final int DEFAULT_END_YEAR = 2100;
    private int startYear = DEFAULT_START_YEAR;
    private int endYear = DEFAULT_END_YEAR;
    private OnTimeSelectListener timeSelectListener;
    private int endMonth = 12;
    private int endDayOfMonth = 31;

    public YearMonthDayWheelTime(View view) {
        super();
        this.view = view;
        type = Type.ALL;
        setView(view);
    }

    public YearMonthDayWheelTime(View view, Type type) {
        super();
        this.view = view;
        this.type = type;
        setView(view);
    }

    public void setPicker(int year, int month, int day) {
        this.setPicker(year, month, day, 0, 0);
    }

    public void setPicker(int year, int month, int day, int h, int m) {
        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
        String[] months_little = {"4", "6", "9", "11"};

        final List<String> list_big = Arrays.asList(months_big);
        final List<String> list_little = Arrays.asList(months_little);

        Context context = view.getContext();

        // 年
        wv_year = (WheelView) view.findViewById(R.id.year);
        wv_year.setAdapter(new NumericUnitWheelAdapter(startYear, endYear, "年"));// 设置"年"的显示数据
        wv_year.setCurrentItem(year - startYear);// 初始化时显示的数据

        // 月
        wv_month = (WheelView) view.findViewById(R.id.month);
        wv_month.setAdapter(new NumericUnitWheelAdapter(1, year == endYear ? Math.min(12, endMonth) : 12, "月"));
        wv_month.setCurrentItem(month);

        // 日
        wv_day = (WheelView) view.findViewById(R.id.day);
        wv_day.setCurrentItem(day - 1);
        setDayAdapter(year, month + 1, list_big, list_big);

        wv_hours = (WheelView) view.findViewById(R.id.hour);
        wv_hours.setAdapter(new NumericUnitWheelAdapter(0, 23, ""));
        wv_hours.setLabel(context.getString(R.string.pickerview_hours));// 添加文字
        wv_hours.setCurrentItem(h);

        wv_mins = (WheelView) view.findViewById(R.id.min);
        wv_mins.setAdapter(new NumericUnitWheelAdapter(0, 59, ""));
        wv_mins.setLabel(context.getString(R.string.pickerview_minutes));// 添加文字
        wv_mins.setCurrentItem(m);

        // 添加"年"监听
        OnItemSelectedListener wheelListener_year = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                int year = index + startYear;
                int month = wv_month.getCurrentItem() + 1;
                int maxItem = (year == endYear ? Math.min(endMonth, 12) : 12);
                wv_month.setAdapter(new NumericUnitWheelAdapter(1, maxItem, "月"));
                if (month > maxItem) {
                    wv_month.setCurrentItem(maxItem - 1);
                    month = maxItem;
                }
                setDayAdapter(year, month, list_big, list_little);
                timeSelectListener.onTimeSelect(getTime());
            }
        };
        // 添加"月"监听
        OnItemSelectedListener wheelListener_month = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                int year = wv_year.getCurrentItem() + startYear;
                int month = index + 1;
                setDayAdapter(year, month, list_big, list_little);
                timeSelectListener.onTimeSelect(getTime());
            }
        };
        // 添加"日"监听
        OnItemSelectedListener wheelListener_day_hour_min = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                timeSelectListener.onTimeSelect(getTime());
            }
        };
        wv_year.setOnItemSelectedListener(wheelListener_year);
        wv_month.setOnItemSelectedListener(wheelListener_month);
        wv_day.setOnItemSelectedListener(wheelListener_day_hour_min);
        wv_hours.setOnItemSelectedListener(wheelListener_day_hour_min);
        wv_mins.setOnItemSelectedListener(wheelListener_day_hour_min);

        switch (type) {
            case YEAR_MONTH_DAY:
                wv_hours.setVisibility(View.GONE);
                wv_mins.setVisibility(View.GONE);
                break;
            case HOURS_MINS:
                wv_year.setVisibility(View.GONE);
                wv_month.setVisibility(View.GONE);
                wv_day.setVisibility(View.GONE);
                break;
            case MONTH_DAY_HOUR_MIN:
                wv_year.setVisibility(View.GONE);
                break;
            case YEAR_MONTH:
                wv_day.setVisibility(View.GONE);
                wv_hours.setVisibility(View.GONE);
                wv_mins.setVisibility(View.GONE);
            default:
                break;
        }
    }

    private void setDayAdapter(int year, int month, List<String> list_big, List<String> list_little) {
        int maxItem;
        int maxDayNum = 31;
        if (year == endYear && month == endMonth) {
            maxDayNum = endDayOfMonth;
        }
        if (list_big.contains(String.valueOf(month))) {
            maxItem = Math.min(maxDayNum, 31);
        } else if (list_little.contains(String.valueOf(month))) {
            maxItem = Math.min(maxDayNum, 30);
        } else if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            maxItem = Math.min(maxDayNum, 29);
        } else {
            maxItem = Math.min(maxDayNum, 28);
        }
        wv_day.setAdapter(new NumericUnitWheelAdapter(1, maxItem, "日"));
        if (wv_day.getCurrentItem() > maxItem - 1) {
            wv_day.setCurrentItem(maxItem - 1);
        }
    }

    /**
     * 设置是否循环滚动
     */
    public void setCyclic(boolean cyclic) {
        wv_year.setCyclic(cyclic);
        wv_month.setCyclic(cyclic);
        wv_day.setCyclic(cyclic);
        wv_hours.setCyclic(cyclic);
        wv_mins.setCyclic(cyclic);
    }

    public String getTime() {
        StringBuffer sb = new StringBuffer();
        sb.append((wv_year.getCurrentItem() + startYear)).append("-")
                .append((wv_month.getCurrentItem() + 1)).append("-")
                .append((wv_day.getCurrentItem() + 1)).append(" ")
                .append(wv_hours.getCurrentItem()).append(":")
                .append(wv_mins.getCurrentItem());
        return sb.toString();
    }

    public int getYear() {
        return wv_year.getCurrentItem() + startYear;
    }

    public int getMonth() {
        return wv_month.getCurrentItem();
    }

    public int getDayOfMonth() {
        return wv_day.getCurrentItem() + 1;
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

    public void setEndCalendar(Calendar calendar) {
        this.endYear = calendar.get(Calendar.YEAR);
        this.endMonth = calendar.get(Calendar.MONTH) + 1;
        this.endDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
    }

    public interface OnTimeSelectListener {
        void onTimeSelect(String date);
    }

    public void setOnTimeSelectListener(OnTimeSelectListener timeSelectListener) {
        this.timeSelectListener = timeSelectListener;
    }
}
