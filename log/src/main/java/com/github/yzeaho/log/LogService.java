package com.github.yzeaho.log;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.github.yzeaho.file.FileApi;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 记录日志服务<br>
 * 将日志记录写入到文件。
 *
 * @author y
 * @since 1.0
 */
public class LogService extends IntentService {

    public static final String TAG = "LogService";
    public static final int MAX_LENGTH = 100 * 1024;
    private BufferedOutputStream out;

    public LogService() {
        super("LogService");
    }

    /**
     * 记录日志
     */
    public static void log(Context context, String text) {
        if (text == null) {
            return;
        }
        log(context, text.getBytes(), LogService.class);
    }

    protected static void log(Context context, byte[] bs, Class<? extends LogService> cl) {
        if (bs == null) {
            return;
        } else if (bs.length <= MAX_LENGTH) {
            Intent i = new Intent(context, cl);
            i.putExtra("extra_log", bs);
            context.startService(i);
        } else {
            byte[] b = new byte[MAX_LENGTH];
            System.arraycopy(bs, 0, b, 0, b.length);
            Intent i = new Intent(context, cl);
            i.putExtra("extra_log", b);
            context.startService(i);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        try {
            // 没有初始化则初始化文件写入流，已经初始化完成则直接写入文件
            // 若继续有操作日志写入请求则重用写入流继续写入，
            // 没有写入请求时，会自动关闭服务，这时一起关闭输入流。
            if (out == null) {
                out = init();
            }
            byte[] bs = intent.getByteArrayExtra("extra_log");
            if (bs != null) {
                out.write(bs);
                out.write(0x0A);
                out.flush();
            }
        } catch (Throwable e) {
            Log.w(TAG, "failed to write file", e);
        }
    }

    protected BufferedOutputStream init() throws IOException {
        File dir = getExternalFilesDir("log");
        FileApi.checkDir(dir);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String name = sdf.format(new Date()) + ".log";
        File file = new File(dir, name);
        return new BufferedOutputStream(new FileOutputStream(file, true));
    }
}
