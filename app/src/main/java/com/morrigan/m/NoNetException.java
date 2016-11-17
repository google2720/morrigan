package com.morrigan.m;

import java.io.IOException;

/**
 * 无网络异常
 * Created by y on 2016/5/3.
 */
public class NoNetException extends IOException {

    public NoNetException(String s) {
        super(s);
    }
}
