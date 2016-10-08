package com.morrigan.m.personal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.github.yzeaho.common.ToastUtils;
import com.morrigan.m.R;
import com.morrigan.m.UiResult;
import com.morrigan.m.UserController;

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

    public PersonalModifyTask(Activity activity, String col, String value, Runnable runnable) {
        this.activity = activity;
        this.col = col;
        this.value = value;
        this.runnable = runnable;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(activity);
        dialog.setMessage(activity.getString(R.string.changing));
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected UiResult doInBackground(Void... params) {
        return UserController.getInstance().modify(activity, col, value);
    }

    @Override
    protected void onPostExecute(UiResult result) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        ToastUtils.show(activity, result.message);
        if (result.success) {
            runnable.run();
        }
    }
}
