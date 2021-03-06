package com.morrigan.m.c;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

import com.github.yzeaho.log.Lg;
import com.morrigan.m.R;
import com.morrigan.m.ble.BackgroundHandler;
import com.morrigan.m.ble.BleController;
import com.morrigan.m.ble.db.Massage;
import com.morrigan.m.device.DeviceScanActivity;
import com.morrigan.m.main.UploadHistoryDataService;
import com.morrigan.m.music.MusicActivity;

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
        c1.set(Calendar.DAY_OF_MONTH, 22);
        c1.set(Calendar.HOUR_OF_DAY, 18);
        c1.set(Calendar.MINUTE, 4);
        c1.set(Calendar.SECOND, 0);
        c1.set(Calendar.MILLISECOND, 0);
        Calendar c2 = Calendar.getInstance();
        c2.set(Calendar.DAY_OF_MONTH, 22);
        c2.set(Calendar.HOUR_OF_DAY, 18);
        c2.set(Calendar.MINUTE, 5);
        c2.set(Calendar.SECOND, 2);
        c2.set(Calendar.MILLISECOND, 0);
        long startTime = c1.getTimeInMillis();
        long endTime = c2.getTimeInMillis();
        save(context, "xx", startTime, endTime);
    }

    public void save(Context _context, final String address, final long startTime, final long endTime) {
        final Context context = _context.getApplicationContext();
        final String userId = UserController.getInstance().getUserId(context);
        BackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                saveImpl(context, userId, address, startTime, endTime);
                context.getContentResolver().notifyChange(getNotifyUri(context), null);
            }
        });
    }

    private void saveImpl(Context context, String userId, String address, long startTime, long endTime) {
        Lg.d(TAG, "save " + startTime + " " + endTime);
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(startTime);
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(startTime);
        int m = c2.get(Calendar.MINUTE);
        c2.add(Calendar.MINUTE, 9 - (m % 10));
        c2.set(Calendar.SECOND, 59);
        c2.set(Calendar.MILLISECOND, 999);
        long time = c2.getTimeInMillis();
        if (time >= endTime) {
            if (endTime - startTime >= 60000) {
                Massage massage = new Massage();
                massage.userId = userId;
                massage.address = address;
                massage.startTime = startTime;
                massage.endTime = endTime;
                massage.duration = (endTime - startTime) / 60000 * 60000;
                massage.hour = c2.get(Calendar.HOUR_OF_DAY);
                massage.date = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(c2.getTime());
                massage.save(context);
                Lg.i(TAG, "save massage time " + massage.duration);
                UploadHistoryDataService.startAction(context);
            }
        } else {
            if (time - startTime >= 60000) {
                Massage massage = new Massage();
                massage.userId = userId;
                massage.address = address;
                massage.startTime = startTime;
                massage.endTime = time;
                massage.duration = (time - startTime) / 60000 * 60000;
                massage.hour = c2.get(Calendar.HOUR_OF_DAY);
                massage.date = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(c2.getTime());
                massage.save(context);
                Lg.i(TAG, "save massage time " + massage.duration);
            }
            saveImpl(context, userId, address, time + 1, endTime);
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

    public void gotoDeviceScanActivity(Activity activity) {
        if (activity instanceof MusicActivity){
            ((MusicActivity)activity).stopPlay();
        }
        BleController ble = BleController.getInstance();
        ble.setAutoConnect(false);
        ble.setAutoReconnect(false);
        ble.disconnect();
        Intent intent = new Intent(activity, DeviceScanActivity.class);
        activity.startActivity(intent);
    }

    public Uri getNotifyUri(Context context) {
        return Uri.parse("content://" + context.getPackageName() + "/massage");
    }
}
