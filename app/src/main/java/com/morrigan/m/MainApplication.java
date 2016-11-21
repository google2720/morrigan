package com.morrigan.m;

import android.app.Application;

import com.github.yzeaho.log.AndroidLgImpl;
import com.github.yzeaho.log.Lg;
import com.morrigan.m.ble.BleController;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Lg.setLg(new AndroidLgImpl(this));
        Lg.setLevel(BuildConfig.LOG_LEVEL);
        BleController.getInstance().initialize(this);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallback());
        // BleService.actionStart(this);
    }
}
