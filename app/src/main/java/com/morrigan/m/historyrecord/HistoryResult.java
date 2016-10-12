package com.morrigan.m.historyrecord;

import android.support.annotation.Keep;

import com.morrigan.m.HttpResult;
import com.morrigan.m.login.UserInfo;

import java.util.List;


public class HistoryResult extends HttpResult {
    @Keep
    public List<HlInfo> llInfo;
}
