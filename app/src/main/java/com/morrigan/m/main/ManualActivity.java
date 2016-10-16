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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);
    }

    public void onClickBack(View view) {
        onBackPressed();
    }
}
