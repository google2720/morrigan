package com.morrigan.m.device;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;

import com.github.yzeaho.http.HttpInterface;
import com.github.yzeaho.log.Lg;
import com.morrigan.m.HttpResult;
import com.morrigan.m.R;
import com.morrigan.m.UiResult;
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
    private List<DeviceInfo> deviceInfoList;

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

    public UiResult fetch(Context context) {
        UiResult uiResult = new UiResult();
        try {
            String url = context.getString(R.string.host) + "/rest/moli/get-device-list";
            FormBody.Builder b = new FormBody.Builder();
            b.add("userId", UserController.getInstance().getUserId(context));
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            builder.post(b.build());
            HttpInterface.Result result = HttpInterface.Factory.create().execute(builder.build());
            DeviceListResult r = result.parse(DeviceListResult.class);
            uiResult.success = r.isSuccessful();
            uiResult.message = r.retMsg;
            if (uiResult.success && r.deviceInfo != null) {
                saveDeviceInfoList(context, r.deviceInfo);
            }
        } catch (Exception e) {
            Lg.w(TAG, "failed to fetch bind device list", e);
            uiResult.message = e.getMessage();
        }
        return uiResult;
    }

    private void saveDeviceInfoList(Context context, List<DeviceInfo> deviceInfoList) {
        synchronized (this) {
            this.deviceInfoList = deviceInfoList;
        }
        context.getContentResolver().notifyChange(NOTIFY_URI, null);
    }

    public List<DeviceInfo> load() {
        synchronized (this) {
            if (deviceInfoList == null) {
                return null;
            }
            return new ArrayList<>(deviceInfoList);
        }
    }

    public UiResult remove(Context context, String address) {
        UiResult uiResult = new UiResult();
        try {
            String url = context.getString(R.string.host) + "/rest/moli/remove-bind";
            FormBody.Builder b = new FormBody.Builder();
            b.add("userId", UserController.getInstance().getUserId(context));
            b.add("mac", address);
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            builder.post(b.build());
            HttpInterface.Result result = HttpInterface.Factory.create().execute(builder.build());
            HttpResult r = result.parse(HttpResult.class);
            uiResult.success = r.isSuccessful();
            uiResult.message = r.retMsg;
            if (uiResult.success) {
                remove(address);
            }
        } catch (Exception e) {
            Lg.w(TAG, "failed to remove device", e);
            uiResult.message = e.getMessage();
        }
        return uiResult;
    }

    private void remove(String address) {
        synchronized (this) {
            if (deviceInfoList != null) {
                DeviceInfo deviceInfo = null;
                for (DeviceInfo d : deviceInfoList) {
                    if (d.mac.equals(address)) {
                        deviceInfo = d;
                        break;
                    }
                }
                if (deviceInfo != null) {
                    deviceInfoList.remove(deviceInfo);
                }
            }
        }
    }

    public UiResult modifyDeviceName(Context context, String address, String name) {
        UiResult uiResult = new UiResult();
        try {
            String url = context.getString(R.string.host) + "/rest/moli/edit-device-name";
            FormBody.Builder b = new FormBody.Builder();
            b.add("userId", UserController.getInstance().getUserId(context));
            b.add("mac", address);
            b.add("deviceName", name);
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            builder.post(b.build());
            HttpInterface.Result result = HttpInterface.Factory.create().execute(builder.build());
            HttpResult r = result.parse(HttpResult.class);
            uiResult.success = r.isSuccessful();
            uiResult.message = r.retMsg;
            if (uiResult.success) {
                modifyDeviceName(address, name);
            }
        } catch (Exception e) {
            Lg.w(TAG, "failed to remove device", e);
            uiResult.message = e.getMessage();
        }
        return uiResult;
    }

    private void modifyDeviceName(String address, String name) {
        synchronized (this) {
            if (deviceInfoList != null) {
                for (DeviceInfo d : deviceInfoList) {
                    if (d.mac.equals(address)) {
                        d.deviceName = name;
                        return;
                    }
                }
            }
        }
    }

    public UiResult<Boolean> check(Context context, String address) {
        UiResult<Boolean> uiResult = new UiResult<>();
        synchronized (this) {
            for (DeviceInfo d : deviceInfoList) {
                if (d.mac.equals(address)) {
                    uiResult.success = true;
                    uiResult.t = false;
                    return uiResult;
                }
            }
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
            HttpInterface.Result result = HttpInterface.Factory.create().execute(builder.build());
            DeviceBindCheckResult r = result.parse(DeviceBindCheckResult.class);
            uiResult.success = r.isSuccessful();
            uiResult.message = r.retMsg;
            uiResult.t = (r.isbinded == 1);
        } catch (Exception e) {
            Lg.w(TAG, "failed to check device", e);
            uiResult.message = e.getMessage();
        }
        return uiResult;
    }

    public UiResult<Void> bind(Context context, String address, String deviceName) {
        UiResult<Void> uiResult = new UiResult<>();
        try {
            String url = context.getString(R.string.host) + "/rest/moli/bind";
            FormBody.Builder b = new FormBody.Builder();
            b.add("userId", UserController.getInstance().getUserId(context));
            b.add("deviceName", deviceName);
            b.add("mac", address);
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            builder.post(b.build());
            HttpInterface.Result result = HttpInterface.Factory.create().execute(builder.build());
            HttpResult r = result.parse(HttpResult.class);
            uiResult.success = r.isSuccessful();
            uiResult.message = r.retMsg;
        } catch (Exception e) {
            Lg.w(TAG, "failed to check device", e);
            uiResult.message = e.getMessage();
        }
        return uiResult;
    }
}
