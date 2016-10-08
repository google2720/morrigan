package com.morrigan.m.personal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.os.AsyncTaskCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.github.yzeaho.common.ToastUtils;
import com.morrigan.m.R;
import com.morrigan.m.Toolbar2Activity;
import com.morrigan.m.UiResult;
import com.morrigan.m.UserController;

public class ModifyNickNameActivity extends Toolbar2Activity {

    private static final String TAG = "personal";
    private EditText editView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_nick_name);
        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editView.setText(null);
            }
        });
        editView = (EditText) findViewById(R.id.edit);
        editView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                confirmView.setEnabled(s.length() > 0);
            }
        });
        editView.setText(UserController.getInstance().getNickname(this));
        editView.setSelection(editView.getText().length());
    }

    @Override
    protected void onClickConfirm() {
        String nickname = editView.getText().toString().trim();
        ModifyNickNameTask task = new ModifyNickNameTask(this, nickname);
        AsyncTaskCompat.executeParallel(task);
    }

    class ModifyNickNameTask extends AsyncTask<Void, Void, UiResult> {

        private Activity activity;
        private String newNickname;
        private ProgressDialog dialog;

        ModifyNickNameTask(Activity activity, String newNickname) {
            this.activity = activity;
            this.newNickname = newNickname;
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
            return UserController.getInstance().modify(activity, "nickName", newNickname);
        }

        @Override
        protected void onPostExecute(UiResult result) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            ToastUtils.show(activity, result.message);
            if (result.success) {
                UserController.getInstance().setNickname(activity, newNickname);
                finish();
            }
        }
    }
}
