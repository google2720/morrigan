package com.morrigan.m.ble.data;

/**
 * notify通知解释
 * Created by y on 2016/6/29.
 */
public class NotifyDataHelper {

    public static Data parser(byte[] data) {
        if (data == null) {
            return null;
        }
        Data r = BatteryResult.parser(data);
        if (r != null) {
            return r;
        }
        return MassageDataResult.parser(data);
    }
}
