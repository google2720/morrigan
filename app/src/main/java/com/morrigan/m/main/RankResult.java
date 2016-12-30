package com.morrigan.m.main;

import android.support.annotation.Keep;

import com.morrigan.m.HttpResult;

/**
 * 星级评定
 * Created by y on 2016/10/30.
 */
public class RankResult extends HttpResult {

    @Keep
    public int rank;

    @Keep
    public int eValue;
}
