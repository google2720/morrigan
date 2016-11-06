package com.morrigan.m.ble.data;

import android.support.annotation.Size;

/**
 * Created by y on 2016/11/6.
 */
public class MassageData extends Data {

    private boolean s;
    private byte mode;
    private byte gear;
    private byte bra;
    private byte[] autoMode;
    private byte decibel;

    public MassageData(boolean s, byte gear, byte bra) {
        this.s = s;
        this.mode = 0x01;
        this.gear = gear;
        this.bra = bra;
    }

    public MassageData(boolean s, @Size(5) byte[] autoMode) {
        this.s = s;
        this.mode = 0x02;
        this.autoMode = autoMode;
    }

    public MassageData(boolean s, byte decibel) {
        this.s = s;
        this.mode = 0x03;
        this.decibel = decibel;
    }

    public MassageData(boolean s) {
        this.s = s;
    }

    public byte[] toValue() {
        byte[] value = new byte[20];
        value[0] = (byte) 0xAA;
        value[1] = (byte) 0x55;
        value[2] = 0x01;
        value[3] = (byte) (s ? 0x01 : 0x00);
        value[4] = mode;
        value[5] = gear;
        value[6] = bra;
        if (autoMode != null) {
            value[7] = autoMode[0];
            value[8] = autoMode[1];
            value[9] = autoMode[2];
            value[10] = autoMode[3];
            value[11] = autoMode[4];
        }
        value[12] = decibel;
        value[19] = sum(value);
        return value;
    }
}