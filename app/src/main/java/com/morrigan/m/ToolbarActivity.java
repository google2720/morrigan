package com.morrigan.m;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * activity的基类
 * Created by y on 2016/10/2.
 */
public abstract class ToolbarActivity extends BaseActivity {

    private TextView titleView;
    private ViewGroup contentPanel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        titleView = (TextView) findViewById(R.id.title);
        titleView.setText(getTitle());
        contentPanel = (ViewGroup) findViewById(R.id.contentPanel);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onClickHome();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onClickHome() {
        finish();
    }
}
