package com.github.yzeaho.http;

/**
 * 网络返回响应流获取的进度
 * Created by y on 2016/4/26.
 */
public interface ProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
