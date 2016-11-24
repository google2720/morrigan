package com.morrigan.m.main;

import android.os.Bundle;

import com.morrigan.m.BaseActivity;
import com.morrigan.m.R;
import com.morrigan.m.ble.BleController;

public class MainActivity extends BaseActivity implements NavigateFragment.NavigateListener, MainFragment.Listener, MenuLayout.Callback {

    private static final int REQUEST_ENABLE_BT = 1;
    private MenuLayout mainLayout;
    private BleController ble = BleController.getInstance();
    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayout = (MenuLayout) findViewById(R.id.mainLayout);
        mainLayout.setCallback(this);
        mainFragment = MainFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.contentContainer, mainFragment).commitAllowingStateLoss();
        // MassageController.getInstance().test(this);
        UploadHistoryDataService.startAction(this);
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

    @Override
    public void onMenuOpenStatusChange(boolean openStatus) {
        if (!openStatus) {
            mainFragment.refresh();
        }
    }
}
