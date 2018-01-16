package com.github.yzeaho.common;

import android.os.SystemClock;
import android.support.annotation.WorkerThread;

/**
 * 最低等待多长时间<br>
 * 如果运行一段代码很快就持续完成了，界面就刷新了， 会变成界面闪烁，<br>
 * 这里增加最少要运行多少秒的等待操作。<br>
 * 该代码必须运行在后台线程
 *
 * @author y
 */
public class SleepTime {

    private long startTime;

    public SleepTime() {
        startTime = SystemClock.elapsedRealtime();
    }

    @WorkerThread
    public void sleep(int minSleepTime) {
        long endTime = SystemClock.elapsedRealtime();
        long spentTime = endTime - startTime;
        long restTime = minSleepTime - spentTime;
        if (restTime > 0) {
            try {
                Thread.sleep(restTime);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }
}
