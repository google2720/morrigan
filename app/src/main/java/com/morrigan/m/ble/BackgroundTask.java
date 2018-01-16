package com.morrigan.m.ble;

/**
 * 后台任务
 * <p/>
 * Created by y on 2016/6/21.
 */
public interface BackgroundTask extends Runnable {

    /**
     * 生产唯一标识key
     */
    int generateKey();
}
