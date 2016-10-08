package com.morrigan.m;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.morrigan.m.login.LoginActivity;
import com.morrigan.m.main.MainActivity;
import com.squareup.picasso.Picasso;

public class WelcomeActivity extends BaseActivity {

    private Handler handle = new Handler();
    private Runnable r = new Runnable() {
        @Override
        public void run() {
            if (!isFinishing()) {
//                gotoMain();
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
        handle.postDelayed(r, 3000);
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
