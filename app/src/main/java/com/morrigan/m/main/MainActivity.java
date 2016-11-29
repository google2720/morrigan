package com.morrigan.m.main;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.morrigan.m.BaseActivity;
import com.morrigan.m.R;
import com.morrigan.m.ble.BleController;

public class MainActivity extends BaseActivity implements NavigateFragment.NavigateListener, MainFragment.Listener, MenuLayout.Callback {

    private MenuLayout mainLayout;
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

    @Override
    public void exitApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("不爱美的都休息去了！让我再美一下");
        builder.setNegativeButton("不要", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BleController.getInstance().quit();
                finish();
            }
        });
        builder.setPositiveButton("美一下", null);
        builder.show();
    }
}
