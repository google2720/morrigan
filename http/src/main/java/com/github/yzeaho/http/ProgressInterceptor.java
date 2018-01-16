package com.github.yzeaho.http;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 网络返回响应流获取的进度拦截器
 * Created by y on 2016/4/26.
 */
public class ProgressInterceptor implements Interceptor {

    private ProgressListener progressListener;

    public ProgressInterceptor(ProgressListener l) {
        progressListener = l;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder().body(new ProgressResponseBody(originalResponse.body(), progressListener)).build();
    }
}
