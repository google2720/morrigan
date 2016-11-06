package com.morrigan.m.ble.data;

/**
 * Created by y on 2016/11/6.
 */
public class BatteryResult extends Data {

    private byte[] data;

    private BatteryResult(byte[] data) {
        this.data = data;
    }

    public static BatteryResult parser(byte[] data) {
        if (data.length == 20 && data[0] == (byte) 0xAA && data[1] == 0x55 && data[2] == 0x02) {
            return new BatteryResult(data);
        }
        return null;
    }

    public int getIntValue() {
        return data[3];
    }
}
