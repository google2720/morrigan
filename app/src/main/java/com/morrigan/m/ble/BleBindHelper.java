package com.morrigan.m.ble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.github.yzeaho.log.Lg;
import com.morrigan.m.UiResult;
import com.morrigan.m.device.DeviceController;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * BLE设备的连接
 * Created by y on 2016/11/21.
 */
public class BleBindHelper {

    private static final String TAG = "BleBindHelper";
    private BleController ble = BleController.getInstance();

    public void connectAndBind(Context context, String address, String deviceName) throws InterruptedException {
        Lg.i(TAG, "start connect and bind " + deviceName + " " + address);
        UiResult<Boolean> result = DeviceController.getInstance().check(context, address);
        if (result.t) {
            Lg.w(TAG, deviceName + "(" + address + ") is already connectAndBind by other user");
            ble.getCallbacks().onBindDeviceFailed(BleError.BIND_BY_OTHER);
            return;
        }
        BluetoothDevice device = ble.getRemoteDevice(address);
        if (device == null) {
            ble.getCallbacks().onBindDeviceFailed(BleError.SYSTEM);
            return;
        }
        Lg.i(TAG, "start connect " + address);
        connect(device, 5, true);
    }

    private void connect(BluetoothDevice device, int retryCount, boolean sendToServer) throws InterruptedException {
        boolean firstBind = device.getAddress().equals(ble.getBindDeviceAddress());
        for (int i = 0; i < retryCount; i++) {
            ConnectBleCallback cb = new ConnectBleCallback();
            try {
                ble.addCallback(cb);
                if (!ble.isEnabled()) {
                    ble.getCallbacks().onBindDeviceFailed(BleError.BLE_OFF);
                    return;
                }
                ble.connect(device);
                cb.await(20, TimeUnit.SECONDS);
                if (cb.interrupt) {
                    ble.getCallbacks().onBindDeviceFailed(BleError.BLE_OFF);
                    return;
                } else if (cb.success) {
                    if (ble.bindDevice(device, sendToServer)) {
                        ble.getCallbacks().onBindDeviceSuccess(device, firstBind);
                    } else {
                        ble.disconnect();
                        ble.getCallbacks().onBindDeviceFailed(BleError.SYSTEM);
                    }
                    return;
                }
            } finally {
                ble.removeCallback(cb);
            }
        }
        ble.getCallbacks().onBindDeviceFailed(BleError.SYSTEM);
    }

    public void reconnect(Context context, String address) throws InterruptedException {
        Lg.i(TAG, "start reconnect " + address);
        BluetoothDevice device = ble.getRemoteDevice(address);
        if (device == null) {
            ble.getCallbacks().onBindDeviceFailed(BleError.SYSTEM);
            return;
        }
        connect(device, 5, false);
    }

    private class ConnectBleCallback extends SimpleBleCallback {

        private CountDownLatch latch = new CountDownLatch(1);
        private volatile boolean success;
        private volatile boolean interrupt;

        @Override
        public void onBluetoothOff() {
            interrupt = true;
            latch.countDown();
        }

        @Override
        public void onGattServicesDiscovered(BluetoothDevice device) {
            success = true;
            latch.countDown();
        }

        @Override
        public void onGattDisconnected(BluetoothDevice device) {
            latch.countDown();
        }

        private void await(int time, TimeUnit timeUnit) throws InterruptedException {
            latch.await(20, timeUnit);
        }
    }
}
