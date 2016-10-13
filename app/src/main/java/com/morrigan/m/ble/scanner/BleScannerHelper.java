package com.morrigan.m.ble.scanner;

import android.bluetooth.BluetoothAdapter;
import android.os.Build;

import com.morrigan.m.ble.BleController;

/**
 * 蓝牙扫描器
 * Created by y on 2016/6/28.
 */
public class BleScannerHelper {

    public static BleScanner create(BleController bleController, BluetoothAdapter bleAdapter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new BleScannerLollipop(bleController, bleAdapter);
        } else {
            return new BleScannerKitkat(bleController, bleAdapter);
        }
    }
}
