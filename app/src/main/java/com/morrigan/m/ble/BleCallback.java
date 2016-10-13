package com.morrigan.m.ble;

import android.bluetooth.BluetoothDevice;

public interface BleCallback {

    void onBluetoothOff();

    void onBluetoothOn();

    void onLeScan(BluetoothDevice device);

    void onLeScanFailed(int error);

    void onGattConnecting(BluetoothDevice device);

    void onGattDisconnected(BluetoothDevice device);

    void onGattConnected(BluetoothDevice device);

    void onGattServicesDiscovered(BluetoothDevice device);

    void onBindDeviceFailed(BluetoothDevice device);

    void onBindDeviceSuccess(BluetoothDevice device, boolean firstBind);
}