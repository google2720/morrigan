package com.morrigan.m.ble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.morrigan.m.UiResult;
import com.morrigan.m.ble.data.Bind;
import com.morrigan.m.device.DeviceController;

import java.util.HashMap;

public class BleController extends AbstractBleController {

    private static final BleController sInstance = new BleController();
    private static final String BLE = "ble.main";
    private static final String BLE_ADDRESS = "ble_address";
    private final HashMap<String, BluetoothDevice> deviceMap = new HashMap<>();
    private BleCallback scanCallback = new SimpleBleCallback() {

        @Override
        public void onLeScan(BluetoothDevice device) {
            if (!TextUtils.isEmpty(device.getName())) {
                synchronized (deviceMap) {
                    deviceMap.put(device.getAddress(), device);
                }
            }
        }
    };

    private BleController() {
        mCallbacks.addListener(scanCallback);
    }

    public static BleController getInstance() {
        return sInstance;
    }

    public String getBindDeviceAddress() {
        SharedPreferences shared = mContext.getSharedPreferences(BLE, Context.MODE_PRIVATE);
        return shared.getString(BLE_ADDRESS, null);
    }

    public void saveBindDevice(String address) {
        SharedPreferences shared = mContext.getSharedPreferences(BLE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(BLE_ADDRESS, address);
        editor.apply();
    }

    public void connectAndBindAsync(Context _context, final String address) {
        final Context context = _context.getApplicationContext();
        EXECUTOR_SERVICE_SINGLE.execute(new Runnable() {
            @Override
            public void run() {
                UiResult<Boolean> result = DeviceController.getInstance().checkDevice(context, address);
                if (result.t) {
                    mCallbacks.onBindDeviceFailed(BleError.BIND_BY_OTHER);
                }
                BluetoothDevice device;
                synchronized (deviceMap) {
                    device = deviceMap.get(address);
                }
                if (device == null) {
                    mCallbacks.onBindDeviceFailed(BleError.SYSTEM);
                }
                connect(device);
            }
        });
    }

    public void bindDeviceAsync(final BluetoothDevice device) {
        EXECUTOR_SERVICE_SINGLE.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean firstBind = device.getAddress().equals(getBindDeviceAddress());
                    Bind bind = new Bind();
                    write(bind.toValue());
                    saveBindDevice(device.getAddress());
                    mCallbacks.onBindDeviceSuccess(device, firstBind);
                } catch (Exception e) {
                    Log.w(TAG, "failed to bind device", e);
                    mCallbacks.onBindDeviceFailed(BleError.SYSTEM);
                }
            }
        });
    }
}
