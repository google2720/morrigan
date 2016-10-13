package com.morrigan.m.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.morrigan.m.BuildConfig;

import java.util.List;
import java.util.UUID;

public class BleConnection {
    public static final String TAG = "BleConnection";

    public final static String ACTION_GATT_CONNECTED = "bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_CONNECTING = "bluetooth.le.ACTION_GATT_CONNECTING";
    public final static String ACTION_GATT_DISCONNECTED = "bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String ACTION_GATT_RSSI = "bluetooth.le.ACTION_GATT_RSSI";
    public final static String EXTRA_DATA = "bluetooth.le.EXTRA_DATA";
    public final static String EXTRA_UUID = "bluetooth.le.EXTRA_UUID";

    /**
     * 与蓝牙设备通讯的超时时间
     */
    private static final long TIMEOUT_MILLIS = 10000;

    private Context mContext;
    // we're doing nothing
    public static final int STATE_NONE = 0;
    // now initiating an outgoing connection
    public static final int STATE_CONNECTING = 1;
    // now connected to a remote device
    public static final int STATE_CONNECTED = 2;
    private int mState;
    private BluetoothGatt mBluetoothGatt;
    private final Object mObject = new Object();

    public BleConnection(Context context) {
        mContext = context.getApplicationContext();
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i(TAG, "onConnectionStateChange " + status + " to " + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "Connected to GATT server.");
                setState(STATE_CONNECTED);
                broadcastUpdate(ACTION_GATT_CONNECTED);
                // 连接成功，进行搜索服务
                boolean r = mBluetoothGatt.discoverServices();
                Log.i(TAG, "Attempting to start service discovery:" + r);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Disconnected from GATT server.");
                connectFailed(false);
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i(TAG, "onServicesDiscovered received " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (BuildConfig.DEBUG) {
                    List<BluetoothGattService> ss = mBluetoothGatt.getServices();
                    for (BluetoothGattService s : ss) {
                        Log.d(TAG, "s:" + s.getUuid() + " " + s.getInstanceId());
                        List<BluetoothGattCharacteristic> cs = s.getCharacteristics();
                        for (BluetoothGattCharacteristic c : cs) {
                            Log.d(TAG, " c:" + c.getUuid());
                        }
                    }
                }
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i(TAG, "onCharacteristicRead " + status + " " + characteristic.getUuid());
            synchronized (mObject) {
                mObject.notifyAll();
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.i(TAG, "onCharacteristicChanged " + characteristic.getUuid());
            broadcastUpdate(characteristic);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i(TAG, "onCharacteristicWrite " + status + " " + characteristic.getUuid());
            synchronized (mObject) {
                mObject.notifyAll();
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.i(TAG, "onDescriptorWrite " + status + " " + descriptor.getUuid());
            synchronized (mObject) {
                mObject.notifyAll();
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.i(TAG, "onDescriptorRead " + status + " " + descriptor.getUuid());
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            Log.i(TAG, "onReadRemoteRssi " + status + " " + rssi);
            synchronized (mObject) {
                mObject.notifyAll();
            }
            Intent intent = new Intent(ACTION_GATT_RSSI);
            intent.putExtra(EXTRA_DATA, rssi);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }
    };
    private BluetoothDevice mmDevice;

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, BluetoothGattDescriptor descriptor, boolean enabled) {
        if (mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        synchronized (mObject) {
            boolean r = mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
            Log.d(TAG, "setCharacteristicNotification " + r);
            byte[] data = enabled ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
            descriptor.setValue(data);
            r = mBluetoothGatt.writeDescriptor(descriptor);
            Log.d(TAG, "write descriptor " + (r ? "success " : "failed ") + "[" + toHex(data) + "]");
            try {
                mObject.wait(TIMEOUT_MILLIS);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    private void broadcastUpdate(String action) {
        Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    public void readRemoteRssi() {
        synchronized (mObject) {
            if (mBluetoothGatt == null) {
                Log.w(TAG, "BluetoothAdapter not initialized");
                return;
            }
            mBluetoothGatt.readRemoteRssi();
            try {
                mObject.wait(TIMEOUT_MILLIS);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    public byte[] read(BluetoothGattCharacteristic characteristic) {
        synchronized (mObject) {
            mBluetoothGatt.readCharacteristic(characteristic);
            try {
                mObject.wait(TIMEOUT_MILLIS);
            } catch (InterruptedException e) {
                // ignore
            }
            byte[] data = characteristic.getValue();
            Log.d("BleData", "read data [" + toHex(data) + "]");
            return data;
        }
    }

    public void write(BluetoothGattCharacteristic characteristic, byte[] data) {
        synchronized (mObject) {
            characteristic.setValue(data);
            boolean r = mBluetoothGatt.writeCharacteristic(characteristic);
            Log.d("BleData", "write data " + (r ? "success " : "failed ") + "[" + toHex(data) + "]");
            try {
                mObject.wait(TIMEOUT_MILLIS);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    public static String toHex(byte[] data) {
        if (data == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder(data.length);
        for (byte byteChar : data) {
            builder.append(String.format("%02X ", byteChar));
        }
        builder.delete(builder.length() - 1, builder.length());
        return builder.toString();
    }

    private void broadcastUpdate(BluetoothGattCharacteristic characteristic) {
        Intent intent = new Intent(ACTION_DATA_AVAILABLE);
        byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            intent.putExtra(EXTRA_DATA, data);
            Log.i("BleData", "data:[" + BleConnection.toHex(data) + "]");
        }
        intent.putExtra(EXTRA_UUID, characteristic.getUuid().toString());
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    private void connectFailed(boolean disconnect) {
        if (mBluetoothGatt != null) {
            if (disconnect) {
                mBluetoothGatt.disconnect();
            }
            mBluetoothGatt.close();
        }
        setState(STATE_NONE);
    }

    private synchronized void setState(int state) {
        Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;
    }

    public synchronized int getState() {
        return mState;
    }

    public synchronized void connect(BluetoothDevice device) {
        if (getState() == STATE_NONE) {
            Log.d(TAG, "connect to: " + device + " tid:" + Thread.currentThread().getId());
            setState(STATE_CONNECTING);
            mmDevice = device;
            broadcastUpdate(ACTION_GATT_CONNECTING);
            mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
        }
    }

    public synchronized List<BluetoothGattService> getServices() {
        if (mBluetoothGatt != null) {
            return mBluetoothGatt.getServices();
        }
        return null;
    }

    public synchronized BluetoothGattService getService(UUID uuid) {
        if (mBluetoothGatt != null) {
            return mBluetoothGatt.getService(uuid);
        }
        return null;
    }

    public synchronized void disconnect() {
        Log.i(TAG, "disconnect");
        connectFailed(true);
    }

    public BluetoothDevice getDevice() {
        return mmDevice;
    }
}
