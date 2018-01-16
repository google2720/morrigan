package com.morrigan.m.ble.data;

public class Data {

    public byte sum(byte[] value) {
        int total = 0;
        int size = value.length - 1;
        for (int i = 0; i < size; i++) {
            total += value[i];
        }
        return (byte) (total % 256);
    }

    public int getIntValue() {
        return 0;
    }
}
