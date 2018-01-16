package com.morrigan.m.main;

import android.content.DialogInterface;
import android.content.Intent;
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
        mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.content);
        // MassageController.getInstance().test(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mainLayout.isMenuOpen()) {
            mainLayout.closeMenuWithNoAnim();
        }
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

    private void exitApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.exit_tip);
        builder.setNegativeButton(R.string.exit_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BleController.getInstance().quit();
                MainActivity.super.onBackPressed();
            }
        });
        builder.setPositiveButton(R.string.exit_no, null);
        builder.show();
    }
}
