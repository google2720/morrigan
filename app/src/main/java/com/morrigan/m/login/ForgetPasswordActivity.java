package com.morrigan.m.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.os.AsyncTaskCompat;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.yzeaho.common.ToastUtils;
import com.github.yzeaho.http.HttpInterface;
import com.github.yzeaho.log.Lg;
import com.morrigan.m.BaseActivity;
import com.morrigan.m.HttpResult;
import com.morrigan.m.R;
import com.morrigan.m.UiResult;

import okhttp3.FormBody;
import okhttp3.Request;

public class ForgetPasswordActivity extends BaseActivity {

    private static final String TAG = "ForgetPasswordActivity";
    private TextView sendSmsCodeView;
    private EditText phoneView;
    private EditText smsCodeView;
    private EditText pwView;
    private SendSmsCodeTask task;
    private int time = 60;
    private static final int MSG_TIME = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TIME:
                    if (time == 0) {
                        sendSmsCodeView.setText(R.string.fetch_sms_code);
                        sendSmsCodeView.setClickable(true);
                    } else {
                        sendSmsCodeView.setText(getString(R.string.refetch_sms_code, time--));
                        sendEmptyMessageDelayed(MSG_TIME, 1000);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.complete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                complete();
            }
        });

        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneView.setText(null);
            }
        });
        sendSmsCodeView = (TextView) findViewById(R.id.sendSmsCode);
        sendSmsCodeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchSmsCode();
            }
        });
        phoneView = (EditText) findViewById(R.id.phone);
        smsCodeView = (EditText) findViewById(R.id.smsCode);
        pwView = (EditText) findViewById(R.id.pw);
        findViewById(R.id.showPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setActivated(!v.isActivated());
                if (v.isActivated()) {
                    pwView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    pwView.setSelection(pwView.getText().length());
                } else {
                    pwView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    pwView.setSelection(pwView.getText().length());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(MSG_TIME);
        if (task != null) {
            task.cancel();
        }
    }

    private void fetchSmsCode() {
        String mobile = phoneView.getText().toString().trim();
        if (TextUtils.isEmpty(mobile)) {
            ToastUtils.show(this, R.string.input_phone_hint);
            return;
        }
        if (task != null) {
            return;
        }
        task = new SendSmsCodeTask(this, mobile);
        AsyncTaskCompat.executeParallel(task);
    }

    class SendSmsCodeTask extends AsyncTask<Void, Void, UiResult> {

        private Context context;
        private String mobile;
        private HttpInterface.Result result;

        SendSmsCodeTask(Context context, String mobile) {
            this.context = context.getApplicationContext();
            this.mobile = mobile;
        }

        @Override
        protected void onPreExecute() {
            sendSmsCodeView.setText(R.string.fetching_sms_code);
            sendSmsCodeView.setClickable(false);
        }

        @Override
        protected UiResult doInBackground(Void... params) {
            UiResult uiResult = new UiResult();
            try {
                String url = context.getString(R.string.host) + "/rest/moli/send-msg";
                FormBody.Builder b = new FormBody.Builder();
                b.add("mobile", mobile);
                Request.Builder builder = new Request.Builder();
                builder.url(url);
                builder.post(b.build());
                result = HttpInterface.Factory.create().execute(builder.build());
                HttpResult r = result.parse(HttpResult.class);
                uiResult.success = r.isSuccessful();
                uiResult.message = r.retMsg;
            } catch (Exception e) {
                Lg.w(TAG, "failed to send sms code", e);
                uiResult.message = e.getMessage();
            }
            return uiResult;
        }

        @Override
        protected void onPostExecute(UiResult result) {
            task = null;
            if (result.success) {
                handler.sendEmptyMessage(MSG_TIME);
            }
            ToastUtils.show(context, result.message);
        }

        void cancel() {
            if (result != null && result.call != null) {
                result.call.cancel();
            }
            cancel(true);
        }
    }

    private void complete() {
        String mobile = phoneView.getText().toString().trim();
        if (TextUtils.isEmpty(mobile)) {
            ToastUtils.show(this, R.string.input_phone_hint);
            return;
        }
        String smsCode = smsCodeView.getText().toString().trim();
        if (TextUtils.isEmpty(smsCode)) {
            ToastUtils.show(this, R.string.input_sms_code);
            return;
        }
        String pw = pwView.getText().toString().trim();
        if (TextUtils.isEmpty(pw)) {
            ToastUtils.show(this, R.string.input_pw_hint);
            return;
        }
        CompleteTask task = new CompleteTask(this, mobile, smsCode, pw);
        AsyncTaskCompat.executeParallel(task);
    }

    class CompleteTask extends AsyncTask<Void, Void, UiResult<String>> {

        private Activity activity;
        private String mobile;
        private String smsCode;
        private String pw;
        private HttpInterface.Result result;
        private ProgressDialog dialog;

        CompleteTask(Activity activity, String mobile, String smsCode, String pw) {
            this.activity = activity;
            this.mobile = mobile;
            this.smsCode = smsCode;
            this.pw = pw;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(activity);
            dialog.setMessage(activity.getString(R.string.resetting_pw));
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected UiResult<String> doInBackground(Void... params) {
            UiResult<String> uiResult = new UiResult<>();
            try {
                String url = activity.getString(R.string.host) + "/rest/moli/forget-psw";
                FormBody.Builder b = new FormBody.Builder();
                b.add("mobile", mobile);
                b.add("msgCode", smsCode);
                b.add("newPsw", pw);
                Request.Builder builder = new Request.Builder();
                builder.url(url);
                builder.post(b.build());
                result = HttpInterface.Factory.create().execute(builder.build());
                HttpResult r = result.parse(RegisterResult.class);
                uiResult.success = r.isSuccessful();
                uiResult.message = r.retMsg;
            } catch (Exception e) {
                Lg.w(TAG, "failed to register", e);
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
