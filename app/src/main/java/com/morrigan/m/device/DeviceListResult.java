package com.morrigan.m.device;

import android.support.annotation.Keep;

import com.morrigan.m.HttpResult;

import java.util.List;

/**
 * Created by y on 2016/11/1.
 */
public class DeviceListResult extends HttpResult {

    @Keep
    public List<DeviceInfo> deviceInfo;
}
