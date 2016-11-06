package com.morrigan.m.main;

import android.content.ClipData;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;

/**
 * 点击开始拖拽接口
 * Created by y on 2016/10/19.
 */
public class AutoTouchListener implements View.OnTouchListener {

    private AutoActivity activity;
    private AutoItem item;

    public AutoTouchListener(AutoActivity activity, AutoItem item) {
        this.activity = activity;
        this.item = item;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && !activity.onTouchDown()) {
            Intent intent = new Intent();
            intent.putExtra("data", item);
            ClipData data = ClipData.newIntent("", intent);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(data, shadowBuilder, v, 0);
            return true;
        }
        return false;
    }
}
