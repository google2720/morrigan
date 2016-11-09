package com.morrigan.m.ble.data;

/**
 * Created by y on 2016/11/6.
 */
public class MassageDataResult extends Data {

    private byte[] data;

    private MassageDataResult(byte[] data) {
        this.data = data;
    }

    public static MassageDataResult parser(byte[] data) {
        if (data.length == 20 && data[0] == (byte) 0xAA && data[1] == 0x55 && data[2] == (byte) 0xEE && data[3] == 0x01) {
            return new MassageDataResult(data);
        }
        return null;
    }
}
