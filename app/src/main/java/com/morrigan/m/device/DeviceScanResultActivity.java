package com.morrigan.m.device;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.github.yzeaho.common.ToastUtils;
import com.morrigan.m.BaseActivity;
import com.morrigan.m.R;
import com.morrigan.m.ble.BleCallback;
import com.morrigan.m.ble.BleController;
import com.morrigan.m.ble.BleError;
import com.morrigan.m.ble.SimpleBleCallback;
import com.morrigan.m.ble.db.Device;
import com.morrigan.m.c.UserController;
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
        public void onBindDeviceSuccess(BluetoothDevice device, boolean firstBind) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(DeviceScanResultActivity.this, DeviceBindSuccessActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }

        @Override
        public void onBindDeviceFailed(final int error) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (error == BleError.BIND_BY_OTHER) {
                        ToastUtils.show(getApplicationContext(), "设备已被绑定");
                    }
                    Intent intent = new Intent(DeviceScanResultActivity.this, DeviceBindFailedActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    };
    private View connectStateView;
    private DeviceScanResultAdapter adapter;
    private View connectIconView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ble.addCallback(cb);
        @SuppressWarnings("unchecked")
        ArrayList<UiData> devices = (ArrayList<UiData>) getIntent().getSerializableExtra("data");
        setContentView(R.layout.activity_device_scan_result);
        connectStateView = findViewById(R.id.connectState);
        connectIconView = findViewById(R.id.connectIcon);
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
        adapter = new DeviceScanResultAdapter(this, this);
        recyclerView.setAdapter(adapter);
        QueryDeviceNameTask task = new QueryDeviceNameTask(this, devices);
        AsyncTaskCompat.executeParallel(task);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ble.removeCallback(cb);
    }

    @Override
    public void onListItemClick(View v, UiData device) {
        if (connectStateView.getVisibility() == View.GONE) {
            connectStateView.setVisibility(View.VISIBLE);
            connectIconView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate));
            ble.connectAndBindAsync(device.name, device.address);
        }
    }

    private class QueryDeviceNameTask extends AsyncTask<Void, Void, ArrayList<UiData>> {

        private Context context;
        private ArrayList<UiData> devices;

        private QueryDeviceNameTask(Context _context, ArrayList<UiData> devices) {
            this.context = _context;
            this.devices = devices;
        }

        @Override
        protected ArrayList<UiData> doInBackground(Void... params) {
            if (devices != null && !devices.isEmpty()) {
                String userId = UserController.getInstance().getUserId(context);
                for (UiData d : devices) {
                    Device device = Device.restoreByAddress(context, userId, d.address);
                    if (device != null) {
                        d.name = device.name;
                    }
                }
            }
            return devices;
        }

        @Override
        protected void onPostExecute(ArrayList<UiData> result) {
            if (isFinishing()) {
                return;
            }
            adapter.setData(result);
        }
    }
}