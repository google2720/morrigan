package com.morrigan.m.main;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.support.annotation.Keep;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.LinearInterpolator;

import com.morrigan.m.BuildConfig;

/**
 * 手动按摩控件
 * Created by y on 2016/10/15.
 */
public class ManualView extends SurfaceView implements SurfaceHolder.Callback {

    private static final int MIN_GEAR = 1;
    private static final int MAX_GEAR = 3;
    private Paint paint = new Paint();
    private Paint timePaint = new Paint();
    private int offset = 2;
    private int textPadding = 10;
    private int gear = 1;
    private int wave = 1;
    private boolean start;
    private long startTime;
    private Duration duration = new Duration();
    private float av;
    private long startSystemTime;
    private long stopSystemTime;
    private RenderThread renderThread;
    private volatile boolean drawCreated;

    public ManualView(Context context, AttributeSet attrs) {
        super(context, attrs);
        float density = getResources().getDisplayMetrics().density;
        textPadding *= density;
        offset *= density;
        wave *= density;

        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        paint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/fzltcxh.ttf"));

        timePaint.setAntiAlias(true);
        timePaint.setColor(Color.WHITE);
        timePaint.setTextAlign(Paint.Align.CENTER);

        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        getHolder().addCallback(this);


    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawCreated = true;
        if (renderThread == null) {
            renderThread = new RenderThread();
            renderThread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        drawCreated = false;
        renderThread = null;
    }

    public boolean isStart() {
        return start;
    }

    public byte getGear() {
        return (byte) gear;
    }

    private class RenderThread extends Thread {

        private Paint paint = new Paint();

        @Override
        public void run() {
            // 不停绘制界面
            while (drawCreated) {
                long startTime = SystemClock.uptimeMillis();
                Canvas canvas = getHolder().lockCanvas();
                if (canvas != null) {
                    try {
                        // 清屏
                        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                        canvas.drawPaint(paint);
                        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
                        drawImpl(canvas);
                    } finally {
                        if (drawCreated) {
                            getHolder().unlockCanvasAndPost(canvas);
                        }
                    }
                }
                long endTime = SystemClock.uptimeMillis();
                SystemClock.sleep(Math.max(0, 1000 / 60 - (endTime - startTime)));
            }
        }
    }

    private class Halo {

        private Paint paint;
        private float time = 60f;
        private int alpha = 0xff;
        private float v;

        private Halo(float time) {
            this.time = time;
            paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
        }

        private void draw(Canvas canvas, int cx, int cy, float initRadius, float maxRadius) {
            final float totalTime = (time - gear * 6);
            paint.setAlpha(Math.min(alpha, 0xff));
            canvas.drawCircle(cx, cy, initRadius + v, paint);
            v += (maxRadius / totalTime);
            alpha -= (0xff / totalTime);
            if (alpha <= 0 || v >= maxRadius) {
                v = 0;
                alpha = 0xff;
            }
        }
    }

    private Halo halo = new Halo(90f);

    private void drawImpl(Canvas canvas) {
        final int w = getWidth();
        final int h = getHeight();
        final int cx = w / 2;
        final int cy = h / 2;
        final float radius = w * 25f / 100;
        final float maxRadius = Math.min(cx / 2, cy / 2);

        if (start) {
            halo.draw(canvas, cx, cy, radius, maxRadius);
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xff7b3ac3);
        canvas.drawCircle(cx, cy, radius, paint);

        int left = Math.round(cx - radius);
        int top = Math.round(cy + radius / 4);
        int right = Math.round(cx + radius);
        int bottom = Math.round(cy + radius);
        if (BuildConfig.WAVE_ON) {
            drawDynamicWave(canvas, left, right, bottom, top, w, radius, cx, cy);
        } else {
            drawStaticWave(canvas, left, right, bottom, top, w, radius, cx, cy);
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(offset);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(cx, cy, radius, paint);

        timePaint.setTextSize(w / 14);
        final String text;
        if (start) {
            text = duration.toValue((SystemClock.uptimeMillis() - startTime) / 1000);
        } else {
            text = "00:00";
        }
        canvas.drawText(text, cx, cy + radius * 70 / 100, timePaint);

        int saveCount = canvas.save();
        canvas.translate(cx, cy);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);
        paint.setTextSize(w / 6);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(String.valueOf(gear), 0, 0, paint);
        paint.setTextSize(w / 18);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("gear", textPadding, 0, paint);
        canvas.restoreToCount(saveCount);
    }

    private long waveProgress;

    private void drawDynamicWave(Canvas canvas, int startX, int endX, int startY, int endY, int w, float radius, int cx, int cy) {
        int sy, ey;
        float period = (float) (2 * Math.PI / w);
        float a = av * (startY - endY) / (10 - gear - 1);
        for (int i = startX; i < endX; i++) {
            // (x-a)^2+(y-b)^2=c^2 其中(a,b)为圆心，c为半径。
            sy = (int) Math.round(cy + Math.sqrt(radius * radius - Math.pow(i - cx, 2)));
            // y = a * sin(wx+b)+h ，这个公式里：w影响周期，a影响振幅，h影响y位置，b为初相；
            ey = (int) Math.round(a * Math.sin(period * (waveProgress + i)) + endY);
            ey = Math.min(sy, ey);
            paint.setColor(0x7f9147dd);
            canvas.drawLine(i, sy, i, ey, paint);

            ey = (int) Math.round(a * Math.cos(period * (waveProgress + i)) + endY);
            ey = Math.min(sy, ey);
            paint.setColor(0x7f9c47d3);
            canvas.drawLine(i, sy, i, ey, paint);
        }
        waveProgress += (wave * Math.sqrt(gear));
    }

    private void drawStaticWave(Canvas canvas, int startX, int endX, int startY, int endY, int w, float radius, int cx, int cy) {
        waveProgress = w / 2;
        int sy, ey;
        float period = (float) (2 * Math.PI / w);
        float a = (startY - endY) / 10;
        for (int i = startX; i < endX; i++) {
            sy = (int) Math.round(cy + Math.sqrt(radius * radius - Math.pow(i - cx, 2)));
            ey = (int) Math.round(a * Math.sin(period * (waveProgress + i)) + endY);
            ey = Math.min(sy, ey);
            paint.setColor(0x7f9147dd);
            canvas.drawLine(i, sy, i, ey, paint);

            ey = (int) Math.round(a * Math.cos(period * (waveProgress + i)) + endY);
            ey = Math.min(sy, ey);
            paint.setColor(0x7f9c47d3);
            canvas.drawLine(i, sy, i, ey, paint);
        }
    }

    public boolean addGear() {
        if (gear == MAX_GEAR) {
            return false;
        }
        gear = Math.min(MAX_GEAR, ++gear);
        return true;
    }

    public boolean deleteGear() {
        if (gear == MIN_GEAR) {
            return false;
        }
        gear = Math.max(MIN_GEAR, --gear);
        return true;
    }

    @Keep
    public void setAnimValue(float v) {
        av = v;
        // ViewCompat.postInvalidateOnAnimation(this);
    }

    public void start() {
        startTime = SystemClock.uptimeMillis();
        startSystemTime = System.currentTimeMillis();
        start = true;
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "animValue", av, 1);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setAutoCancel(true);
        animator.start();
    }

    public void stop() {
        stopSystemTime = System.currentTimeMillis();
        start = false;
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "animValue", av, 0);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setAutoCancel(true);
        animator.start();
    }

    public long getStartSystemTime() {
        return startSystemTime;
    }

    public long getStopSystemTime() {
        return stopSystemTime;
    }
}
