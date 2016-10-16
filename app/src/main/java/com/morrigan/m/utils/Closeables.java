package com.morrigan.m.utils;

import java.io.Closeable;
import java.io.IOException;

public class Closeables {

    public static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }
}
