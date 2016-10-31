package com.morrigan.m.main;

import android.content.Context;

import com.github.yzeaho.log.Lg;
import com.morrigan.m.ble.BackgroundHandler;
import com.morrigan.m.ble.db.Massage;

import java.util.Calendar;

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
        BackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                Lg.d(TAG, "save " + startTime + " " + endTime);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(startTime);

                Massage massage = new Massage();
                massage.address = address;
                massage.startTime = startTime;
                massage.endTime = endTime;
                massage.save(context);
                UploadHistoryDataService.startAction(context);
            }
        });
    }
}
