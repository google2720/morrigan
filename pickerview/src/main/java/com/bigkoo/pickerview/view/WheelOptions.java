package com.bigkoo.pickerview.view;

import android.view.View;

import com.bigkoo.pickerview.R;
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;

import java.util.ArrayList;

public class WheelOptions<T> {
    private View view;
    private WheelView wv_option1;
    private WheelView wv_option2;
    private WheelView wv_option3;

    private ArrayList<T> mOptions1Items;
    private ArrayList<T> mOptions2Items;
    private ArrayList<T> mOptions3Items;

    public WheelOptions(View view) {
        this.view = view;
        setView(view);
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void setPicker(ArrayList<T> optionsItems) {
        setPicker(optionsItems, null, null);
    }

    public void setPicker(ArrayList<T> options1Items, ArrayList<T> options2Items) {
        setPicker(options1Items, options2Items, null);
    }

    public void setPicker(ArrayList<T> options1Items, ArrayList<T> options2Items, ArrayList<T> options3Items) {
        this.mOptions1Items = options1Items;
        this.mOptions2Items = options2Items;
        this.mOptions3Items = options3Items;

        // 选项1
        wv_option1 = (WheelView) view.findViewById(R.id.options1);
        wv_option1.setAdapter(new ArrayWheelAdapter<>(mOptions1Items));// 设置显示数据
        wv_option1.setCurrentItem(0);// 初始化时显示的数据

        // 选项2
        wv_option2 = (WheelView) view.findViewById(R.id.options2);
        if (mOptions2Items != null) {
            wv_option2.setAdapter(new ArrayWheelAdapter<>(mOptions2Items));// 设置显示数据
            wv_option2.setCurrentItem(0);// 初始化时显示的数据
        } else {
            wv_option2.setVisibility(View.GONE);
        }
        // 选项3
        wv_option3 = (WheelView) view.findViewById(R.id.options3);
        if (mOptions3Items != null) {
            wv_option3.setAdapter(new ArrayWheelAdapter<>(mOptions3Items));// 设置显示数据
            wv_option3.setCurrentItem(0);// 初始化时显示的数据
        } else {
            wv_option3.setVisibility(View.GONE);
        }
    }

    public void setPicker2(ArrayList<T> optionsItems) {
        wv_option2.setAdapter(new ArrayWheelAdapter<>(optionsItems));
        int index = 0;
        if (optionsItems != null) {
            index = wv_option2.getCurrentItem();
            int size = optionsItems.size();
            if (index >= size) {
                index = size - 1;
            }
        }
        wv_option2.setCurrentItem(index);
    }

    public void setPicker3(ArrayList<T> optionsItems) {
        wv_option3.setAdapter(new ArrayWheelAdapter<>(optionsItems));
        int index = 0;
        if (optionsItems != null) {
            index = wv_option3.getCurrentItem();
            int size = optionsItems.size();
            if (index >= size) {
                index = size - 1;
            }
        }
        wv_option3.setCurrentItem(index);
    }

    public ArrayList<T> getOptions1Items() {
        return mOptions1Items;
    }

    public ArrayList<T> getOptions2Items() {
        return mOptions2Items;
    }

    public ArrayList<T> getOptions3Items() {
        return mOptions3Items;
    }

    public void setGravity(int gravity1, int gravity2, int gravity3) {
        wv_option1.setGravity(gravity1);
        wv_option2.setGravity(gravity2);
        wv_option3.setGravity(gravity3);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener1, OnItemSelectedListener listener2, OnItemSelectedListener listener3) {
        wv_option1.setOnItemSelectedListener(listener1);
        wv_option2.setOnItemSelectedListener(listener2);
        wv_option3.setOnItemSelectedListener(listener3);
    }

    /**
     * 设置选项的单位
     *
     * @param label1 单位
     * @param label2 单位
     * @param label3 单位
     */
    public void setLabels(String label1, String label2, String label3) {
        if (label1 != null) {
            wv_option1.setLabel(label1);
        }
        if (label2 != null) {
            wv_option2.setLabel(label2);
        }
        if (label3 != null) {
            wv_option3.setLabel(label3);
        }
    }

    /**
     * 分别设置第一二三级是否循环滚动
     *
     * @param cyclic1,cyclic2,cyclic3 是否循环
     */
    public void setCyclic(boolean cyclic1, boolean cyclic2, boolean cyclic3) {
        wv_option1.setCyclic(cyclic1);
        wv_option2.setCyclic(cyclic2);
        wv_option3.setCyclic(cyclic3);
    }

    /**
     * 返回当前选中的结果对应的位置数组 因为支持三级联动效果，分三个级别索引，0，1，2
     *
     * @return 索引数组
     */
    public int[] getCurrentItems() {
        int[] currentItems = new int[3];
        currentItems[0] = wv_option1.getCurrentItem();
        currentItems[1] = wv_option2.getCurrentItem();
        currentItems[2] = wv_option3.getCurrentItem();
        return currentItems;
    }

    public void setCurrentItems(int option1, int option2, int option3) {
        wv_option1.setCurrentItem(option1);
        wv_option2.setCurrentItem(option2);
        wv_option3.setCurrentItem(option3);
    }

    public void setCurrentItems2(int option2) {
        wv_option2.setCurrentItem(option2);
    }

    public void setCurrentItems3(int option3) {
        wv_option3.setCurrentItem(option3);
    }
}
