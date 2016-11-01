package com.morrigan.m.ble.data;

public class Bind extends Data {

    public byte[] toValue() {
        byte[] value = new byte[20];
        value[0] = (byte) 0xAA;
        value[1] = (byte) 0x55;
        value[2] = 0x04;
        value[19] = sum(value);
        return value;
    }
}
