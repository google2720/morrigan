package com.morrigan.m.ble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.Size;
import android.text.TextUtils;

import com.github.yzeaho.log.Lg;
import com.morrigan.m.UiResult;
import com.morrigan.m.ble.data.Battery;
import com.morrigan.m.ble.data.BatteryResponse;
import com.morrigan.m.ble.data.BatteryResult;
import com.morrigan.m.ble.data.Bind;
import com.morrigan.m.ble.data.Data;
import com.morrigan.m.ble.data.MassageData;
import com.morrigan.m.ble.data.MassageDataResult;
import com.morrigan.m.ble.data.NotifyDataHelper;
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
    private MassageTask massageTask;

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

    @Override
    protected void onReceiveNotifyData(byte[] data) {
        Data d = NotifyDataHelper.parser(data);
        if (d == null) {
            Lg.i(TAG, "no support notify data?");
        } else if (d instanceof BatteryResult) {
            batteryResponseAsync();
            mCallbacks.onFetchBatterySuccess(d.getIntValue());
        } else if (d instanceof MassageDataResult) {
            cancelTask(massageTask);
            mCallbacks.onMassageSuccess();
        }
    }

    public void connectAndBindAsync(final String deviceName, final String address) {
        EXECUTOR_SERVICE_SINGLE.execute(new Runnable() {
            @Override
            public void run() {
                Lg.i(TAG, "connectAndBindAsync " + deviceName + " " + address);
                UiResult<Boolean> result = DeviceController.getInstance().check(mContext, address);
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

    public void bindDeviceAsync(final BluetoothDevice device, final boolean sendToServer) {
        EXECUTOR_SERVICE_SINGLE.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Lg.i(TAG, "bind device start");
                    setCharacteristicNotification(mNotifyCharacteristic, mNotifyDescriptor, true);
                    boolean firstBind = device.getAddress().equals(getBindDeviceAddress());
                    Bind data = new Bind();
                    write2(data.toValue());
                    saveBindDevice(device.getAddress());
                    if (sendToServer) {
                        DeviceController.getInstance().bind(mContext, device.getAddress(), device.getName());
                    }
                    mCallbacks.onBindDeviceSuccess(device, firstBind);
                } catch (Exception e) {
                    Lg.w(TAG, "failed to bind device", e);
                    mCallbacks.onBindDeviceFailed(BleError.SYSTEM);
                }
            }
        });
    }

    public void fetchBatteryAsync() {
        EXECUTOR_SERVICE_SINGLE.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Lg.i(TAG, "fetch battery start");
                    Battery data = new Battery();
                    write2(data.toValue());
                } catch (Exception e) {
                    Lg.w(TAG, "failed to bind device", e);
                    mCallbacks.onFetchBatteryFailed(BleError.SYSTEM);
                }
            }
        });
    }

    public void batteryResponseAsync() {
        EXECUTOR_SERVICE_SINGLE.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Lg.i(TAG, "battery response start");
                    BatteryResponse data = new BatteryResponse();
                    write2(data.toValue());
                } catch (Exception e) {
                    Lg.w(TAG, "failed to battery response", e);
                }
            }
        });
    }

    private class MassageTask extends AsyncTask<Void, Void, Void> {

        private MassageData data;

        private MassageTask(MassageData data) {
            this.data = data;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Lg.i(TAG, "massage start");
                for (int i = 0; !isCancelled() && i < 5; i++) {
                    write2(data.toValue());
                    SystemClock.sleep(1000);
                }
            } catch (Exception e) {
                Lg.w(TAG, "failed to massage", e);
                mCallbacks.onMassageFailed(BleError.SYSTEM);
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            massageTask = null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            massageTask = null;
        }
    }

    private void cancelTask(AsyncTask<?, ?, ?> task) {
        if (task != null) {
            task.cancel(true);
        }
    }

    public void manualAsync(byte gear, byte bra) {
        cancelTask(massageTask);
        massageTask = new MassageTask(new MassageData(true, gear, bra));
        massageTask.executeOnExecutor(EXECUTOR_SERVICE_SINGLE);
    }

    public void manualStopAsync() {
        cancelTask(massageTask);
        massageTask = new MassageTask(new MassageData(false));
        massageTask.executeOnExecutor(EXECUTOR_SERVICE_SINGLE);
    }

    public void autoMassageAsync(@Size(5) byte[] autoMode) {
        cancelTask(massageTask);
        massageTask = new MassageTask(new MassageData(true, autoMode));
        massageTask.executeOnExecutor(EXECUTOR_SERVICE_SINGLE);
    }
}
