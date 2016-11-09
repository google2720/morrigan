package com.morrigan.m.c;

import android.content.Context;

import com.github.yzeaho.log.Lg;
import com.morrigan.m.ble.BackgroundHandler;
import com.morrigan.m.ble.db.Massage;
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

    public void save(Context _context, final String address, final long startTime, final long endTime) {
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
}
