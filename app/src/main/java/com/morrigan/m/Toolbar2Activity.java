package com.morrigan.m;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public abstract class Toolbar2Activity extends BaseActivity {

    private TextView titleView;
    protected ViewGroup contentPanel;
    protected View confirmView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_toolbar2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        titleView = (TextView) findViewById(R.id.title);
        titleView.setText(getTitle());
        contentPanel = (ViewGroup) findViewById(R.id.contentPanel);
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCancel();
            }
        });
        confirmView = findViewById(R.id.confirm);
        confirmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickConfirm();
            }
        });
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (titleView != null) {
            titleView.setText(title);
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        setContentView(getLayoutInflater().inflate(layoutResID, contentPanel, false));
    }

    @Override
    public void setContentView(View view) {
        contentPanel.addView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        contentPanel.addView(view, params);
    }

    protected void onClickCancel() {
        finish();
    }

    protected void onClickConfirm() {
    }
}
