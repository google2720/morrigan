package com.morrigan.m.main;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.morrigan.m.R;

/**
 * 自动模式下的布局
 * Created by y on 2016/10/19.
 */
public class AutoItemView extends ImageButton implements View.OnDragListener, View.OnTouchListener {

    private AutoItem autoItem;

    public AutoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnDragListener(this);
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        int action = event.getAction();
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                break;
            case DragEvent.ACTION_DROP:
                ClipData data = event.getClipData();
                ClipData.Item item = data.getItemAt(0);
                if (item != null) {
                    Intent intent = item.getIntent();
                    if (intent != null) {
                        AutoItem autoItem = (AutoItem) intent.getSerializableExtra("data");
                        if (autoItem != null) {
                            this.autoItem = autoItem;
                            ((ImageView) v).setImageResource(autoItem.resId);
                            setOnTouchListener(this);
                        }
                    }
                }
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && autoItem != null) {
            Intent intent = new Intent();
            intent.putExtra("data", autoItem);
            ClipData data = ClipData.newIntent("", intent);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(data, shadowBuilder, v, 0);
            setImageResource(R.drawable.massage_empty);
            autoItem = null;
            return true;
        } else {
            return false;
        }
    }
}
