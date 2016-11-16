package com.morrigan.m.ble;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.github.yzeaho.log.Lg;

public class BleService extends Service {

    private static final String TAG = "BleService";

    private ServiceHandler mHandler;
    private BleController mBle;
    private BleCallback mCallback = new SimpleBleCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device) {
            String address = device.getAddress();
            if (mBle.isAutoConnect() && address.equals(mBle.getBindDeviceAddress())) {
                mBle.connect(device);
            }
        }

        @Override
        public void onGattConnected(BluetoothDevice device) {
            mBle.stopLeScan();
        }

        @Override
        public void onGattDisconnected(BluetoothDevice device) {
            if (mBle.isAutoReconnect()) {
                mHandler.sendEmptyMessage(MSG_SCAN);
            }
        }

        @Override
        public void onGattServicesDiscovered(BluetoothDevice device) {
            String address = device.getAddress();
            if (mBle.isAutoConnect() && address.equals(mBle.getBindDeviceAddress())) {
                mBle.bindDeviceAsync(device, false);
            }
        }

        @Override
        public void onBindDeviceSuccess(BluetoothDevice device, boolean firstBind) {
            mBle.fetchBatteryAsync();
        }
    };

    private static final int MSG_INIT = 0;
    private static final int MSG_SCAN = 1;

    private class ServiceHandler extends Handler {

        private ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Lg.d(TAG, "handleMessage " + msg.what);
            switch (msg.what) {
                case MSG_INIT:
                    String address = mBle.getBindDeviceAddress();
                    Lg.i(TAG, "bind device address " + address);
                    if (!TextUtils.isEmpty(address)) {
                        sendEmptyMessage(MSG_SCAN);
                    }
                    break;
                case MSG_SCAN:
                    if (mBle.isEnabled()) {
                        mBle.startLeScan();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, BleService.class);
        context.startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Lg.i(TAG, "onCreate");
        mHandler = new ServiceHandler(Looper.getMainLooper());
        mBle = BleController.getInstance();
        mBle.initialize(this);
        mBle.addCallback(mCallback);
        mHandler.sendEmptyMessage(MSG_INIT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Lg.i(TAG, "onStartCommand " + startId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Lg.i(TAG, "onDestroy");
        mBle.removeCallback(mCallback);
    }
}