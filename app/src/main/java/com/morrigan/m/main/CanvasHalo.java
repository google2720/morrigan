package com.morrigan.m.main;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * 画布光环动画
 * Created by y on 2017/2/14.
 */
public class CanvasHalo implements ManualView.Halo {

    private Paint paint = new Paint();
    private float time;
    private int alpha = 0xff;
    private float v = 0;
    private int gear;

    private CanvasHalo(float time) {
        this.time = time;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
    }

    @Override
    public void draw(Canvas canvas, int cx, int cy, float initRadius, float maxRadius) {
        paint.setAlpha(Math.min(alpha, 0xff));
        canvas.drawCircle(cx, cy, initRadius + v, paint);
        final float totalTime = (time - (gear - 1) * 30);
        v += ((maxRadius - initRadius) / totalTime);
        alpha -= (0xff / totalTime);
        if (alpha <= 0 || initRadius + v >= maxRadius) {
            v = 0;
            alpha = 0xff;
        }
    }

    @Override
    public void setGear(int gear) {
        this.gear = gear;
    }

    @Override
    public void stop() {
        v = 0;
        alpha = 0xff;
    }
}