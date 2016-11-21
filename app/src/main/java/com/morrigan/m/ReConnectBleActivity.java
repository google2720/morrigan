package com.morrigan.m;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.morrigan.m.ble.BleCallback;
import com.morrigan.m.ble.BleController;
import com.morrigan.m.ble.SimpleBleCallback;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_connect);
        ble.setAutoReconnect(false);
        ble.addCallback(cb);
        ble.reconnectAsync();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ble.removeCallback(cb);
    }
}
