package com.morrigan.m.c;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.github.yzeaho.log.Lg;
import com.morrigan.m.R;
import com.morrigan.m.ble.BackgroundHandler;
import com.morrigan.m.ble.BleController;
import com.morrigan.m.ble.db.Massage;
import com.morrigan.m.device.DeviceScanActivity;
import com.morrigan.m.main.UploadHistoryDataService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * 用户信息控制器
 * Created by y on 2016/10/3.
 */
public class MassageController {

    private static final String TAG = "MassageController";

    private static MassageController sInstance = new MassageController();

    private MassageController() {
    }

    public static MassageController getInstance() {
        return sInstance;
    }

    public void test(Context context) {
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.HOUR_OF_DAY, 21);
        c1.set(Calendar.MINUTE, 4);
        c1.set(Calendar.SECOND, 0);
        c1.set(Calendar.MILLISECOND, 0);
        Calendar c2 = Calendar.getInstance();
        c2.set(Calendar.HOUR_OF_DAY, 21);
        c2.set(Calendar.MINUTE, 8);
        c2.set(Calendar.SECOND, 0);
        c2.set(Calendar.MILLISECOND, 0);
        long startTime = c1.getTimeInMillis();
        long endTime = c2.getTimeInMillis();
        save(context, "xx", startTime, endTime);
    }

    public void save(Context _context, final String address, final long startTime, final long endTime) {
        if (endTime - startTime < 60000) {
            return;
        }
        final Context context = _context.getApplicationContext();
        final String userId = UserController.getInstance().getUserId(context);
        BackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                saveImpl(context, userId, address, startTime, endTime);
            }
        });
    }

    private void saveImpl(Context context, String userId, String address, long startTime, long endTime) {
        Lg.d(TAG, "save " + startTime + " " + endTime);
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(startTime);
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(startTime);
        c2.set(Calendar.MINUTE, 59);
        c2.set(Calendar.SECOND, 59);
        c2.set(Calendar.MILLISECOND, 999);
        if (c2.getTimeInMillis() >= endTime) {
            Massage massage = new Massage();
            massage.userId = userId;
            massage.address = address;
            massage.startTime = startTime;
            massage.endTime = endTime;
            massage.hour = c2.get(Calendar.HOUR_OF_DAY);
            massage.date = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(c2.getTime());
            massage.save(context);
            UploadHistoryDataService.startAction(context);
        } else {
            Massage massage = new Massage();
            massage.userId = userId;
            massage.address = address;
            massage.startTime = startTime;
            massage.endTime = c2.getTimeInMillis();
            massage.hour = c2.get(Calendar.HOUR_OF_DAY);
            massage.date = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(c2.getTime());
            massage.save(context);
            saveImpl(context, userId, address, c2.getTimeInMillis() + 1, endTime);
        }
    }

    public void onClickConnect(final Activity activity) {
        if (BleController.getInstance().isDeviceReady()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(R.string.change_device_tip);
            builder.setNegativeButton(R.string.action_cancel, null);
            builder.setPositiveButton(R.string.action_confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    gotoDeviceScanActivity(activity);
                }
            });
            builder.show();
        } else {
            gotoDeviceScanActivity(activity);
        }
    }

    private void gotoDeviceScanActivity(Activity activity) {
        Intent intent = new Intent(activity, DeviceScanActivity.class);
        activity.startActivity(intent);
    }
}
