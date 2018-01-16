package com.morrigan.m.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;

import com.github.yzeaho.common.SleepTime;
import com.github.yzeaho.common.ToastUtils;
import com.morrigan.m.BaseActivity;
import com.morrigan.m.HttpResult;
import com.morrigan.m.R;
import com.morrigan.m.UiResult;
import com.morrigan.m.c.UserController;
import com.morrigan.m.main.MainActivity;
import com.morrigan.m.utils.AppTextUtils;
import com.morrigan.m.utils.NetUtils;

public class LoginActivity extends BaseActivity {

    private static final int REQUEST_CODE_FORGET_PW = 1;
    private static final int REQUEST_CODE_REGISTER = 2;
    private EditText phoneView;
    private EditText pwView;
    private View clearPhoneView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        clearPhoneView = findViewById(R.id.clear);
        clearPhoneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneView.setText(null);
                pwView.setText(null);
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
        phoneView.setText(UserController.getInstance().getMobile(this));
        pwView = (EditText) findViewById(R.id.pw);
        pwView.setText(UserController.getInstance().getPassword(this));
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
                intent.putExtra("phone", phoneView.getText().toString());
                startActivityForResult(intent, REQUEST_CODE_FORGET_PW);
            }
        });
        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, REQUEST_CODE_REGISTER);
            }
        });
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        if (UserController.getInstance().isAutoLogin(this)) {
            login();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_REGISTER) {
            if (resultCode == RESULT_OK) {
                phoneView.setText(data.getStringExtra("phone"));
                pwView.setText(data.getStringExtra("password"));
                login();
            }
        }
    }

    private void login() {
        if (!NetUtils.isConnected(this)) {
            ToastUtils.show(this, R.string.error_no_net);
            return;
        }
        String mobile = phoneView.getText().toString().trim();
        if (TextUtils.isEmpty(mobile)) {
            ToastUtils.show(this, R.string.login_error_phone);
            return;
        }
        if (!AppTextUtils.isCellPhone(mobile)) {
            ToastUtils.show(this, R.string.login_error_phone);
            return;
        }
        String pw = pwView.getText().toString().trim();
        if (TextUtils.isEmpty(pw)) {
            ToastUtils.show(this, R.string.login_error_phone);
            return;
        }
        loginImpl(mobile, pw);
    }

    private void loginImpl(String mobile, String pw) {
        LoginTask task = new LoginTask(this, mobile, pw);
        AsyncTaskCompat.executeParallel(task);
    }

    class LoginTask extends AsyncTask<Void, Void, UiResult<LoginResult>> {

        private Activity activity;
        private String mobile;
        private String pw;
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
        protected UiResult<LoginResult> doInBackground(Void... params) {
            SleepTime sleepTime = new SleepTime();
            UiResult<LoginResult> result = UserController.getInstance().login(activity, mobile, pw);
            sleepTime.sleep(1500);
            return result;
        }

        @Override
        protected void onPostExecute(UiResult<LoginResult> result) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if (result.t != null && result.t.retCode == HttpResult.CODE_NO_REGISTER) {
                showNoRegisterDialog();
            } else {
                ToastUtils.show(activity, result.message);
                if (result.success) {
                    gotoMain();
                }
            }
        }
    }

    private void showNoRegisterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.login_error_no_register);
        builder.setNegativeButton(R.string.action_cancel, null);
        builder.setPositiveButton(R.string.action_register, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, REQUEST_CODE_REGISTER);
            }
        });
        builder.show();
    }

    private void gotoMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
