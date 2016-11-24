package com.morrigan.m.main;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Keep;
import android.util.Log;

import com.google.gson.Gson;
import com.morrigan.m.HttpProxy;
import com.morrigan.m.HttpResult;
import com.morrigan.m.R;
import com.morrigan.m.ble.db.Massage;
import com.morrigan.m.c.UserController;

import java.util.Calendar;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Request;

public class UploadHistoryDataService extends IntentService {

    private static final String TAG = "Upload";

    public static void startAction(Context context) {
        Intent intent = new Intent(context, UploadHistoryDataService.class);
        context.startService(intent);
    }

    public UploadHistoryDataService() {
        super("UploadHistoryDataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            upload();
        } catch (Throwable e) {
            Log.w(TAG, "failed to upload data", e);
        }
    }

    public static class Data {
        @Keep
        public String userId;
        @Keep
        public String date;
        @Keep
        public String timeLong;
        @Keep
        public String goalLong;
    }

    private void upload() throws Exception {
        Log.i(TAG, "start upload massage data");
        String userId = UserController.getInstance().getUserId(this);
        String goalLong = UserController.getInstance().getTarget(this);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long todayStartTime = calendar.getTimeInMillis();
        List<Data> dataList = Massage.queryUploadData(this, userId, goalLong, todayStartTime);
        if (dataList.isEmpty()) {
            Log.i(TAG, "no data need to upload");
            return;
        }
        String json = new Gson().toJson(dataList);
        Log.i(TAG, "json: " + json);
        String url = getString(R.string.host) + "/rest/moli/upload-record-list";
        FormBody.Builder b = new FormBody.Builder();
        b.add("userId", userId);
        b.add("hlInfo", json);
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.post(b.build());
        HttpResult result = new HttpProxy().execute(this, builder.build(), HttpResult.class);
        if (result.isSuccessful()) {
            Massage.deleteUploadData(this, userId, todayStartTime);
        }
    }
}
