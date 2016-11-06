package com.morrigan.m.ble;

import android.bluetooth.BluetoothDevice;

public class SimpleBleCallback implements BleCallback {

    @Override
    public void onBluetoothOff() {
    }

    @Override
    public void onBluetoothOn() {
    }

    @Override
    public void onLeScan(BluetoothDevice device) {
    }

    @Override
    public void onLeScanFailed(int error) {
    }

    @Override
    public void onGattConnecting(BluetoothDevice device) {
    }

    @Override
    public void onGattDisconnected(BluetoothDevice device) {
    }

    @Override
    public void onGattConnected(BluetoothDevice device) {
    }

    @Override
    public void onGattServicesDiscovered(BluetoothDevice device) {
    }

    @Override
    public void onBindDeviceFailed(int error) {
    }

    @Override
    public void onBindDeviceSuccess(BluetoothDevice device, boolean firstBind) {
    }

    @Override
    public void onGattServicesNoFound(BluetoothDevice device) {
    }

    @Override
    public void onFetchBatterySuccess(int value) {
    }

    @Override
    public void onFetchBatteryFailed(int error) {
    }

    @Override
    public void onMassageFailed(int error) {
    }

    @Override
    public void onMassageSuccess() {
    }
}