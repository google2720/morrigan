package com.bigkoo.pickerview;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigkoo.pickerview.view.BasePickerView;
import com.bigkoo.pickerview.view.WheelDate;

import java.util.Calendar;
import java.util.Date;

/**
 * 时间选择器
 * Created by Sai on 15/11/22.
 */
public class DatePickerView extends BasePickerView implements View.OnClickListener {

    /**
     * 四种选择模式，年月日时分，年月日，时分，月日时分
     */
    public enum Type {
        DAY_HOUR_MIN;
    }

    private WheelDate wheelTime;
    private OnTimeSelectListener timeSelectListener;
    private Type type;

    public DatePickerView(Activity activity, Type type) {
        super(activity);
        this.type = type;
        createView(activity.getLayoutInflater(), getContainer());
    }

    private void createView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.pickerview_date, container);

        // 确定按钮
        View submitView = view.findViewById(R.id.btnSubmit);
        submitView.setOnClickListener(this);

        // 时间转轮
        View timePickerView = view.findViewById(R.id.timepicker);
        wheelTime = new WheelDate(timePickerView, type);
    }

    /**
     * 设置选中时间
     *
     * @param date 时间
     */
    public void setTime(Date date, int minDayValue, int maxDayValue, int initDayValue) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        wheelTime.setPicker(calendar, hours, minute, minDayValue, maxDayValue, initDayValue);
    }

    /**
     * 设置是否循环滚动
     */
    public void setCyclic(boolean dayCyclic, boolean hourCyclic, boolean minCyclic) {
        wheelTime.setCyclic(dayCyclic, hourCyclic, minCyclic);
    }

    @Override
    public void onClick(View v) {
        if (timeSelectListener != null) {
            int year = wheelTime.getYear();
            int month = wheelTime.getMonth();
            int dayOfMonth = wheelTime.getDayOfMonth();
            int hour = wheelTime.getHour();
            int minute = wheelTime.getMinute();
            timeSelectListener.onTimeSelect(year, month, dayOfMonth, hour, minute);
        }
        dismiss();
    }

    public interface OnTimeSelectListener {
        void onTimeSelect(int year, int month, int dayOfMonth, int hour, int minute);
    }

    public void setOnTimeSelectListener(OnTimeSelectListener timeSelectListener) {
        this.timeSelectListener = timeSelectListener;
    }
}
