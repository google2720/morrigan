package com.bigkoo.pickerview;

import android.app.Activity;
import android.view.View;

import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.bigkoo.pickerview.view.BasePickerView;
import com.bigkoo.pickerview.view.WheelOptions;

import java.util.ArrayList;

/**
 * 条件选择器
 * Created by Sai on 15/11/22.
 */
public class OptionsPickerView<T> extends BasePickerView implements View.OnClickListener {
    private WheelOptions<T> wheelOptions;
    private OnOptionsSelectListener optionsSelectListener;

    public OptionsPickerView(Activity activity) {
        super(activity);
        activity.getLayoutInflater().inflate(R.layout.pickerview_options, getContainer());

        // 确定按钮
        View okView = findViewById(R.id.ok);
        okView.setOnClickListener(this);

        // 取消按钮
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // 转轮
        View optionsPicker = findViewById(R.id.optionspicker);
        wheelOptions = new WheelOptions<>(optionsPicker);
    }

    public void setPicker(ArrayList<T> options1Items, ArrayList<T> options2Items, ArrayList<T> options3Items) {
        wheelOptions.setPicker(options1Items, options2Items, options3Items);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener1, OnItemSelectedListener listener2, OnItemSelectedListener listener3) {
        wheelOptions.setOnItemSelectedListener(listener1, listener2, listener3);
    }

    /**
     * 设置选中的item位置
     */
    public void setSelectOptions(int option1, int option2, int option3) {
        wheelOptions.setCurrentItems(option1, option2, option3);
    }

    /**
     * 设置选项的单位
     */
    public void setLabels(String label1, String label2, String label3) {
        wheelOptions.setLabels(label1, label2, label3);
    }

    public void setCyclic(boolean cyclic1, boolean cyclic2, boolean cyclic3) {
        wheelOptions.setCyclic(cyclic1, cyclic2, cyclic3);
    }

    @Override
    public void onClick(View v) {
        if (optionsSelectListener != null) {
            int[] optionsCurrentItems = wheelOptions.getCurrentItems();
            optionsSelectListener.onOptionsSelect(optionsCurrentItems[0], optionsCurrentItems[1], optionsCurrentItems[2]);
        }
        dismiss();
    }

    public interface OnOptionsSelectListener {
        void onOptionsSelect(int options1, int option2, int options3);
    }

    public void setOnOptionsSelectListener(OnOptionsSelectListener optionsSelectListener) {
        this.optionsSelectListener = optionsSelectListener;
    }
}
