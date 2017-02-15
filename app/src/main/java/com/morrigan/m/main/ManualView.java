package com.morrigan.m.main;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.annotation.Keep;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.LinearInterpolator;

import com.github.yzeaho.log.Lg;
import com.morrigan.m.BuildConfig;
import com.morrigan.m.R;

/**
 * 手动按摩控件
 * Created by y on 2016/10/15.
 */
public class ManualView extends SurfaceView implements SurfaceHolder.Callback {

    private static final int MIN_GEAR = 1;
    private static final int MAX_GEAR = 3;
    private Paint paint = new Paint();
    private Paint timePaint = new Paint();
    private Paint textPaint = new Paint();
    private Paint text2Paint = new Paint();
    private Paint wave1Paint = new Paint();
    private Paint wave2Paint = new Paint();
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
    private Rect drawableRect = new Rect();
    private Halo halo;
    private final Drawable drawable;

    public ManualView(Context context, AttributeSet attrs) {
        super(context, attrs);
        drawable = ContextCompat.getDrawable(getContext(), R.drawable.manual_bg);
        halo = new DrawableHalo(context, 90f);
        halo.setGear(gear);

        float density = getResources().getDisplayMetrics().density;
        textPadding *= density;
        offset *= density;
        wave *= density;

        paint.setAntiAlias(true);

        textPaint.setAntiAlias(true);
        textPaint.setStrokeWidth(1);
        textPaint.setColor(Color.WHITE);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.RIGHT);
        textPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/fzltcxh.ttf"));

        text2Paint.setAntiAlias(true);
        text2Paint.setStrokeWidth(1);
        text2Paint.setColor(Color.WHITE);
        text2Paint.setFakeBoldText(true);
        text2Paint.setTextAlign(Paint.Align.LEFT);
        text2Paint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/fzltcxh.ttf"));

        timePaint.setAntiAlias(true);
        timePaint.setStrokeWidth(1);
        timePaint.setColor(Color.WHITE);
        timePaint.setTextAlign(Paint.Align.CENTER);
        timePaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/fzltxhk.ttf"));

        wave1Paint.setAntiAlias(true);
        wave1Paint.setColor(0x7fbc54e2);

        wave2Paint.setAntiAlias(true);
        wave2Paint.setColor(0x7fa753f7);

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
            try {
                // 不停绘制界面
                while (drawCreated) {
                    final long startTime = SystemClock.elapsedRealtime();
                    final SurfaceHolder holder = getHolder();
                    if (holder != null) {
                        final Canvas canvas = holder.lockCanvas();
                        if (canvas != null) {
                            try {
                                // 清屏
                                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                                canvas.drawPaint(paint);
                                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
                                drawImpl(canvas);
                            } finally {
                                holder.unlockCanvasAndPost(canvas);
                            }
                        }
                    }
                    final long endTime = SystemClock.elapsedRealtime();
                    Log.i("DrawableHalo", "s " + (endTime - startTime));
                    SystemClock.sleep(Math.max(0, 1000 / 60 - (endTime - startTime)));
                }
            } catch (Exception e) {
                Lg.w("ManualView", "failed to draw manual view", e);
            }
        }
    }

    public interface Halo {

        void draw(Canvas canvas, int cx, int cy, float initRadius, float maxRadius);

        void setGear(int gear);

        void stop();
    }

    private void drawImpl(Canvas canvas) {
        final int w = getWidth();
        final int h = getHeight();
        final int cx = w / 2;
        final int cy = h / 2;
        final float radius = w * 25f / 100;

        if (start) {
            halo.draw(canvas, cx, cy, radius, Math.min(cx, cy));
        }

        if (BuildConfig.WAVE_ON) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(0xff7b3ac3);
            canvas.drawCircle(cx, cy, radius, paint);

            final int left = Math.round(cx - radius);
            final int top = Math.round(cy + radius / 4);
            final int right = Math.round(cx + radius);
            final int bottom = Math.round(cy + radius);
            drawDynamicWave(canvas, left, right, bottom, top, w, radius, cx, cy);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(offset);
            paint.setColor(Color.WHITE);
            canvas.drawCircle(cx, cy, radius, paint);
        } else {
            drawableRect.left = Math.round(cx - radius);
            drawableRect.top = Math.round(cy - radius);
            drawableRect.right = Math.round(cx + radius);
            drawableRect.bottom = Math.round(cy + radius);
            drawable.setBounds(drawableRect);
            drawable.draw(canvas);
        }

        timePaint.setTextSize(w / 15);
        final String text;
        if (start) {
            text = duration.toValue((SystemClock.elapsedRealtime() - startTime) / 1000);
        } else {
            text = "00:00";
        }
        canvas.drawText(text, cx, cy + radius * 70 / 100, timePaint);

        final int saveCount = canvas.save();
        canvas.translate(cx, cy);
        textPaint.setTextSize(w / 5);
        canvas.drawText(String.valueOf(gear), 0, 0, textPaint);
        text2Paint.setTextSize(w / 18);
        canvas.drawText("gear", textPadding, 0, text2Paint);
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
        waveProgress = (endX - startX) / 2;
        int sy, ey;
        float period = (float) (2 * Math.PI / ((endX - startX) / 1.4));
        float a = (startY - endY) / 10;
        for (int i = startX; i < endX; i++) {
            sy = (int) Math.round(cy + Math.sqrt(radius * radius - Math.pow(i - cx, 2)));

            ey = (int) Math.round(a * Math.cos(period * (waveProgress + i - startX)) + endY);
            ey = Math.min(sy, ey);
            canvas.drawLine(i, sy, i, ey, wave1Paint);

            ey = (int) Math.round(a * Math.sin(period * (waveProgress + i - startX)) + endY);
            ey = Math.min(sy, ey);
            canvas.drawLine(i, sy, i, ey, wave2Paint);
        }
    }

    public boolean addGear() {
        if (gear == MAX_GEAR) {
            return false;
        }
        gear = Math.min(MAX_GEAR, ++gear);
        halo.setGear(gear);
        return true;
    }

    public boolean deleteGear() {
        if (gear == MIN_GEAR) {
            return false;
        }
        gear = Math.max(MIN_GEAR, --gear);
        halo.setGear(gear);
        return true;
    }

    @Keep
    public void setAnimValue(float v) {
        av = v;
    }

    public void start() {
        startTime = SystemClock.elapsedRealtime();
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
        halo.stop();
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
