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

import com.github.yzeaho.log.Lg;
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
            Lg.i(TAG, "onConnectionStateChange " + status + " to " + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Lg.i(TAG, "Connected to GATT server.");
                setState(STATE_CONNECTED);
                broadcastUpdate(ACTION_GATT_CONNECTED);
                // 连接成功，进行搜索服务
                boolean r = mBluetoothGatt.discoverServices();
                Lg.i(TAG, "Attempting to start service discovery:" + r);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Lg.i(TAG, "Disconnected from GATT server.");
                connectFailed(false);
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Lg.i(TAG, "onServicesDiscovered received " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (BuildConfig.DEBUG) {
                    List<BluetoothGattService> ss = mBluetoothGatt.getServices();
                    for (BluetoothGattService s : ss) {
                        Lg.d(TAG, "s:" + s.getUuid() + " " + s.getInstanceId());
                        List<BluetoothGattCharacteristic> cs = s.getCharacteristics();
                        for (BluetoothGattCharacteristic c : cs) {
                            Lg.d(TAG, " c:" + c.getUuid());
                        }
                    }
                }
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Lg.i(TAG, "onCharacteristicRead " + status + " " + characteristic.getUuid());
            synchronized (mObject) {
                mObject.notifyAll();
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Lg.i(TAG, "onCharacteristicChanged " + characteristic.getUuid());
            broadcastUpdate(characteristic);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Lg.i(TAG, "onCharacteristicWrite " + status + " " + characteristic.getUuid());
            synchronized (mObject) {
                mObject.notifyAll();
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Lg.i(TAG, "onDescriptorWrite " + status + " " + descriptor.getUuid());
            synchronized (mObject) {
                mObject.notifyAll();
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Lg.i(TAG, "onDescriptorRead " + status + " " + descriptor.getUuid());
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            Lg.i(TAG, "onReadRemoteRssi " + status + " " + rssi);
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
        synchronized (mObject) {
            if (mBluetoothGatt == null) {
                Lg.w(TAG, "BluetoothAdapter not initialized");
                return;
            }
            boolean r = mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
            Lg.d(TAG, "setCharacteristicNotification " + r);
            byte[] data = enabled ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
            descriptor.setValue(data);
            r = mBluetoothGatt.writeDescriptor(descriptor);
            Lg.d(TAG, "write descriptor " + (r ? "success " : "failed ") + "[" + toHex(data) + "]");
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
                Lg.w(TAG, "BluetoothAdapter not initialized");
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
        return read(characteristic, TIMEOUT_MILLIS);
    }

    public byte[] read(BluetoothGattCharacteristic characteristic, long timeout) {
        synchronized (mObject) {
            if (mBluetoothGatt == null) {
                Lg.w(TAG, "BluetoothAdapter not initialized");
                return null;
            }
            mBluetoothGatt.readCharacteristic(characteristic);
            try {
                mObject.wait(timeout);
            } catch (InterruptedException e) {
                // ignore
            }
            byte[] data = characteristic.getValue();
            Lg.d("BleData", "read data [" + toHex(data) + "]");
            return data;
        }
    }

    public void write(BluetoothGattCharacteristic characteristic, byte[] data) {
        write(characteristic, data, TIMEOUT_MILLIS);
    }

    public void write(BluetoothGattCharacteristic characteristic, byte[] data, long timeout) {
        synchronized (mObject) {
            if (mBluetoothGatt == null) {
                Lg.w(TAG, "BluetoothAdapter not initialized");
                return;
            }
            characteristic.setValue(data);
            boolean r = mBluetoothGatt.writeCharacteristic(characteristic);
            Lg.d("BleData", "write data " + (r ? "success " : "failed ") + "[" + toHex(data) + "]");
            try {
                mObject.wait(timeout);
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
            Lg.i("BleData", "notify data:[" + BleConnection.toHex(data) + "]");
        }
        intent.putExtra(EXTRA_UUID, characteristic.getUuid().toString());
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    private synchronized void connectFailed(boolean disconnect) {
        if (mBluetoothGatt != null) {
            if (disconnect) {
                mBluetoothGatt.disconnect();
            } else {
                mBluetoothGatt.close();
                mBluetoothGatt = null;
            }
        }
        setState(STATE_NONE);
    }

    private synchronized void setState(int state) {
        Lg.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;
    }

    public synchronized int getState() {
        return mState;
    }

    public synchronized void connect(BluetoothDevice device) {
        if (getState() == STATE_NONE) {
            Lg.d(TAG, "connect to: " + device + " tid:" + Thread.currentThread().getId());
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
        Lg.i(TAG, "disconnect");
        connectFailed(true);
    }

    public BluetoothDevice getDevice() {
        return mmDevice;
    }
}
