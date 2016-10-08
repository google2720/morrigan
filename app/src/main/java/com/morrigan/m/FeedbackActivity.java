package com.morrigan.m;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.os.AsyncTaskCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.github.yzeaho.common.ToastUtils;
import com.github.yzeaho.http.HttpInterface;
import com.github.yzeaho.log.Lg;

import okhttp3.FormBody;
import okhttp3.Request;

public class FeedbackActivity extends Toolbar2Activity {

    private static final String TAG = "FeedbackActivity";
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        editText = (EditText) findViewById(android.R.id.edit);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                confirmView.setEnabled(s.toString().trim().length() > 0);
            }
        });
        findViewById(R.id.panel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.showSoftInput(editText, 0);
            }
        });
    }

    protected void onClickConfirm() {
        String text = editText.getText().toString().trim();
        ConfirmTask task = new ConfirmTask(this, text);
        AsyncTaskCompat.executeParallel(task);
    }

    private class ConfirmTask extends AsyncTask<Void, Void, UiResult<String>> {

        private Activity activity;
        private String text;
        private String userId;
        private ProgressDialog dialog;

        private ConfirmTask(Activity activity, String text) {
            this.activity = activity;
            this.text = text;
            userId = UserController.getInstance().getUserId(activity);
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(activity);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected UiResult<String> doInBackground(Void... params) {
            UiResult<String> uiResult = new UiResult<>();
            try {
                String url = activity.getString(R.string.host) + "/rest/moli/feedback";
                FormBody.Builder b = new FormBody.Builder();
                b.add("userId", userId);
                b.add("content", text);
                Request.Builder builder = new Request.Builder();
                builder.url(url);
                builder.post(b.build());
                HttpInterface.Result result = HttpInterface.Factory.create().execute(builder.build());
                HttpResult r = result.parse(HttpResult.class);
                uiResult.success = r.isSuccessful();
                uiResult.message = r.retMsg;
            } catch (Exception e) {
                Lg.w(TAG, "failed to send feedback", e);
                uiResult.message = e.getMessage();
            }
            return uiResult;
        }

        @Override
        protected void onPostExecute(UiResult<String> result) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            ToastUtils.show(activity, result.message);
            if (result.success) {
                finish();
            }
        }
    }
}
