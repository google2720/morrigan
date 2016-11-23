package com.morrigan.m.main;

import android.os.Bundle;

import com.morrigan.m.BaseActivity;
import com.morrigan.m.R;
import com.morrigan.m.ble.BleController;

public class MainActivity extends BaseActivity implements NavigateFragment.NavigateListener, MainFragment.Listener {

    private static final int REQUEST_ENABLE_BT = 1;
    private MenuLayout mainLayout;
    private BleController ble = BleController.getInstance();
    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayout = (MenuLayout) findViewById(R.id.mainLayout);
        mainFragment = MainFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.contentContainer, mainFragment).commitAllowingStateLoss();
    }

    @Override
    public void onMenuClick() {
        if (mainLayout.isMenuOpen()) {
            mainLayout.closeMenu();
            mainFragment.refresh();
        } else {
            mainLayout.openMenu();
        }
    }

    @Override
    public void onBackPressed() {
        if (mainLayout.isMenuOpen()) {
            mainLayout.closeMenu();
            mainFragment.refresh();
        } else {
            exitApp();
        }
    }
}
