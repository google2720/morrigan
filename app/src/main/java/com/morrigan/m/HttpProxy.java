package com.morrigan.m;

import android.content.Context;

import com.github.yzeaho.http.HttpInterface;
import com.morrigan.m.utils.NetUtils;

import java.io.IOException;

import okhttp3.Request;

/**
 * http代理
 * Created by y on 2016/11/16.
 */
public class HttpProxy {

    private HttpInterface.Result result;

    public <T> T execute(Context context, Request request, Class<T> cl) throws IOException {
        if (!NetUtils.isConnected(context)) {
            throw new NoNetException("No NetWork");
        }
        result = HttpInterface.Factory.create().execute(request);
        return result.parse(cl);
    }

    public void cancel() {
        if (result != null && result.call != null) {
            result.call.cancel();
        }
    }

    public static String parserError(Context context, Throwable e) {
        if (e instanceof NoNetException) {
            return context.getString(R.string.error_no_net);
        } else {
            return e.getMessage();
        }
    }
}
