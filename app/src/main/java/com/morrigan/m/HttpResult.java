package com.morrigan.m;

import android.support.annotation.Keep;

/**
 * 网络请求返回结果对象
 * Created by y on 2016/10/3.
 */
public class HttpResult {

    public static final int CODE_SUCCESS = 0;
    public static final int CODE_NO_REGISTER = 2;

    @Keep
    public int retCode;
    @Keep
    public String retMsg;

    public boolean isSuccessful() {
        return retCode == CODE_SUCCESS;
    }
}
