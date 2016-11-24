package com.morrigan.m.historyrecord;

import android.support.annotation.Keep;

import com.morrigan.m.HttpResult;

import java.io.Serializable;
import java.util.List;

public class HistoryResult extends HttpResult implements Serializable {
    @Keep
    public List<HlInfo> hlInfo;
}
