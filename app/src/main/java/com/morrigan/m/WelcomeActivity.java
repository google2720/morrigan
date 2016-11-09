package com.morrigan.m;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;

import com.morrigan.m.c.UserController;
import com.morrigan.m.login.LoginActivity;
import com.morrigan.m.main.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends BaseActivity {

    private static final int REQUEST_CODE_PERMISSION = 1;
    private Handler handle = new Handler();
    private Runnable r = new Runnable() {
        @Override
        public void run() {
            if (isFinishing()) {
                return;
            }
            if (UserController.getInstance().isAutoLogin(getApplicationContext())) {
                gotoMain();
            } else {
                gotoLogin();
            }
        }
    };
    private ImageView iconView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        iconView = (ImageView) findViewById(android.R.id.icon);
        Picasso.with(this).load(R.drawable.welcome_bg).into(iconView);
        showPermission();
    }

    private void showPermission() {
        List<String> permissions = new ArrayList<>();
        // Check if we have write permission
        int p = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (p != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.RECORD_AUDIO);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissions.isEmpty()) {
            doIt();
        } else {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[permissions.size()]), REQUEST_CODE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            StringBuilder sb = new StringBuilder();
            boolean allGranted = true;
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int r = grantResults[i];
                if (r != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    if (Manifest.permission.ACCESS_COARSE_LOCATION.equals(permission)) {
                        sb.append(getString(R.string.permission_explain_location));
                    }
                    if (Manifest.permission.RECORD_AUDIO.equals(permission)) {
                        sb.append(getString(R.string.permission_explain_record));
                    }
                    if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission)) {
                        sb.append(getString(R.string.permission_explain_retad_external_storage));
                    }
                }
            }
            if (allGranted) {
                doIt();
            } else {
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                showNoGrantedDialog(sb.toString());
            }
        }
    }

    private void showNoGrantedDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.permission_explain);
        builder.setMessage(msg);
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setPositiveButton(R.string.action_resetting, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showPermission();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                finish();
            }
        });
        builder.show();
    }

    private void doIt() {
        handle.postDelayed(r, 3000);
        final Context context = getApplicationContext();
        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if (UserController.getInstance().isAutoLogin(context)) {
                    String mobile = UserController.getInstance().getMobile(context);
                    String pw = UserController.getInstance().getPassword(context);
                    UserController.getInstance().login(context, mobile, pw);
                }
                return null;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handle.removeCallbacks(r);
        iconView.setImageDrawable(null);
    }

    private void gotoMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void gotoLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
