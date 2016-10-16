package com.morrigan.m.device;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.morrigan.m.BaseActivity;
import com.morrigan.m.R;
import com.morrigan.m.ble.BleCallback;
import com.morrigan.m.ble.BleController;
import com.morrigan.m.ble.SimpleBleCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * 设备结果界面
 * Created by y on 2016/10/13.
 */
public class DeviceScanResultActivity extends BaseActivity implements DeviceScanResultAdapter.Listener {

    private BleController ble = BleController.getInstance();
    private BleCallback cb = new SimpleBleCallback() {
        @Override
        public void onGattConnecting(BluetoothDevice device) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    connectStateView.setVisibility(View.VISIBLE);
                }
            });
        }

        @Override
        public void onBindDeviceSuccess(BluetoothDevice device, boolean firstBind) {
            Intent intent = new Intent(DeviceScanResultActivity.this, DeviceBindSuccessActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void onBindDeviceFailed(BluetoothDevice device) {
            Intent intent = new Intent(DeviceScanResultActivity.this, DeviceBindFailedActivity.class);
            startActivity(intent);
            finish();
        }
    };
    private View connectStateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ble.addCallback(cb);
        @SuppressWarnings("unchecked")
        ArrayList<Device> devices = (ArrayList<Device>) getIntent().getSerializableExtra("data");
        setContentView(R.layout.activity_device_scan_result);
        connectStateView = findViewById(R.id.connectState);
        ImageView iconView = (ImageView) findViewById(R.id.icon);
        Picasso.with(this).load(R.drawable.device_scan_result_top).into(iconView);
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DeviceScanResultAdapter adapter = new DeviceScanResultAdapter(this, this);
        recyclerView.setAdapter(adapter);
        adapter.setData(devices);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ble.removeCallback(cb);
    }

    @Override
    public void onListItemClick(View v, Device device) {
        ble.getCallbacks().onBindDeviceSuccess(null, false);
    }
}