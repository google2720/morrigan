package com.morrigan.m.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.morrigan.m.BuildConfig;
import com.morrigan.m.ble.scanner.BleScanner;
import com.morrigan.m.ble.scanner.BleScannerHelper;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BleController {

    private static final String TAG = "BleController";

    private final static UUID UUID_DATA_SERVICE = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private final static UUID UUID_DATA_CHARACTERISTIC = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    private final static UUID UUID_SOS_CHARACTERISTIC = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");
    private final static UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private static BleController sInstance = new BleController();
    private Context mContext;
    private static final Object mLock = new Object();
    private final ExecutorService EXECUTOR_SERVICE_SINGLE = Executors.newSingleThreadExecutor();
    private final Executor EXECUTOR_SERVICE_POOL = AsyncTask.THREAD_POOL_EXECUTOR;
    private BluetoothAdapter mBleAdapter;
    private BleConnection mBleConnection;
    private GroupBleCallback mCallbacks;
    private BluetoothGattService mDefaultGattService;
    private BluetoothGattCharacteristic mDataCharacteristic;
    private BluetoothGattDescriptor mDataDescriptor;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            Log.i(TAG, "onReceive " + action);
            EXECUTOR_SERVICE_POOL.execute(new Runnable() {
                @Override
                public void run() {
                    doReceiver(context, intent, action);
                }
            });
        }
    };
    private boolean mScanning;
    private boolean mInitialize;
    private boolean mAutoConnect = true;
    private boolean mAutoReconnect = true;
    private boolean mDeviceReady;
    private BleScanner mScanner;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.i(TAG, "onReceive " + action);
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
                Log.i(TAG, "bluetooth state " + state);
                if (state == BluetoothAdapter.STATE_OFF) {
                    disconnect();
                    mCallbacks.onBluetoothOff();
                } else if (state == BluetoothAdapter.STATE_ON) {
                    mCallbacks.onBluetoothOn();
                }
            }
        }
    };

    private BleController() {
        mCallbacks = new GroupBleCallback();
    }

    public static BleController getInstance() {
        return sInstance;
    }

    public void initialize(Context context) {
        if (mInitialize) {
            return;
        }
        mInitialize = true;
        mContext = context.getApplicationContext();
        mBleAdapter = BluetoothAdapter.getDefaultAdapter();
        mBleConnection = new BleConnection(mContext);
        mScanner = BleScannerHelper.create(this, mBleAdapter);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BleConnection.ACTION_GATT_CONNECTED);
        filter.addAction(BleConnection.ACTION_GATT_CONNECTING);
        filter.addAction(BleConnection.ACTION_GATT_DISCONNECTED);
        filter.addAction(BleConnection.ACTION_GATT_SERVICES_DISCOVERED);
        filter.addAction(BleConnection.ACTION_GATT_RSSI);
        filter.addAction(BleConnection.ACTION_DATA_AVAILABLE);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mReceiver, filter);
        filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mContext.registerReceiver(receiver, filter);
    }

    private void doReceiver(Context context, Intent intent, String action) {
        if (BleConnection.ACTION_GATT_CONNECTED.equals(action)) {
            mCallbacks.onGattConnected(mBleConnection.getDevice());
        } else if (BleConnection.ACTION_GATT_CONNECTING.equals(action)) {
            mCallbacks.onGattConnecting(mBleConnection.getDevice());
        } else if (BleConnection.ACTION_GATT_DISCONNECTED.equals(action)) {
            disconnectInner();
            mCallbacks.onGattDisconnected(mBleConnection.getDevice());
        } else if (BleConnection.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            mDefaultGattService = mBleConnection.getService(UUID_DATA_SERVICE);
            if (mDefaultGattService == null) {
                // TODO 设备没有该服务？
                Log.i(TAG, "It find no gatt service");
                mDeviceReady = false;
                mBleConnection.disconnect();
                return;
            }
            mDataCharacteristic = mDefaultGattService.getCharacteristic(UUID_DATA_CHARACTERISTIC);
            if (mDataCharacteristic == null) {
                // TODO 设备没有该特征？
                Log.i(TAG, "It find no characteristic");
                mDeviceReady = false;
                mBleConnection.disconnect();
                return;
            }
            if (BuildConfig.DEBUG) {
                List<BluetoothGattDescriptor> descriptors = mDataCharacteristic.getDescriptors();
                for (BluetoothGattDescriptor d : descriptors) {
                    Log.i(TAG, "  d:" + d.getUuid() + " " + d.getPermissions());
                }
            }
            mDataDescriptor = mDataCharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
            mDeviceReady = true;
            mCallbacks.onGattServicesDiscovered(mBleConnection.getDevice());
        } else if (BleConnection.ACTION_DATA_AVAILABLE.equals(action)) {
            String uuid = intent.getStringExtra(BleConnection.EXTRA_UUID);
            byte[] data = intent.getByteArrayExtra(BleConnection.EXTRA_DATA);
            if (data != null && data.length > 0) {
            }
        }
    }

    public void addCallback(BleCallback cb) {
        mCallbacks.addListener(cb);
    }

    public void removeCallback(BleCallback cb) {
        mCallbacks.removeListener(cb);
    }

    public BleCallback getCallbacks() {
        return mCallbacks;
    }

    public void startLeScan() {
        BackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!mScanning) {
                    mScanning = true;
                    Log.i(TAG, "startLeScan");
                    mScanner.startLeScan();
                    Log.i(TAG, "startLeScan2");
                }
            }
        });
    }

    public void stopLeScan() {
        BackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mScanning) {
                    mScanning = false;
                    Log.i(TAG, "stopLeScan");
                    mScanner.stopLeScan();
                }
            }
        });
    }

    public boolean isScanning() {
        return mScanning;
    }

    public boolean isEnabled() {
        return mBleAdapter != null && mBleAdapter.isEnabled();
    }

    public boolean isDeviceReady() {
        return mDeviceReady;
    }

    public boolean isAutoConnect() {
        return mAutoConnect;
    }

    public void setAutoConnect(boolean auto) {
        mAutoConnect = auto;
    }

    public boolean isAutoReconnect() {
        return mAutoReconnect;
    }

    public void setAutoReconnect(boolean auto) {
        mAutoReconnect = auto;
    }

    public void connect(BluetoothDevice device) {
        mBleConnection.connect(device);
    }

    public void disconnect() {
        Log.i(TAG, "disconnect");
        disconnectInner();
        if (mBleConnection != null) {
            mBleConnection.disconnect();
        }
    }

    private byte[] write(byte[] data) {
        checkThread();
        checkConnectionState();
        synchronized (mLock) {
            mBleConnection.write(mDataCharacteristic, data);
            return mBleConnection.read(mDataCharacteristic);
        }
    }

    private void write2(byte[] data) {
        checkThread();
        checkConnectionState();
        synchronized (mLock) {
            mBleConnection.write(mDataCharacteristic, data);
        }
    }

    private void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, BluetoothGattDescriptor descriptor, boolean enable) {
        checkThread();
        checkConnectionState();
        synchronized (mLock) {
            mBleConnection.setCharacteristicNotification(characteristic, descriptor, enable);
        }
    }

    private void checkConnectionState() {
        if (!mDeviceReady) {
            throw new IllegalStateException("Bluetooth is not ready.");
        }
    }

    private void checkThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("It must be called on the background thread.");
        }
    }

    private void disconnectInner() {
        mDeviceReady = false;
        stopLeScan();
    }
}