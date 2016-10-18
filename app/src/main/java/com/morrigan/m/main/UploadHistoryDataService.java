package com.morrigan.m.main;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.github.yzeaho.http.HttpInterface;
import com.google.gson.Gson;
import com.morrigan.m.HttpResult;
import com.morrigan.m.R;
import com.morrigan.m.UserController;

import java.util.ArrayList;
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

    static class Data {
        String userId;
        String date;
        String timeLong;
        String goalLong;
    }

    private void upload() throws Exception {
        String url = getString(R.string.host) + "/rest/moli/upload-record-list";
        String userId = UserController.getInstance().getUserId(this);
        String goalLong = UserController.getInstance().getTarget(this);
        FormBody.Builder b = new FormBody.Builder();
        b.add("userId", userId);
        b.add("hlInfo", new Gson().toJson(queryData(this, userId, goalLong)));
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.post(b.build());
        HttpInterface.Result result = HttpInterface.Factory.create().execute(builder.build());
        result.parse(HttpResult.class);
    }

    private List<Data> queryData(Context context, String userId, String goalLong) {
        List<Data> result = new ArrayList<>();
        Data data = new Data();
        data.userId = userId;
        data.date = "2016-10-17";
        data.goalLong = goalLong;
        data.timeLong = "120";
        result.add(data);
        return result;
    }
}
