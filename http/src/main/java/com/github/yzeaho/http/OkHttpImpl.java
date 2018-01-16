package com.github.yzeaho.http;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 封装okhttp的网络请求
 * Created by y on 2016/4/18.
 */
public class OkHttpImpl implements HttpInterface {

    private OkHttpClient client;

    public OkHttpImpl() {
        this(null);
    }

    public OkHttpImpl(Interceptor logInterceptor) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(20, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);
        if (logInterceptor != null) {
            builder.addInterceptor(logInterceptor);
        }
        client = builder.build();
    }

    @Override
    public Result execute(Request request) throws IOException {
        Result r = new Result();
        r.call = client.newCall(request);
        r.response = r.execute();
        return r;
    }

    @Override
    public Result execute(Request request, ProgressListener progressListener) throws IOException {
        Result r = new Result();
        r.call = client.newBuilder().addNetworkInterceptor(new ProgressInterceptor(progressListener)).build().newCall(request);
        r.response = r.execute();
        return r;
    }

    @Override
    public Call enqueue(Request request, Callback responseCallback) throws IOException {
        Call call = client.newCall(request);
        call.enqueue(responseCallback);
        return call;
    }

    @Override
    public Call enqueue(Request request, Callback responseCallback, ProgressListener progressListener) throws IOException {
        Call call = client.newBuilder().addNetworkInterceptor(new ProgressInterceptor(progressListener)).build().newCall(request);
        call.enqueue(responseCallback);
        return call;
    }
}
