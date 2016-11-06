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

    void onBindDeviceFailed(int error);

    void onBindDeviceSuccess(BluetoothDevice device, boolean firstBind);

    void onGattServicesNoFound(BluetoothDevice device);

    void onFetchBatterySuccess(int value);

    void onFetchBatteryFailed(int error);

    void onMassageFailed(int error);

    void onMassageSuccess();
}