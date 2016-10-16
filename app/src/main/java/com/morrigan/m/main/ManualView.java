package com.morrigan.m.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 手动按摩控件
 * Created by y on 2016/10/15.
 */
public class ManualView extends View {

    private Paint paint;
    private int offset = 2;
    private int offset2 = 10;
    private boolean debug = false;
    private Xfermode duffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    private Paint maskXferPaint = new Paint();

    public ManualView(Context context, AttributeSet attrs) {
        super(context, attrs);
        float density = getResources().getDisplayMetrics().density;
        offset *= density;
        offset2 *= density;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(96);
        paint.setColor(0xff7b3ac3);
        maskXferPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int w = getWidth() - getPaddingLeft() - getPaddingRight();
        final int h = getHeight() - getPaddingTop() - getPaddingBottom();
        int cx = w / 2;
        int cy = h / 2;
        float radius = w / 4;

        paint.setColor(0xffae66f9);
        canvas.drawCircle(cx, cy, radius + offset + offset2 * 3, paint);

        paint.setColor(0xffb272f5);
        canvas.drawCircle(cx, cy, radius + offset + offset2 * 2, paint);

        paint.setColor(0xffb57fef);
        canvas.drawCircle(cx, cy, radius + offset + offset2, paint);

        paint.setColor(Color.WHITE);
        canvas.drawCircle(cx, cy, radius + offset, paint);

        paint.setColor(0xff7b3ac3);
        canvas.drawCircle(cx, cy, radius, paint);

        int left = Math.round(cx - radius);
        int top = cy;
        int right = Math.round(cx + radius);
        int bottom = Math.round(cy + radius);
        drawDynamicWave(canvas, left, right, bottom, top, w);


        int sc = canvas.saveLayer(left, top, right, bottom, maskXferPaint, Canvas.ALL_SAVE_FLAG);
        paint.setColor(Color.RED);
        canvas.drawCircle(cx, cy, radius + 100, paint);
        paint.setColor(Color.YELLOW);
        canvas.drawCircle(cx, cy, radius - 30, paint);
//        drawDynamicWave(canvas, left, right, bottom, top, w);
        canvas.restoreToCount(sc);

        paint.setColor(Color.WHITE);
        canvas.drawText("05:25", cx, cy + radius / 2 + radius / 4, paint);

        postInvalidateDelayed(100);
    }

    private int p;

    private void drawDynamicWave(Canvas canvas, int startX, int endX, int startY, int endY, int w) {
        float zq = (float) (2 * Math.PI / w);
        int y;
        int offset = (startY - endY) / 5;
        for (int i = startX; i < endX; i++) {
            double a = Math.sin(zq * (p + i));
            double b = (a + 1) / 2f;
            long c = Math.round(offset * b);
            y = endY + (int) c;
            if (debug) {
                Log.i("yyyyy", String.format("zq %s/%s/%s/%s/%s/%s/%s", p + i, zq, a, b, offset, c, y));
                Log.i("bbbbb", String.format("c %s", c));
            }
            paint.setColor(0x7f9147dd);
            canvas.drawLine(i, startY, i, y, paint);

            a = Math.cos(zq * (p + i));
            b = (a + 1) / 2f;
            c = Math.round(offset * b);
            y = endY + (int) c;
            paint.setColor(0x7f9c47d3);
            canvas.drawLine(i, startY, i, y, paint);
        }
        p++;
    }
}
