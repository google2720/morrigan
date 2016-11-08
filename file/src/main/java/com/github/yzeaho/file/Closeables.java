package com.github.yzeaho.file;

import java.io.Closeable;
import java.io.IOException;

public class Closeables {

    public static void close(Closeable c) {
        if (c != null && Closeable.class.isInstance(c)) {
            try {
                c.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }
}
