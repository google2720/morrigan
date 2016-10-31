package com.morrigan.m;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;
import android.util.Log;

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
        Log.i(TAG, activity + " onCreate has savedInstanceState " + (savedInstanceState != null));
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.i(TAG, activity + " onStart");
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.i(TAG, activity + " onResume");
        MobclickAgent.onResume(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.i(TAG, activity + " onPause");
        MobclickAgent.onPause(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.i(TAG, activity + " onStop");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.i(TAG, activity + " onSaveInstanceState");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.i(TAG, activity + " onDestroy");
    }
}
