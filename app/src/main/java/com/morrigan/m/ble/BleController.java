package com.morrigan.m.ble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Size;

import com.github.yzeaho.log.Lg;
import com.morrigan.m.ble.data.Battery;
import com.morrigan.m.ble.data.BatteryResponse;
import com.morrigan.m.ble.data.BatteryResult;
import com.morrigan.m.ble.data.Bind;
import com.morrigan.m.ble.data.Data;
import com.morrigan.m.ble.data.MassageData;
import com.morrigan.m.ble.data.MassageDataResult;
import com.morrigan.m.ble.data.NotifyDataHelper;
import com.morrigan.m.device.DeviceController;

public class BleController extends AbstractBleController {

    private static final BleController sInstance = new BleController();
    private static final String BLE = "ble.main";
    private static final String BLE_ADDRESS = "ble_address";
    private BleCallback scanCallback = new SimpleBleCallback() {
        @Override
        public void onGattDisconnected(BluetoothDevice device) {
            mCallbacks.onNotifyBattery(0);
        }

        @Override
        public void onBluetoothOff() {
            mCallbacks.onNotifyBattery(0);
        }

        @Override
        public void onBindDeviceSuccess(BluetoothDevice device, boolean firstBind) {
            fetchBatteryAsync();
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

    @Override
    protected void onReceiveNotifyData(byte[] data) {
        Data d = NotifyDataHelper.parser(data);
        if (d == null) {
            Lg.w(TAG, "no support notify data?");
        } else if (d instanceof BatteryResult) {
            batteryResponseAsync();
            mCallbacks.onNotifyBattery(d.getIntValue());
        }
    }

    public void connectAndBindAsync(final String deviceName, final String address) {
        EXECUTOR_SERVICE_SINGLE.execute(new Runnable() {
            @Override
            public void run() {
                new BleBindHelper().connectAndBind(mContext, address, deviceName);
            }
        });
    }

    public boolean bindDevice(final BluetoothDevice device, final boolean sendToServer) {
        try {
            Lg.i(TAG, "start bind device " + sendToServer);
            setCharacteristicNotification(mNotifyCharacteristic, mNotifyDescriptor, true);
            Bind data = new Bind();
            writeWithNoRead(data.toValue());
            saveBindDevice(device.getAddress());
            if (sendToServer) {
                Lg.i(TAG, "send bind info to server");
                DeviceController.getInstance().bind(mContext, device.getAddress(), device.getName());
            }
            return true;
        } catch (Exception e) {
            Lg.w(TAG, "failed to bind device", e);
        }
        return false;
    }

    public void fetchBatteryAsync() {
        EXECUTOR_SERVICE_SINGLE.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Lg.i(TAG, "fetch battery start");
                    Battery data = new Battery();
                    writeWithNoRead(data.toValue());
                    mCallbacks.onFetchBatterySuccess();
                } catch (Exception e) {
                    Lg.w(TAG, "failed to connectAndBind device", e);
                    mCallbacks.onFetchBatteryFailed(BleError.SYSTEM);
                }
            }
        });
    }

    private void batteryResponseAsync() {
        EXECUTOR_SERVICE_SINGLE.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Lg.i(TAG, "battery response start");
                    BatteryResponse data = new BatteryResponse();
                    writeWithNoRead(data.toValue());
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
                for (int i = 0; i < 5; i++) {
                    MassageDataResult r = MassageDataResult.parser(write(data.toValue(), 1000));
                    if (r != null) {
                        mCallbacks.onMassageSuccess();
                        return null;
                    }
                }
                mCallbacks.onMassageFailed(BleError.TIME_OUT);
            } catch (Exception e) {
                Lg.w(TAG, "failed to massage", e);
                mCallbacks.onMassageFailed(BleError.SYSTEM);
            }
            return null;
        }
    }

    public void massageStopAsync() {
        MassageTask massageTask = new MassageTask(new MassageData(false));
        massageTask.executeOnExecutor(EXECUTOR_SERVICE_SINGLE);
    }

    public void manualMassageAsync(byte gear, byte bra) {
        MassageTask massageTask = new MassageTask(new MassageData(true, gear, bra));
        massageTask.executeOnExecutor(EXECUTOR_SERVICE_SINGLE);
    }

    public void autoMassageAsync(@Size(5) byte[] autoMode) {
        MassageTask massageTask = new MassageTask(new MassageData(true, autoMode));
        massageTask.executeOnExecutor(EXECUTOR_SERVICE_SINGLE);
    }

    public void musicMassageAsync(int decibel) {
        MassageTask massageTask = new MassageTask(new MassageData(true, decibel));
        massageTask.executeOnExecutor(EXECUTOR_SERVICE_SINGLE);
    }
}
