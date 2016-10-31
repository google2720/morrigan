package com.morrigan.m.ble.scanner;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Build;
import android.util.Log;

import com.morrigan.m.ble.AbstractBleController;

/**
 * android5.0的蓝牙扫描器
 * Created by y on 2016/6/28.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BleScannerLollipop implements BleScanner {

    public static final String TAG = "BleScannerLollipop";
    private AbstractBleController mBle;
    private BluetoothAdapter mBleAdapter;
    private ScanCallback mScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i(TAG, "onLeScan " + result.getDevice().getName() + " " + result.getDevice().getAddress() + " " + result.getRssi());
            mBle.getCallbacks().onLeScan(result.getDevice());
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.w(TAG, "onScanFailed:" + errorCode);
            mBle.getCallbacks().onLeScanFailed(errorCode);
        }
    };

    public BleScannerLollipop(AbstractBleController bleController, BluetoothAdapter bleAdapter) {
        this.mBle = bleController;
        this.mBleAdapter = bleAdapter;
    }

    @Override
    public void startLeScan() {
        BluetoothLeScanner scanner = mBleAdapter.getBluetoothLeScanner();
        if (scanner != null) {
            scanner.startScan(mScanCallback);
        }
    }

    @Override
    public void stopLeScan() {
        BluetoothLeScanner scanner = mBleAdapter.getBluetoothLeScanner();
        if (scanner != null) {
            scanner.stopScan(mScanCallback);
        }
    }
}
