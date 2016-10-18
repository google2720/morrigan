package com.morrigan.m.main;

import android.content.ClipData;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.DragEvent;
import android.view.View;

import com.morrigan.m.BaseActivity;
import com.morrigan.m.R;

/**
 * 自动按摩界面
 * Created by y on 2016/10/18.
 */
public class AutoActivity extends BaseActivity {

    private View sortView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto);
        sortView = findViewById(R.id.massage_soft);
//        sortView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                ClipData dragData = ClipData.newPlainText("", "");
//                View.DrawShadowBuilder myShadow = new MyDragShadowBuilder(v);
//                v.startDrag(dragData, myShadow, null, 0);
//                return true;
//            }
//        });
//        sortView.setOnDragListener(new View.OnDragListener() {
//            @Override
//            public boolean onDrag(View v, DragEvent event) {
//                return false;
//            }
//        });
    }

    public void onClickBack(View view) {
        finish();
    }
}
