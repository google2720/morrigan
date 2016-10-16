package com.morrigan.m.main;

import android.os.Bundle;
import android.view.View;

import com.morrigan.m.BaseActivity;
import com.morrigan.m.R;

/**
 * 手动按摩界面
 * Created by y on 2016/10/15.
 */
public class ManualActivity extends BaseActivity {

    private ManualView manualView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);
        manualView = (ManualView) findViewById(R.id.manual);
    }

    public void onClickBack(View view) {
        onBackPressed();
    }

    public void onClickAddGear(View view) {
        manualView.addGear();
    }

    public void onClickDeleteGear(View view) {
        manualView.deleteGear();
    }

    public void onClickLeftBra(View view) {
        view.setActivated(!view.isActivated());
    }

    public void onClickRightBra(View view) {
        view.setActivated(!view.isActivated());
    }
}
