package com.bigkoo.pickerview.adapter;

import java.util.ArrayList;

/**
 * The simple Array wheel adapter
 *
 * @param <T> the element type
 */
public class ArrayWheelAdapter<T> implements WheelAdapter {

    // items
    private ArrayList<T> items;

    // length
    private int length;

    /**
     * Constructor
     *
     * @param items  the items
     * @param length the max items length
     */
    public ArrayWheelAdapter(ArrayList<T> items, int length) {
        this.items = items;
        this.length = length;
    }

    public ArrayWheelAdapter(ArrayList<T> items) {
        this(items, items == null ? 0 : items.size());
    }

    @Override
    public Object getItem(int index) {
        if (items != null && index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return "";
    }

    @Override
    public int getItemsCount() {
        return length;
    }

    @Override
    public int indexOf(Object o) {
        return items == null ? -1 : items.indexOf(o);
    }

}
