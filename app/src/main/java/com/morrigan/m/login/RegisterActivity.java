package com.morrigan.m.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import com.github.yzeaho.common.ToastUtils;
import com.github.yzeaho.log.Lg;
import com.morrigan.m.BaseActivity;
import com.morrigan.m.HttpProxy;
import com.morrigan.m.HttpResult;
import com.morrigan.m.R;
import com.morrigan.m.UiResult;

import okhttp3.FormBody;
import okhttp3.Request;

public class RegisterActivity extends BaseActivity {

    private static final String TAG = "RegisterActivity";
    private View manView;
    private View womenView;
    private TextView sendSmsCodeView;
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
    private EditText phoneView;
    private EditText pwView;
    private EditText smsCodeView;
    private SendSmsCodeTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneView.setText(null);
            }
        });
        manView = findViewById(R.id.man);
        manView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manView.setActivated(true);
                womenView.setActivated(false);
            }
        });
        womenView = findViewById(R.id.women);
        womenView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manView.setActivated(false);
                womenView.setActivated(true);
            }
        });
        womenView.setActivated(true);
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
        private HttpProxy http = new HttpProxy();

        SendSmsCodeTask(Context context, String mobile) {
            this.context = context.getApplicationContext();
            this.mobile = mobile;
        }

        @Override
        protected void onPreExecute() {
//            sendSmsCodeView.setText(R.string.fetching_sms_code);
            sendSmsCodeView.setClickable(false);
            handler.sendEmptyMessage(MSG_TIME);
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
                HttpResult r = http.execute(context, builder.build(), HttpResult.class);
                uiResult.success = r.isSuccessful();
                uiResult.message = r.retMsg;
            } catch (Exception e) {
                Lg.w(TAG, "failed to send sms code", e);
                uiResult.message = HttpProxy.parserError(context, e);
            }
            return uiResult;
        }

        @Override
        protected void onPostExecute(UiResult result) {
            task = null;
            if (isFinishing()) {
                return;
            }
            if (result.success) {
                // handler.sendEmptyMessage(MSG_TIME);
            } else {
                handler.removeMessages(MSG_TIME);
                sendSmsCodeView.setText(R.string.fetch_sms_code);
                sendSmsCodeView.setClickable(true);
            }
            ToastUtils.show(context, result.message);
        }

        void cancel() {
            http.cancel();
            cancel(true);
        }
    }

    private void register() {
        if (!manView.isActivated() && !womenView.isActivated()) {
            ToastUtils.show(this, R.string.please_select_sex);
            return;
        }
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
        RegisterTask task = new RegisterTask(this, mobile, smsCode, pw, manView.isActivated());
        AsyncTaskCompat.executeParallel(task);
    }

    class RegisterTask extends AsyncTask<Void, Void, UiResult<String>> {

        private Activity activity;
        private String mobile;
        private String smsCode;
        private String pw;
        private boolean male;
        private ProgressDialog dialog;

        RegisterTask(Activity activity, String mobile, String smsCode, String pw, boolean male) {
            this.activity = activity;
            this.mobile = mobile;
            this.smsCode = smsCode;
            this.pw = pw;
            this.male = male;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(activity);
            dialog.setMessage(activity.getString(R.string.registering));
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected UiResult<String> doInBackground(Void... params) {
            UiResult<String> uiResult = new UiResult<>();
            try {
                String url = activity.getString(R.string.host) + "/rest/moli/regist";
                FormBody.Builder b = new FormBody.Builder();
                b.add("mobile", mobile);
                b.add("msgCode", smsCode);
                b.add("password", pw);
                b.add("sex", male ? "M" : "F");
                Request.Builder builder = new Request.Builder();
                builder.url(url);
                builder.post(b.build());
                RegisterResult r = new HttpProxy().execute(activity, builder.build(), RegisterResult.class);
                uiResult.success = r.isSuccessful();
                uiResult.message = r.retMsg;
                uiResult.t = r.userId;
            } catch (Exception e) {
                Lg.w(TAG, "failed to register", e);
                uiResult.message = HttpProxy.parserError(activity, e);
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
                Intent data = new Intent();
                data.putExtra("phone", mobile);
                data.putExtra("password", pw);
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }
}
