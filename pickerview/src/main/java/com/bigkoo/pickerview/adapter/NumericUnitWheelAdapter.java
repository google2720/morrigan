package com.bigkoo.pickerview.adapter;


/**
 * Numeric Wheel adapter.
 */
public class NumericUnitWheelAdapter implements WheelAdapter {

    /**
     * The default min value
     */
    public static final int DEFAULT_MAX_VALUE = 9;

    /**
     * The default max value
     */
    private static final int DEFAULT_MIN_VALUE = 0;

    // Values
    private int minValue;
    private int maxValue;
    String unit = "";

    /**
     * Default constructor
     */
    public NumericUnitWheelAdapter() {
        this(DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE, "");
    }

    /**
     * Constructor
     *
     * @param minValue the wheel min value
     * @param maxValue the wheel max value
     */
    public NumericUnitWheelAdapter(int minValue, int maxValue, String unit) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.unit = unit;
    }

    @Override
    public Object getItem(int index) {
        if (index >= 0 && index < getItemsCount()) {
            int value = minValue + index;
            return value + unit;
        }
        return 0;
    }

    @Override
    public int getItemsCount() {
        return maxValue - minValue + 1;
    }

    @Override
    public int indexOf(Object o) {
        String str = (String) o;
        str.trim();
        if (str.contains(unit)) {
            int end = str.indexOf(unit);
            str = str.substring(0, end);
        }
        int index = Integer.parseInt(str) - minValue;
        return index;
    }
}
