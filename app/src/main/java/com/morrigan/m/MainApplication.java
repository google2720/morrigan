package com.morrigan.m;

import android.app.Application;

import com.morrigan.m.ble.BleController;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BleController.getInstance().initialize(this);
    }
}