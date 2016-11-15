package com.morrigan.m.main;

import android.os.Bundle;

import com.morrigan.m.BaseActivity;
import com.morrigan.m.R;

public class MainActivity extends BaseActivity implements NavigateFragment.NavigateListener, MainFragment.Listener {

    private MenuLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayout = (MenuLayout) findViewById(R.id.mainLayout);
        getSupportFragmentManager().beginTransaction().replace(R.id.contentContainer, MainFragment.newInstance()).commitAllowingStateLoss();
        // MassageController.getInstance().test(this);
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
            exitApp();
        }
    }
}
