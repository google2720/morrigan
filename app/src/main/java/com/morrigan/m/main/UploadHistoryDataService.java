package com.morrigan.m.main;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Keep;
import android.util.Log;

import com.github.yzeaho.http.HttpInterface;
import com.google.gson.Gson;
import com.morrigan.m.HttpResult;
import com.morrigan.m.R;
import com.morrigan.m.ble.db.Massage;
import com.morrigan.m.c.UserController;

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
        String userId = UserController.getInstance().getUserId(this);
        String goalLong = UserController.getInstance().getTarget(this);
        List<Data> dataList = queryData(this, userId, goalLong);
        if (dataList.isEmpty()) {
            return;
        }
        String url = getString(R.string.host) + "/rest/moli/upload-record-list";
        FormBody.Builder b = new FormBody.Builder();
        b.add("userId", userId);
        b.add("hlInfo", new Gson().toJson(dataList));
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.post(b.build());
        HttpInterface.Result result = HttpInterface.Factory.create().execute(builder.build());
        result.parse(HttpResult.class);
    }

    private List<Data> queryData(Context context, String userId, String goalLong) {
        return Massage.queryUploadData(context, userId, goalLong);
    }
}
