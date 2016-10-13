package com.morrigan.m.ble;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;

/**
 * 后台线程
 *
 * @author y
 */
public class BackgroundHandler {

    private static final Handler sHandler;

    static {
        HandlerThread sLooperThread = new HandlerThread("BackgroundHandler", Process.THREAD_PRIORITY_BACKGROUND);
        sLooperThread.start();
        sHandler = new Handler(sLooperThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                BackgroundTask task = (BackgroundTask) msg.obj;
                if (task != null) {
                    task.run();
                }
            }
        };
    }

    private BackgroundHandler() {
    }

    public static void send(BackgroundTask task) {
        if (task != null) {
            int key = task.generateKey();
            sHandler.removeMessages(key);
            sHandler.sendMessage(sHandler.obtainMessage(key, task));
        }
    }

    public static void post(Runnable runnable) {
        sHandler.post(runnable);
    }

    public static void remove(Runnable runnable) {
        sHandler.removeCallbacks(runnable);
    }
}
