package com.morrigan.m.personal;

import android.support.annotation.Keep;

import com.morrigan.m.HttpResult;

/**
 * 上传头像的返回结果
 * Created by y on 2016/10/6.
 */
public class UploadResult extends HttpResult {
    @Keep
    public String imgUrl;
}
