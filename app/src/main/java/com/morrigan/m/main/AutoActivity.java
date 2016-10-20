package com.morrigan.m.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.morrigan.m.BaseActivity;
import com.morrigan.m.R;

/**
 * 自动按摩界面
 * Created by y on 2016/10/18.
 */
public class AutoActivity extends BaseActivity {

    private AutoLayout autoLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto);
        findViewById(R.id.massage_soft).setOnTouchListener(new AutoTouchListener(new AutoItem(AutoItem.TYPE_SOFT, R.drawable.massage_soft_check)));
        findViewById(R.id.massage_wave).setOnTouchListener(new AutoTouchListener(new AutoItem(AutoItem.TYPE_WAVE, R.drawable.massage_wave_check)));
        findViewById(R.id.massage_dynamic).setOnTouchListener(new AutoTouchListener(new AutoItem(AutoItem.TYPE_DYNAMIC, R.drawable.massage_dynamic_check)));
        findViewById(R.id.massage_gently).setOnTouchListener(new AutoTouchListener(new AutoItem(AutoItem.TYPE_GENTLY, R.drawable.massage_gently_check)));
        findViewById(R.id.massage_intense).setOnTouchListener(new AutoTouchListener(new AutoItem(AutoItem.TYPE_INTENSE, R.drawable.massage_intense_check)));
        autoLayout = (AutoLayout) findViewById(R.id.auto);
    }

    public void onClickBack(View view) {
        finish();
    }

    public void onClickStart(View view) {
        boolean a = view.isActivated();
        if (a) {
            autoLayout.stop();
            UploadHistoryDataService.startAction(this);
        } else {
            autoLayout.start();
        }
        view.setActivated(!a);
    }
}
