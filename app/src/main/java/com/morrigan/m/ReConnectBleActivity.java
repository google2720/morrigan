package com.morrigan.m;

import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.morrigan.m.ble.BleBindHelper;
import com.morrigan.m.ble.BleCallback;
import com.morrigan.m.ble.BleController;
import com.morrigan.m.ble.SimpleBleCallback;
import com.morrigan.m.c.MassageController;

/**
 * Ble设备重连界面
 * Created by y on 2016/11/21.
 */
public class ReConnectBleActivity extends BaseActivity {

    private BleController ble = BleController.getInstance();
    private BleCallback cb = new SimpleBleCallback() {
        @Override
        public void onBindDeviceSuccess(BluetoothDevice device, boolean firstBind) {
            finish();
        }

        @Override
        public void onBindDeviceFailed(int error) {
            finish();
        }
    };
    private BleBindHelper helper;
    private Handler handler = new Handler();
    private Runnable r = new Runnable() {
        @Override
        public void run() {
            if (helper != null) {
                helper.cancel();
                helper = null;
            }
            finish();
        }
    };
    private AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ble.setAutoReconnect(false);
        ble.addCallback(cb);
        helper = ble.reconnectAsync();
        handler.postDelayed(r, 30000);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.reconnect_tip);
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                r.run();
            }
        });
        builder.setPositiveButton(R.string.action_reconnect, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MassageController.getInstance().gotoDeviceScanActivity(ReConnectBleActivity.this);
                finish();
            }
        });
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialog.dismiss();
        handler.removeCallbacks(r);
        ble.removeCallback(cb);
        if (helper != null) {
            helper.cancel();
            helper = null;
        }
    }
}
