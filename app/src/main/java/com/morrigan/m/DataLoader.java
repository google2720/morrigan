package com.morrigan.m;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

/**
 * 异步数据加载器
 * 
 * @author y
 */
public abstract class DataLoader<D> extends AsyncTaskLoader<D> {

    private ForceLoadContentObserver observer;

    /**
     * 自动响应刷新数据的URI
     */
    private Uri uri;

    public DataLoader(Context context) {
        super(context.getApplicationContext());
    }

    public DataLoader(Context context, Uri uri) {
        super(context.getApplicationContext());
        observer = new ForceLoadContentObserver();
        this.uri = uri;
    }

    @Override
    protected void onStartLoading() {
        if (uri != null) {
            getContext().getContentResolver().registerContentObserver(uri, true, observer);
        }
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        if (uri != null) {
            getContext().getContentResolver().unregisterContentObserver(observer);
        }
    }
}
