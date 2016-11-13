package com.morrigan.m;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;

import com.github.yzeaho.common.ToastUtils;

/**
 * activity的基类
 * Created by y on 2016/10/2.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private long firstPressTime;

    public void exitApp() {
        long time = SystemClock.elapsedRealtime();
        long spent = time - firstPressTime;
        if (spent < 2000) {
            super.onBackPressed();
        } else {
            firstPressTime = time;
            ToastUtils.show(this, "再按一次退出应用");
        }
    }
}
