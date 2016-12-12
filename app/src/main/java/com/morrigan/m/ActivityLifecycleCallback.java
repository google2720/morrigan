package com.morrigan.m;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;

import com.github.yzeaho.log.Lg;
import com.umeng.analytics.MobclickAgent;


/**
 * 检查activity生命周期
 *
 * @author y
 * @since 1.0
 */
public class ActivityLifecycleCallback implements ActivityLifecycleCallbacks {

    private static final String TAG = "Activity";

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Lg.d(TAG, activity + " onCreate has savedInstanceState " + (savedInstanceState != null));
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Lg.d(TAG, activity + " onStart");
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Lg.d(TAG, activity + " onResume");
        MobclickAgent.onResume(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Lg.d(TAG, activity + " onPause");
        MobclickAgent.onPause(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Lg.d(TAG, activity + " onStop");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Lg.d(TAG, activity + " onSaveInstanceState");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Lg.d(TAG, activity + " onDestroy");
    }
}
