package com.bigkoo.pickerview.adapter;


import com.bigkoo.pickerview.model.DateTimeBean;

import java.util.Calendar;

/**
 * Numeric Wheel adapter.
 */
public class DateWheelAdapter implements WheelAdapter {

    // Values
    private int minValue;
    private int maxValue;
    private int initValue;
    private Calendar now;

    public DateWheelAdapter(Calendar now, int minValue, int maxValue, int initValue) {
        this.now = now;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.initValue = initValue;
    }

    public int getInitValue() {
        return initValue;
    }

    @Override
    public Object getItem(int index) {
        Calendar c = (Calendar) now.clone();
        c.add(Calendar.DAY_OF_MONTH, index - initValue);
        return new DateTimeBean(c, now, index);
    }

    @Override
    public int getItemsCount() {
        return maxValue - minValue + 1;
    }

    @Override
    public int indexOf(Object o) {
        DateTimeBean bean = (DateTimeBean) o;
        return bean.getIndex();
    }
}
