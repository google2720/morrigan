package com.morrigan.m.ble.data;

public class BindResult extends Data {

    private byte[] data;

    private BindResult(byte[] data) {
        this.data = data;
    }

    public static BindResult parser(byte[] data) {
        if (data.length == 20 && data[0] == (byte) 0xAA && data[1] == 0x55 && data[2] == (byte) 0xEE && data[3] == 0x04) {
            return new BindResult(data);
        }
        return null;
    }
}
