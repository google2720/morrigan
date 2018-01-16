package com.morrigan.m.c;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.morrigan.m.UiResult;

public class UploadUserInfoService extends IntentService {

    private static final String TAG = "UploadUserInfoService";

    public static void startAction(Context context) {
        Intent intent = new Intent(context, UploadUserInfoService.class);
        context.startService(intent);
    }

    public UploadUserInfoService() {
        super("UploadHistoryDataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.i(TAG, "start upload user info");
            UserController c = UserController.getInstance();
            UiResult result = c.upload(this);
            c.setModifyUserInfo(this, !result.success);
        } catch (Throwable e) {
            Log.w(TAG, "failed to upload user info", e);
        }
    }
}
