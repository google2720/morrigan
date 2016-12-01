package com.bigkoo.pickerview;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigkoo.pickerview.view.BasePickerView;
import com.bigkoo.pickerview.view.YearMonthDayWheelTime;

import java.util.Calendar;

/**
 * 时间选择器
 * Created by Sai on 15/11/22.
 */
public class YearMonthDayPickerView extends BasePickerView implements View.OnClickListener {

    /**
     * 四种选择模式，年月日时分，年月日，时分，月日时分
     */
    public enum Type {
        ALL, YEAR_MONTH_DAY, HOURS_MINS, MONTH_DAY_HOUR_MIN, YEAR_MONTH
    }

    private YearMonthDayWheelTime wheelTime;
    private Type type;
    private OnTimeSelectListener timeSelectListener;

    public YearMonthDayPickerView(Activity activity, Type type) {
        super(activity);
        this.type = type;
        createView(activity.getLayoutInflater(), getContainer());
    }

    private void createView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.pickerview_time, container);
        // 确定按钮
        View btnSubmit = view.findViewById(R.id.ok);
        btnSubmit.setOnClickListener(this);
        // 取消按钮
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        // 时间转轮
        View timePickerView = view.findViewById(R.id.timepicker);
        wheelTime = new YearMonthDayWheelTime(timePickerView, type);
        // wheelTime必须需要一个回调接口
        wheelTime.setOnTimeSelectListener(new YearMonthDayWheelTime.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(String date) {
            }
        });
    }

    /**
     * 设置可以选择的时间范围
     * 要在setTime之前调用才有效果
     *
     * @param startYear 开始年份
     * @param endYear   结束年份
     */
    public void setRange(int startYear, int endYear) {
        wheelTime.setStartYear(startYear);
        wheelTime.setEndYear(endYear);
    }

    /**
     * 设置可以选择的时间范围
     * 要在setTime之前调用才有效果
     */
    public void setRange(int startYear, Calendar calendar) {
        wheelTime.setStartYear(startYear);
        wheelTime.setEndCalendar(calendar);
    }

    /**
     * 设置选中时间
     */
    public void setTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        wheelTime.setPicker(year, month, day, hours, minute);
    }

    /**
     * 设置是否循环滚动
     *
     * @param cyclic 是否循环
     */
    public void setCyclic(boolean cyclic) {
        wheelTime.setCyclic(cyclic);
    }

    @Override
    public void onClick(View v) {
        if (timeSelectListener != null) {
            int year = wheelTime.getYear();
            int month = wheelTime.getMonth();
            int dayOfMonth = wheelTime.getDayOfMonth();
            timeSelectListener.onTimeSelect(year, month, dayOfMonth);
        }
        dismiss();
    }

    public interface OnTimeSelectListener {
        void onTimeSelect(int year, int month, int dayOfMonth);
    }

    public void setOnTimeSelectListener(OnTimeSelectListener timeSelectListener) {
        this.timeSelectListener = timeSelectListener;
    }
}
