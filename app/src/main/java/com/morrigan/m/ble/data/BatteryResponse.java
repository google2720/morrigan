package com.morrigan.m.ble.data;

/**
 * Created by y on 2016/11/6.
 */
public class BatteryResponse extends Data {

    public byte[] toValue() {
        byte[] value = new byte[20];
        value[0] = (byte) 0xAA;
        value[1] = (byte) 0x55;
        value[2] = (byte) 0xEE;
        value[3] = 0x02;
        value[19] = sum(value);
        return value;
    }
}
