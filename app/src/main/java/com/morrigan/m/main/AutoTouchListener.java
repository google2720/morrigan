package com.morrigan.m.main;

import android.content.ClipData;
import android.content.Intent;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * 点击开始拖拽接口
 * Created by y on 2016/10/19.
 */
public class AutoTouchListener implements View.OnTouchListener {

    private AutoActivity activity;
    private AutoItem item;
    private int touchSlop;
    private float lastMotionY;
    private float lastMotionX;
    private int activePointerId;
    private boolean dragEnabled;

    public AutoTouchListener(AutoActivity activity, AutoItem item) {
        this.activity = activity;
        this.item = item;
        ViewConfiguration configuration = ViewConfiguration.get(activity);
        touchSlop = configuration.getScaledTouchSlop();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastMotionY = event.getY();
                lastMotionX = event.getX();
                activePointerId = event.getPointerId(0);
                dragEnabled = false;
                break;
            case MotionEvent.ACTION_MOVE:
                final int activePointerIndex = event.findPointerIndex(activePointerId);
                if (activePointerIndex == -1) {
                    break;
                }
                final float y = event.getY(activePointerIndex);
                final float x = event.getX(activePointerIndex);
                if (!dragEnabled && (Math.abs(y - lastMotionY) >= touchSlop || Math.abs(x - lastMotionX) >= touchSlop)) {
                    dragEnabled = true;
                    if (!activity.onModeViewTouchDown()) {
                        Intent intent = new Intent();
                        intent.putExtra("data", item);
                        ClipData data = ClipData.newIntent("", intent);
                        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                        v.startDrag(data, shadowBuilder, v, 0);
                    }
                }
                break;
            default:
                break;
        }
        Log.i("auto", "onTouch " + action + " " + dragEnabled);
        return dragEnabled;
    }
}
