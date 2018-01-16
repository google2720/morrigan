/*
 * 文件名：NetUtil.java
 * 创建人：fei
 * 创建时间：2014-3-21
 * 版     权：
 */
package com.morrigan.m.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtils {

    /**
     * 当前设备是否已连接到网络
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
