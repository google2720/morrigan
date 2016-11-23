package com.morrigan.m.personal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;

import com.github.yzeaho.common.SleepTime;
import com.github.yzeaho.common.ToastUtils;
import com.morrigan.m.R;
import com.morrigan.m.UiResult;
import com.morrigan.m.c.UserController;

/**
 * 修改用户信息的task
 * Created by y on 2016/10/8.
 */
public class PersonalModifyTask extends AsyncTask<Void, Void, UiResult> {

    private Activity activity;
    private String col;
    private String value;
    private Runnable runnable;
    private ProgressDialog dialog;
    private Handler handler = new Handler();
    private Runnable showDialogRunnable = new Runnable() {
        @Override
        public void run() {
            if (!activity.isFinishing()) {
                dialog = new ProgressDialog(activity);
                dialog.setMessage(activity.getString(R.string.changing));
                dialog.setCancelable(false);
                dialog.show();
            }
        }
    };

    public PersonalModifyTask(Activity activity, String col, String value, Runnable runnable) {
        this.activity = activity;
        this.col = col;
        this.value = value;
        this.runnable = runnable;
    }

    @Override
    protected void onPreExecute() {
        handler.postDelayed(showDialogRunnable, 1000);
    }

    @Override
    protected UiResult doInBackground(Void... params) {
        SleepTime sleepTime = new SleepTime();
        UiResult result = UserController.getInstance().modify(activity, col, value);
        if (dialog != null) {
            sleepTime.sleep(3000);
        } else {
            handler.removeCallbacks(showDialogRunnable);
        }
        return result;
    }

    @Override
    protected void onPostExecute(UiResult result) {
        handler.removeCallbacks(showDialogRunnable);
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        ToastUtils.show(activity, result.message);
        if (result.success) {
            runnable.run();
        }
    }
}
