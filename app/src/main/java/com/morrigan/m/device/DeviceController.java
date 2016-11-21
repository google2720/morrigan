package com.morrigan.m.device;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;

import com.github.yzeaho.log.Lg;
import com.morrigan.m.HttpProxy;
import com.morrigan.m.HttpResult;
import com.morrigan.m.R;
import com.morrigan.m.UiResult;
import com.morrigan.m.ble.BleController;
import com.morrigan.m.ble.db.Device;
import com.morrigan.m.c.UserController;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Request;

/**
 * 用户信息控制器
 * Created by y on 2016/10/3.
 */
public class DeviceController {

    private static final String TAG = "DeviceController";
    public static final Uri NOTIFY_URI = Uri.parse("content://com.morrigan.m/device_list_notify");

    private static DeviceController sInstance = new DeviceController();

    private DeviceController() {
    }

    public static DeviceController getInstance() {
        return sInstance;
    }

    public void fetchAsync(Context _context) {
        final Context context = _context.getApplicationContext();
        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                fetch(context);
                return null;
            }
        });
    }

    private UiResult fetch(Context context) {
        UiResult uiResult = new UiResult();
        try {
            String userId = UserController.getInstance().getUserId(context);
            String url = context.getString(R.string.host) + "/rest/moli/get-device-list";
            FormBody.Builder b = new FormBody.Builder();
            b.add("userId", userId);
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            builder.post(b.build());
            DeviceListResult r = new HttpProxy().execute(context, builder.build(), DeviceListResult.class);
            uiResult.success = r.isSuccessful();
            uiResult.message = r.retMsg;
            if (uiResult.success) {
                ArrayList<Device> deviceList = new ArrayList<>();
                if (r.deviceInfo != null) {
                    for (DeviceInfo d : r.deviceInfo) {
                        Device device = new Device();
                        device.userId = userId;
                        device.address = d.mac;
                        device.name = d.deviceName;
                        deviceList.add(device);
                    }
                }
                Device.save(context, userId, deviceList);
                context.getContentResolver().notifyChange(NOTIFY_URI, null);
            }
        } catch (Exception e) {
            Lg.w(TAG, "failed to fetch device list", e);
            uiResult.message = HttpProxy.parserError(context, e);
        }
        return uiResult;
    }

    public List<Device> load(Context context, String userId) {
        return Device.query(context, userId);
    }

    public UiResult remove(Context context, String address) {
        UiResult uiResult = new UiResult();
        try {
            String userId = UserController.getInstance().getUserId(context);
            String url = context.getString(R.string.host) + "/rest/moli/remove-bind";
            FormBody.Builder b = new FormBody.Builder();
            b.add("userId", userId);
            b.add("mac", address);
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            builder.post(b.build());
            HttpResult r = new HttpProxy().execute(context, builder.build(), HttpResult.class);
            uiResult.success = r.isSuccessful();
            uiResult.message = r.retMsg;
            if (uiResult.success) {
                Device.remove(context, userId, address);
                if (address.equals(BleController.getInstance().getBindDeviceAddress())) {
                    BleController.getInstance().saveBindDevice(null);
                    BleController.getInstance().disconnect();
                }
            }
        } catch (Exception e) {
            Lg.w(TAG, "failed to remove device", e);
            uiResult.message = HttpProxy.parserError(context, e);
        }
        return uiResult;
    }

    public UiResult modifyDeviceName(Context context, String address, String name) {
        UiResult uiResult = new UiResult();
        try {
            String userId = UserController.getInstance().getUserId(context);
            String url = context.getString(R.string.host) + "/rest/moli/edit-device-name";
            FormBody.Builder b = new FormBody.Builder();
            b.add("userId", userId);
            b.add("mac", address);
            b.add("deviceName", name);
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            builder.post(b.build());
            HttpResult r = new HttpProxy().execute(context, builder.build(), HttpResult.class);
            uiResult.success = r.isSuccessful();
            uiResult.message = r.retMsg;
            if (uiResult.success) {
                Device.update(context, userId, address, name);
            }
        } catch (Exception e) {
            Lg.w(TAG, "failed to edit device", e);
            uiResult.message = HttpProxy.parserError(context, e);
        }
        return uiResult;
    }

    public UiResult<Boolean> check(Context context, String address) {
        UiResult<Boolean> uiResult = new UiResult<>();
        String userId = UserController.getInstance().getUserId(context);
        Device device = Device.restoreByAddress(context, userId, address);
        if (device != null) {
            uiResult.success = true;
            uiResult.t = false;
            return uiResult;
        }
        return bindCheck(context, address);
    }

    private UiResult<Boolean> bindCheck(Context context, String address) {
        UiResult<Boolean> uiResult = new UiResult<>();
        try {
            String url = context.getString(R.string.host) + "/rest/moli/bind-check";
            FormBody.Builder b = new FormBody.Builder();
            b.add("userId", UserController.getInstance().getUserId(context));
            b.add("mac", address);
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            builder.post(b.build());
            DeviceBindCheckResult r = new HttpProxy().execute(context, builder.build(), DeviceBindCheckResult.class);
            uiResult.success = r.isSuccessful();
            uiResult.message = r.retMsg;
            uiResult.t = (r.isbinded == 1);
        } catch (Exception e) {
            Lg.w(TAG, "failed to check device", e);
            uiResult.message = HttpProxy.parserError(context, e);
        }
        return uiResult;
    }

    public UiResult<Void> bind(Context context, String address, String deviceName) {
        UiResult<Void> uiResult = new UiResult<>();
        try {
            String userId = UserController.getInstance().getUserId(context);
            String url = context.getString(R.string.host) + "/rest/moli/bind";
            FormBody.Builder b = new FormBody.Builder();
            b.add("userId", userId);
            b.add("deviceName", deviceName);
            b.add("mac", address);
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            builder.post(b.build());
            HttpResult r = new HttpProxy().execute(context, builder.build(), HttpResult.class);
            uiResult.success = r.isSuccessful();
            uiResult.message = r.retMsg;
            if (uiResult.success) {
                Device.add(context, userId, address, deviceName);
            }
        } catch (Exception e) {
            Lg.w(TAG, "failed to bind device", e);
            uiResult.message = HttpProxy.parserError(context, e);
        }
        return uiResult;
    }
}
