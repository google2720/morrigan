package com.morrigan.m.ble;

public class BleController extends AbstractBleController {

    private static BleController sInstance = new BleController();

    private BleController() {
    }

    public static BleController getInstance() {
        return sInstance;
    }
}
