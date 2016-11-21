package com.morrigan.m.main;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.morrigan.m.BaseActivity;
import com.morrigan.m.R;
import com.morrigan.m.ble.BleController;

public class MainActivity extends BaseActivity implements NavigateFragment.NavigateListener, MainFragment.Listener {

    private static final int REQUEST_ENABLE_BT = 1;
    private MenuLayout mainLayout;
    private BleController ble = BleController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayout = (MenuLayout) findViewById(R.id.mainLayout);
        getSupportFragmentManager().beginTransaction().replace(R.id.contentContainer, MainFragment.newInstance()).commitAllowingStateLoss();
        if (!ble.isEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.no_open_ble_tip);
            builder.setNegativeButton(R.string.action_cancel, null);
            builder.setPositiveButton(R.string.action_connect, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                }
            });
            builder.show();
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
}
