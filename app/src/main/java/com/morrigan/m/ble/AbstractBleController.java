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
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;

import com.github.yzeaho.log.Lg;
import com.morrigan.m.BuildConfig;
import com.morrigan.m.ble.scanner.BleScanner;
import com.morrigan.m.ble.scanner.BleScannerHelper;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractBleController {

    public static final String TAG = "AbstractBleController";

    private final static UUID UUID_DATA_SERVICE = UUID.fromString("000056ff-0000-1000-8000-00805f9b34fb");
    private final static UUID UUID_DATA_CHARACTERISTIC = UUID.fromString("000033f1-0000-1000-8000-00805f9b34fb");
    private final static UUID UUID_NOTIFY_CHARACTERISTIC = UUID.fromString("000033f2-0000-1000-8000-00805f9b34fb");
    private final static UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002901-0000-1000-8000-00805f9b34fb");

    protected Context mContext;
    private static final Object mLock = new Object();
    protected final ExecutorService EXECUTOR_SERVICE_SINGLE = Executors.newSingleThreadExecutor();
    protected final Executor EXECUTOR_SERVICE_POOL = AsyncTask.THREAD_POOL_EXECUTOR;
    protected BluetoothAdapter mBleAdapter;
    private BleConnection mBleConnection;
    protected GroupBleCallback mCallbacks = new GroupBleCallback();
    private BluetoothGattService mDefaultGattService;
    protected BluetoothGattCharacteristic mDataCharacteristic;
    protected BluetoothGattCharacteristic mNotifyCharacteristic;
    protected BluetoothGattDescriptor mNotifyDescriptor;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            Lg.i(TAG, "onReceive " + action);
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
    private boolean mAutoConnect = false;
    private boolean mAutoReconnect = false;
    private boolean mDeviceReady;
    private BleScanner mScanner;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Lg.i(TAG, "onReceive " + action);
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
                Lg.i(TAG, "bluetooth state " + state);
                if (state == BluetoothAdapter.STATE_OFF) {
                    disconnect();
                    mCallbacks.onBluetoothOff();
                } else if (state == BluetoothAdapter.STATE_ON) {
                    mCallbacks.onBluetoothOn();
                }
            }
        }
    };

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
                Lg.i(TAG, "It find no gatt service");
                disconnect();
                mCallbacks.onGattServicesNoFound(mBleConnection.getDevice());
                return;
            }
            mDataCharacteristic = mDefaultGattService.getCharacteristic(UUID_DATA_CHARACTERISTIC);
            if (mDataCharacteristic == null) {
                Lg.i(TAG, "It find no characteristic");
                disconnect();
                mCallbacks.onGattServicesNoFound(mBleConnection.getDevice());
                return;
            }
            mNotifyCharacteristic = mDefaultGattService.getCharacteristic(UUID_NOTIFY_CHARACTERISTIC);
            if (mDataCharacteristic == null) {
                Lg.i(TAG, "It find no characteristic");
                disconnect();
                mCallbacks.onGattServicesNoFound(mBleConnection.getDevice());
                return;
            }
            if (BuildConfig.DEBUG) {
                List<BluetoothGattDescriptor> descriptors = mNotifyCharacteristic.getDescriptors();
                for (BluetoothGattDescriptor d : descriptors) {
                    Lg.i(TAG, "  d:" + d.getUuid() + " " + d.getPermissions());
                }
            }
            mNotifyDescriptor = mDataCharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
            mDeviceReady = true;
            mCallbacks.onGattServicesDiscovered(mBleConnection.getDevice());
        } else if (BleConnection.ACTION_DATA_AVAILABLE.equals(action)) {
            String uuid = intent.getStringExtra(BleConnection.EXTRA_UUID);
            byte[] data = intent.getByteArrayExtra(BleConnection.EXTRA_DATA);
            if (data != null && data.length > 0) {
                onReceiveNotifyData(data);
            }
        }
    }

    protected abstract void onReceiveNotifyData(byte[] data);

    public void addCallback(BleCallback cb) {
        mCallbacks.addListener(cb);
    }

    public void removeCallback(BleCallback cb) {
        mCallbacks.removeListener(cb);
    }

    public BleCallback getCallbacks() {
        return mCallbacks;
    }

    public BluetoothDevice getRemoteDevice(String address) {
        return mBleAdapter == null ? null : mBleAdapter.getRemoteDevice(address);
    }

    public void startLeScan() {
        BackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!mScanning) {
                    mScanning = true;
                    Lg.i(TAG, "startLeScan");
                    mScanner.startLeScan();
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
                    Lg.i(TAG, "stopLeScan");
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

    public BluetoothDevice getConnectDevice() {
        return mBleConnection == null ? null : mBleConnection.getDevice();
    }

    public void disconnect() {
        Lg.i(TAG, "disconnect");
        stopLeScan();
        disconnectInner();
        mBleConnection.disconnect();
    }

    protected byte[] write(byte[] data) {
        checkThread();
        checkConnectionState();
        synchronized (mLock) {
            mBleConnection.write(mDataCharacteristic, data);
            return mBleConnection.read(mNotifyCharacteristic);
        }
    }

    protected byte[] write(byte[] data, long timeout) {
        checkThread();
        checkConnectionState();
        synchronized (mLock) {
            mBleConnection.write(mDataCharacteristic, data, timeout);
            return mBleConnection.read(mNotifyCharacteristic, timeout);
        }
    }

    protected void writeWithNoRead(byte[] data) {
        checkThread();
        checkConnectionState();
        synchronized (mLock) {
            mBleConnection.write(mDataCharacteristic, data);
        }
    }

    protected void writeWithNoRead(byte[] data, long timeout) {
        checkThread();
        checkConnectionState();
        synchronized (mLock) {
            mBleConnection.write(mDataCharacteristic, data, timeout);
        }
    }

    protected byte[] read(long timeout) {
        checkThread();
        checkConnectionState();
        synchronized (mLock) {
            return mBleConnection.read(mNotifyCharacteristic, timeout);
        }
    }

    protected void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, BluetoothGattDescriptor descriptor, boolean enable) {
        checkThread();
        checkConnectionState();
        synchronized (mLock) {
            mBleConnection.setCharacteristicNotification(characteristic, descriptor, enable);
        }
    }

    protected void checkConnectionState() {
        if (!mDeviceReady) {
            throw new IllegalStateException("Bluetooth is not ready.");
        }
    }

    protected void checkThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("It must be called on the background thread.");
        }
    }

    protected void disconnectInner() {
        mDeviceReady = false;
    }
}