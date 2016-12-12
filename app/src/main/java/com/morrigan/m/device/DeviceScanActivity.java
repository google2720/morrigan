package com.morrigan.m.device;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.morrigan.m.BaseActivity;
import com.morrigan.m.R;
import com.morrigan.m.ble.BleCallback;
import com.morrigan.m.ble.BleController;
import com.morrigan.m.ble.SimpleBleCallback;
import com.morrigan.m.main.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;

public class DeviceScanActivity extends BaseActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            gotoResult();
            finish();
        }
    };
    private ArrayList<UiData> devices = new ArrayList<>();
    private HashSet<String> addresses = new HashSet<>();
    private BleController ble = BleController.getInstance();
    private BleCallback cb = new SimpleBleCallback() {
        @Override
        public void onLeScan(BluetoothDevice device) {
            if (!TextUtils.isEmpty(device.getName())) {
                if (!addresses.contains(device.getAddress())) {
                    UiData d = new UiData();
                    d.name = device.getName();
                    d.address = device.getAddress();
                    devices.add(d);
                    addresses.add(device.getAddress());
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ble.setAutoConnect(false);
        ble.setAutoReconnect(false);
        ble.disconnect();
        ble.addCallback(cb);
        setContentView(R.layout.activity_device_scan);
        ImageView iconView = (ImageView) findViewById(R.id.icon);
        Picasso.with(this).load(R.drawable.device_scan_top).into(iconView);
        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanLeDevice(false);
                if (getIntent().getBooleanExtra("stopGotoMain", true)) {
                    Intent intent = new Intent(DeviceScanActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensures Bluetooth is enabled on the device. If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!ble.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        } else {
            scanLeDevice(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ble.removeCallback(cb);
        handler.removeCallbacks(runnable);
    }

    private void scanLeDevice(boolean b) {
        if (b) {
            devices.clear();
            ble.startLeScan();
            handler.postDelayed(runnable, 10000);
        } else {
            ble.stopLeScan();
            handler.removeCallbacks(runnable);
        }
    }

    private void gotoResult() {
        Intent intent = new Intent(DeviceScanActivity.this, DeviceScanResultActivity.class);
        intent.putExtra("data", devices);
        intent.putExtra("backGotoMain", getIntent().getBooleanExtra("backGotoMain", true));
        startActivity(intent);
    }
}
