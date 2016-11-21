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
    private CountDownLatch latch;
    private volatile boolean connectSuccess;
    private volatile boolean connectInterrupt;

    public void connectAndBind(Context context, String address, String deviceName) {
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

    private void connect(BluetoothDevice device, int retryCount, boolean sendToServer) {
        SimpleBleCallback cb = new SimpleBleCallback() {
            @Override
            public void onBluetoothOff() {
                connectInterrupt = true;
                latch.countDown();
            }

            @Override
            public void onGattServicesDiscovered(BluetoothDevice device) {
                connectSuccess = true;
                latch.countDown();
            }

            @Override
            public void onGattDisconnected(BluetoothDevice device) {
                latch.countDown();
            }
        };
        try {
            boolean firstBind = device.getAddress().equals(ble.getBindDeviceAddress());
            ble.addCallback(cb);
            for (int i = 0; i < retryCount; i++) {
                latch = new CountDownLatch(1);
                connectSuccess = false;
                ble.connect(device);
                latch.await(20, TimeUnit.SECONDS);
                if (connectInterrupt) {
                    ble.getCallbacks().onBindDeviceFailed(BleError.BLE_OFF);
                    return;
                } else if (connectSuccess) {
                    if (ble.bindDevice(device, sendToServer)) {
                        ble.getCallbacks().onBindDeviceSuccess(device, firstBind);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            Lg.w(TAG, "failed to connect device", e);
        } finally {
            ble.removeCallback(cb);
        }
        ble.getCallbacks().onBindDeviceFailed(BleError.SYSTEM);
    }

    public void reconnect(Context context, String address) {
        Lg.i(TAG, "start reconnect " + address);
        BluetoothDevice device = ble.getRemoteDevice(address);
        if (device == null) {
            ble.getCallbacks().onBindDeviceFailed(BleError.SYSTEM);
            return;
        }
        connect(device, 5, false);
    }
}
