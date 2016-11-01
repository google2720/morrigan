package com.morrigan.m;

import android.app.Application;

import com.morrigan.m.ble.BleController;
import com.morrigan.m.ble.BleService;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallback());
        BleController.getInstance().initialize(this);
        BleService.actionStart(this);
    }
}
