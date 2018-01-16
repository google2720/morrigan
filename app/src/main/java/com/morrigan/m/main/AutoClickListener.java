package com.morrigan.m.main;

import android.util.Log;
import android.view.View;

/**
 * 自动按摩按钮点击事件
 * Created by y on 2016/11/29.
 */
public class AutoClickListener implements View.OnClickListener {

    private AutoActivity activity;
    private int type;

    public AutoClickListener(AutoActivity activity, int type) {
        this.activity = activity;
        this.type = type;
    }

    @Override
    public void onClick(View v) {
        Log.i("auto", "onClick " + type);
        activity.onModeViewClick(v, type);
    }
}
