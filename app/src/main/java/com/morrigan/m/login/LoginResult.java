package com.morrigan.m.login;

import android.support.annotation.Keep;

import com.morrigan.m.HttpResult;

/**
 * 注册结果
 * Created by y on 2016/10/3.
 */
public class LoginResult extends HttpResult {
    @Keep
    public UserInfo userInfo;
}
