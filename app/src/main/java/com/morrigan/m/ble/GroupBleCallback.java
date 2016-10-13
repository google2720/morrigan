package com.morrigan.m.ble;

import android.bluetooth.BluetoothDevice;

import java.util.concurrent.CopyOnWriteArraySet;

public class GroupBleCallback implements BleCallback {

    private CopyOnWriteArraySet<BleCallback> listeners = new CopyOnWriteArraySet<>();

    public synchronized void addListener(BleCallback listener) {
        listeners.add(listener);
    }

    public synchronized void removeListener(BleCallback listener) {
        listeners.remove(listener);
    }

    public synchronized boolean isActiveListener(BleCallback listener) {
        return listeners.contains(listener);
    }

    @Override
    public void onGattDisconnected(BluetoothDevice device) {
        for (BleCallback l : listeners) {
            l.onGattDisconnected(device);
        }
    }

    @Override
    public void onGattConnected(BluetoothDevice device) {
        for (BleCallback l : listeners) {
            l.onGattConnected(device);
        }
    }

    @Override
    public void onGattServicesDiscovered(BluetoothDevice device) {
        for (BleCallback l : listeners) {
            l.onGattServicesDiscovered(device);
        }
    }

    @Override
    public void onLeScan(BluetoothDevice device) {
        for (BleCallback l : listeners) {
            l.onLeScan(device);
        }
    }

    @Override
    public void onLeScanFailed(int error) {
        for (BleCallback l : listeners) {
            l.onLeScanFailed(error);
        }
    }

    @Override
    public void onGattConnecting(BluetoothDevice device) {
        for (BleCallback l : listeners) {
            l.onGattConnecting(device);
        }
    }

    @Override
    public void onBindDeviceFailed(BluetoothDevice device) {
        for (BleCallback l : listeners) {
            l.onBindDeviceFailed(device);
        }
    }

    @Override
    public void onBindDeviceSuccess(BluetoothDevice device, boolean firstBinded) {
        for (BleCallback l : listeners) {
            l.onBindDeviceSuccess(device, firstBinded);
        }
    }

    @Override
    public void onBluetoothOff() {
        for (BleCallback l : listeners) {
            l.onBluetoothOff();
        }
    }

    @Override
    public void onBluetoothOn() {
        for (BleCallback l : listeners) {
            l.onBluetoothOn();
        }
    }
}