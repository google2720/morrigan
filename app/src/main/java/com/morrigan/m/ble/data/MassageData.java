package com.morrigan.m.ble.data;

import android.support.annotation.Size;

/**
 * 与硬件通讯的按摩消息
 * Created by y on 2016/11/6.
 */
public class MassageData extends Data {

    private boolean s;
    private byte mode;
    private byte gear;
    private byte bra;
    private byte[] autoMode;
    private byte autoSingleMode = -1;
    private byte decibel1;
    private byte decibel2;
    private byte decibel3;
    private byte decibel4;
    private byte decibel5;

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

    public MassageData(boolean s, byte autoSingleMode) {
        this.s = s;
        this.mode = 0x02;
        this.autoSingleMode = autoSingleMode;
    }

    public MassageData(boolean s, byte decibel1, byte decibel2, byte decibel3, byte decibel4, byte decibel5) {
        this.s = s;
        this.mode = 0x03;
        this.decibel1 = decibel1;
        this.decibel2 = decibel2;
        this.decibel3 = decibel3;
        this.decibel4 = decibel4;
        this.decibel5 = decibel5;
    }

    public MassageData(boolean s) {
        this.s = s;
    }

    public void setMode(byte mode) {
        this.mode = mode;
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
        } else if (autoSingleMode != -1) {
            value[7] = autoSingleMode;
        }
        value[12] = decibel1;
        value[13] = decibel2;
        value[14] = decibel3;
        value[15] = decibel4;
        value[16] = decibel5;
        value[19] = sum(value);
        return value;
    }
}
