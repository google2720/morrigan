package com.morrigan.m.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.os.AsyncTaskCompat;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;

import com.github.yzeaho.common.ToastUtils;
import com.github.yzeaho.http.HttpInterface;
import com.github.yzeaho.log.Lg;
import com.morrigan.m.BaseActivity;
import com.morrigan.m.main.MainActivity;
import com.morrigan.m.R;
import com.morrigan.m.UiResult;
import com.morrigan.m.UserController;

import okhttp3.FormBody;
import okhttp3.Request;

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";
    private EditText phoneView;
    private EditText pwView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phoneView = (EditText) findViewById(R.id.phone);
        phoneView.setText(UserController.getInstance().getMobile(this));
        pwView = (EditText) findViewById(R.id.pw);
        pwView.setText(UserController.getInstance().getPassword(this));
        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneView.setText(null);
            }
        });
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
        findViewById(R.id.forgetPw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        String mobile = phoneView.getText().toString().trim();
        if (TextUtils.isEmpty(mobile)) {
            ToastUtils.show(this, R.string.input_phone_hint);
            return;
        }
        String pw = pwView.getText().toString().trim();
        if (TextUtils.isEmpty(pw)) {
            ToastUtils.show(this, R.string.input_pw_hint);
            return;
        }
        LoginTask task = new LoginTask(this, mobile, pw);
        AsyncTaskCompat.executeParallel(task);
    }

    class LoginTask extends AsyncTask<Void, Void, UiResult> {

        private Activity activity;
        private String mobile;
        private String pw;
        private HttpInterface.Result result;
        private ProgressDialog dialog;

        LoginTask(Activity activity, String mobile, String pw) {
            this.activity = activity;
            this.mobile = mobile;
            this.pw = pw;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(activity);
            dialog.setMessage(activity.getString(R.string.login_ing_message));
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected UiResult doInBackground(Void... params) {
            UiResult uiResult = new UiResult();
            try {
                String url = activity.getString(R.string.host) + "/rest/moli/login";
                FormBody.Builder b = new FormBody.Builder();
                b.add("mobile", mobile);
                b.add("password", pw);
                Request.Builder builder = new Request.Builder();
                builder.url(url);
                builder.post(b.build());
                result = HttpInterface.Factory.create().execute(builder.build());
                LoginResult r = result.parse(LoginResult.class);
                uiResult.success = r.isSuccessful();
                uiResult.message = r.retMsg;
                if (uiResult.success) {
                    UserController.getInstance().setUserInfo(activity, r.userInfo);
                }
            } catch (Exception e) {
                Lg.w(TAG, "failed to login", e);
                uiResult.message = e.getMessage();
            }
            return uiResult;
        }

        @Override
        protected void onPostExecute(UiResult result) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            ToastUtils.show(activity, result.message);
            if (result.success) {
                gotoMain();
            }
        }
    }

    private void gotoMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
