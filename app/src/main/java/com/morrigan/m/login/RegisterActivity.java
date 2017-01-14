package com.morrigan.m.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.morrigan.m.c.UserController;
import com.morrigan.m.utils.AppTextUtils;
import com.morrigan.m.utils.NetUtils;

public class RegisterActivity extends BaseActivity {

    private static final String TAG = "RegisterActivity";
    private static final int DEFAULT_TIME = 60;
    private View manView;
    private View womenView;
    private TextView sendSmsCodeView;
    private int time = DEFAULT_TIME;
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
    private View clearPhoneView;

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
        clearPhoneView = findViewById(R.id.clear);
        clearPhoneView.setOnClickListener(new View.OnClickListener() {
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
        phoneView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                clearPhoneView.setVisibility(s.length() > 0 ? View.VISIBLE : View.INVISIBLE);
            }
        });
        smsCodeView = (EditText) findViewById(R.id.smsCode);
        pwView = (EditText) findViewById(R.id.pw);
        View showPasswordView = findViewById(R.id.showPassword);
        showPasswordView.setOnClickListener(new View.OnClickListener() {
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
        showPasswordView.setActivated(true);
        pwView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
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
        if (!AppTextUtils.isCellPhone(mobile)) {
            ToastUtils.show(this, R.string.login_error_phone_format);
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
            time = DEFAULT_TIME;
            handler.sendEmptyMessage(MSG_TIME);
        }

        @Override
        protected UiResult doInBackground(Void... params) {
            UiResult uiResult = new UiResult();
            try {
                UserController c = UserController.getInstance();
                if (c.checkRegister(context, mobile)) {
                    publishProgress();
                } else {
                    HttpResult r = c.sendSmsCode(context, mobile);
                    uiResult.success = r.isSuccessful();
                    uiResult.message = r.retMsg;
                }
            } catch (Exception e) {
                Lg.w(TAG, "failed to send sms code", e);
                uiResult.message = HttpProxy.parserError(context, e);
            }
            return uiResult;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            showRegisteredDialog();
        }

        @Override
        protected void onCancelled() {
            task = null;
        }

        @Override
        protected void onPostExecute(UiResult result) {
            task = null;
            if (isFinishing()) {
                return;
            }
            if (!result.success) {
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

    private void showRegisteredDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.login_error_register);
        builder.setPositiveButton(R.string.action_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    private void showSmsCodeExpireDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.login_error_sms_code_expire);
        builder.setNegativeButton(R.string.action_cancel, null);
        builder.setPositiveButton(R.string.action_fetch, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fetchSmsCode();
            }
        });
        builder.show();
    }

    private void register() {
        if (!NetUtils.isConnected(this)) {
            ToastUtils.show(this, R.string.error_no_net);
            return;
        }
        if (!manView.isActivated() && !womenView.isActivated()) {
            ToastUtils.show(this, R.string.please_select_sex);
            return;
        }
        String mobile = phoneView.getText().toString().trim();
        if (TextUtils.isEmpty(mobile)) {
            ToastUtils.show(this, R.string.input_phone_hint);
            return;
        }
        if (!AppTextUtils.isCellPhone(mobile)) {
            ToastUtils.show(this, R.string.login_error_phone_format);
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

    class RegisterTask extends AsyncTask<Void, Boolean, UiResult<String>> {

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
                UserController c = UserController.getInstance();
                if (c.checkRegister(activity, mobile)) {
                    uiResult.success = false;
                    publishProgress(true);
                } else {
                    RegisterResult r = c.register(activity, mobile, smsCode, pw, male);
                    if (r.retCode == HttpResult.CODE_SMS_CODE_EXPIRE) {
                        uiResult.success = false;
                        publishProgress(false);
                    } else {
                        uiResult.success = r.isSuccessful();
                        uiResult.message = r.retMsg;
                        uiResult.t = r.userId;
                    }
                }
            } catch (Exception e) {
                Lg.w(TAG, "failed to register", e);
                uiResult.message = HttpProxy.parserError(activity, e);
            }
            return uiResult;
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            if (values[0]) {
                showRegisteredDialog();
            } else {
                showSmsCodeExpireDialog();
            }
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
