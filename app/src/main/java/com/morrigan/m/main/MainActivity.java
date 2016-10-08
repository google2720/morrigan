package com.morrigan.m.main;

import android.os.Bundle;

import com.morrigan.m.BaseActivity;
import com.morrigan.m.R;

public class MainActivity extends BaseActivity implements NavigateFragment.NavigateListener, MainFragment.Listener {

    private MainLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayout = (MainLayout) findViewById(R.id.mainLayout);
        getSupportFragmentManager().beginTransaction().replace(R.id.contentContainer, MainFragment.newInstance()).commitAllowingStateLoss();
    }

    @Override
    public void onMenuClick() {
        if (mainLayout.isMenuOpen()) {
            mainLayout.closeMenu();
        } else {
            mainLayout.openMenu();
        }
    }

    @Override
    public void onBackPressed() {
        if (mainLayout.isMenuOpen()) {
            mainLayout.closeMenu();
        } else {
            super.onBackPressed();
        }
    }
}
