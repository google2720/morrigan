package com.github.yzeaho.http;

import com.github.yzeaho.log.Lg;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络请求接口
 * Created by y on 2016/10/3.
 */
public interface HttpInterface {

    Result execute(Request request) throws IOException;

    Result execute(Request request, ProgressListener progressListener) throws IOException;

    Call enqueue(Request request, Callback responseCallback) throws IOException;

    Call enqueue(Request request, Callback responseCallback, ProgressListener progressListener) throws IOException;

    class Result {
        private static final String TAG = "http";
        public Call call;
        public Response response;

        public <T> T parse(Class<T> cls) throws IOException {
            if (response == null) {
                throw new RuntimeException("response is null");
            }
            try {
                if (!response.isSuccessful()) {
                    throw new RuntimeException(response.code() + " " + response.message());
                }
                String r = response.body().string();
                Lg.d(TAG, hashCode() + " rev:" + r);
                return new Gson().fromJson(r, cls);
            } finally {
                response.close();
            }
        }

        Response execute() throws IOException {
            Lg.d(TAG, hashCode() + " send:" + call.request().url());
            Response response = call.execute();
            Lg.d(TAG, hashCode() + " " + response.toString());
            return response;
        }
    }

    class Factory {

        private static final HttpInterface sInstance = new OkHttpImpl();

        public static HttpInterface create() {
            return sInstance;
        }
    }
}
