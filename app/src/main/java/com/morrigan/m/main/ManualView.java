package com.morrigan.m.main;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.SystemClock;
import android.support.annotation.Keep;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * 手动按摩控件
 * Created by y on 2016/10/15.
 */
public class ManualView extends View {

    private static final int MIN_GEAR = 1;
    private static final int MAX_GEAR = 5;
    private float density;
    private Paint paint;
    private int offset = 2;
    private int offset2 = 10;
    private int gear = 1;
    private boolean start;
    private long startTime;
    private Duration duration = new Duration();
    private float av;
    private RadialGradient radialGradient1;
    private RadialGradient radialGradient2;
    private RadialGradient radialGradient3;
    private RadialGradient radialGradient4;

    public ManualView(Context context, AttributeSet attrs) {
        super(context, attrs);
        density = getResources().getDisplayMetrics().density;
        offset *= density;
        offset2 *= density;
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != 0) {
            int cx = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2;
            int cy = (getHeight() - getPaddingTop() - getPaddingBottom()) / 2;
            radialGradient1 = new RadialGradient(cx, cy, offset2, 0x00b57fef, 0xffb57fef, Shader.TileMode.REPEAT);
            radialGradient2 = new RadialGradient(cx, cy, offset2, 0x00b378f2, 0xffb378f2, Shader.TileMode.REPEAT);
            radialGradient3 = new RadialGradient(cx, cy, offset2, 0x00b171f5, 0xffb171f5, Shader.TileMode.REPEAT);
            radialGradient4 = new RadialGradient(cx, cy, offset2, 0x00ae66f9, 0xffae66f9, Shader.TileMode.REPEAT);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int w = getWidth() - getPaddingLeft() - getPaddingRight();
        final int h = getHeight() - getPaddingTop() - getPaddingBottom();
        final int cx = w / 2;
        final int cy = h / 2;
        final float radius = w * 25f / 100;
        offset2 = Math.round(radius / 6);

        paint.setShader(null);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);
        paint.setColor(0xff7b3ac3);
        canvas.drawCircle(cx, cy, radius, paint);

        int left = Math.round(cx - radius);
        int top = cy;
        int right = Math.round(cx + radius);
        int bottom = Math.round(cy + radius);
        drawDynamicWave(canvas, left, right, bottom, top, w, radius, cx, cy);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(offset);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(cx, cy, radius + offset, paint);

        float radiusOut = radius + offset2 / 2;
        paint.setShader(radialGradient1);
        paint.setStrokeWidth(offset2);
        canvas.drawCircle(cx, cy, radiusOut, paint);

        radiusOut = radius + offset2 + offset2 / 2;
        paint.setShader(radialGradient2);
        paint.setStrokeWidth(offset2);
        canvas.drawCircle(cx, cy, radiusOut, paint);

        radiusOut = radius + offset2 * 2 + offset2 / 2;
        paint.setShader(radialGradient3);
        paint.setStrokeWidth(offset2);
        canvas.drawCircle(cx, cy, radiusOut, paint);

        radiusOut = radius + offset2 * 3 + offset2 / 2;
        paint.setShader(radialGradient4);
        paint.setStrokeWidth(offset2);
        canvas.drawCircle(cx, cy, radiusOut, paint);

        paint.setShader(null);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(96);
        paint.setTextAlign(Paint.Align.CENTER);
        if (start) {
            final long time = SystemClock.uptimeMillis() - startTime;
            canvas.drawText(duration.toValue(time / 1000), cx, cy + radius * 3 / 5, paint);
        } else {
            canvas.drawText("00:00", cx, cy + radius * 3 / 5, paint);
        }
        paint.setTextSize(256);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(String.valueOf(gear), cx, cy, paint);
        paint.setTextSize(96);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("gear", cx, cy, paint);

        postInvalidateDelayed(1000 / 60);
    }

    private int p;

    private void drawDynamicWave(Canvas canvas, int startX, int endX, int startY, int endY, int w, float radius, int cx, int cy) {
        int y;
        float period = (float) (2 * Math.PI / getWidth());
        float offset = av * (startY - endY) / (10 - gear - 1);
        for (int i = startX; i < endX; i++) {
            // (x-a)^2+(y-b)^2=c^2 其中(a,b)为圆心，c为半径。
            startY = (int) Math.round(endY + Math.sqrt(radius * radius - Math.pow(i - cx, 2)));
            // y = Asin(wx+b)+h ，这个公式里：w影响周期，A影响振幅，h影响y位置，b为初相；
            y = endY + (int) Math.round(offset * (Math.sin(period * (p + i)) + 1) / 2f);

            paint.setColor(0x7f9147dd);
            canvas.drawLine(i, startY, i, y, paint);

            y = endY + (int) Math.round(offset * (Math.cos(period * (p + i)) + 1) / 2f);
            paint.setColor(0x7f9c47d3);
            canvas.drawLine(i, startY, i, y, paint);
        }
        p += density * 10;
    }

    public void addGear() {
        gear = Math.min(MAX_GEAR, ++gear);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void deleteGear() {
        gear = Math.max(MIN_GEAR, --gear);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    @Keep
    public void setAnimValue(float v) {
        av = v;
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void start() {
        startTime = SystemClock.uptimeMillis();
        start = true;
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "animValue", av, 1);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setAutoCancel(true);
        animator.start();
    }

    public void stop() {
        start = false;
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "animValue", av, 0);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setAutoCancel(true);
        animator.start();
    }
}
