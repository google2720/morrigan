package com.github.yzeaho.http;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.lang.reflect.Modifier;
import java.util.concurrent.Executor;

/**
 * http请求的异步任务
 * Created by y on 2016/7/7.
 */
public abstract class HttpAsyncTask<Result> {

    public interface Callback<Result> {
        void onPreExecute();

        void onPostExecute(Result result);
    }

    private FetchAsyncTask task;
    private WeakReference<Callback> callback;

    public static void cancel(HttpAsyncTask task, boolean mayInterruptIfRunning) {
        if (task != null) {
            task.cancel(mayInterruptIfRunning);
        }
    }

    public void cancel(boolean mayInterruptIfRunning) {
        if (task != null) {
            task.cancel(mayInterruptIfRunning);
        }
        callback = null;
    }

    public void start(Executor exec, Callback callback) {
        this.callback = new WeakReference<>(callback);
        task = new FetchAsyncTask();
        task.executeOnExecutor(exec);
    }

    private void preExecute() {
        if (callback != null) {
            Callback cb = callback.get();
            if (cb != null) {
                cb.onPreExecute();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void postExecute(Result result) {
        if (callback != null) {
            Callback cb = callback.get();
            if (cb != null) {
                cb.onPostExecute(result);
            }
        }
    }

    private final class FetchAsyncTask extends AsyncTask<Void, Void, Result> {

        @Override
        protected void onPreExecute() {
            HttpAsyncTask.this.preExecute();
        }

        @Override
        protected Result doInBackground(Void... params) {
            if (HttpAsyncTask.this.getClass().isMemberClass() && !Modifier.isStatic(HttpAsyncTask.this.getClass().getModifiers())) {
                throw new IllegalArgumentException("Object must not be a non-static inner member class: " + HttpAsyncTask.this);
            }
            return HttpAsyncTask.this.doInBackground();
        }

        @Override
        protected void onPostExecute(Result result) {
            HttpAsyncTask.this.postExecute(result);
        }
    }

    protected abstract Result doInBackground();
}
