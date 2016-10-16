package com.morrigan.m.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * 手动按摩控件
 * Created by y on 2016/10/15.
 */
public class ManualView extends View {

    private float density;
    private Paint paint;
    private int offset = 2;
    private int offset2 = 10;
    private int gear = 1;

    public ManualView(Context context, AttributeSet attrs) {
        super(context, attrs);
        density = getResources().getDisplayMetrics().density;
        offset *= density;
        offset2 *= density;
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int w = getWidth() - getPaddingLeft() - getPaddingRight();
        final int h = getHeight() - getPaddingTop() - getPaddingBottom();
        int cx = w / 2;
        int cy = h / 2;
        float radius = w * 25f / 100;
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
        paint.setShader(new RadialGradient(cx, cy, offset2, 0x00b57fef, 0xffb57fef, Shader.TileMode.REPEAT));
        paint.setStrokeWidth(offset2);
        canvas.drawCircle(cx, cy, radiusOut, paint);

        radiusOut = radius + offset2 + offset2 / 2;
        paint.setShader(new RadialGradient(cx, cy, offset2, 0x00b378f2, 0xffb378f2, Shader.TileMode.REPEAT));
        paint.setStrokeWidth(offset2);
        canvas.drawCircle(cx, cy, radiusOut, paint);

        radiusOut = radius + offset2 * 2 + offset2 / 2;
        paint.setShader(new RadialGradient(cx, cy, offset2, 0x00b171f5, 0xffb171f5, Shader.TileMode.REPEAT));
        paint.setStrokeWidth(offset2);
        canvas.drawCircle(cx, cy, radiusOut, paint);

        radiusOut = radius + offset2 * 3 + offset2 / 2;
        paint.setShader(new RadialGradient(cx, cy, offset2, 0x00ae66f9, 0xffae66f9, Shader.TileMode.REPEAT));
        paint.setStrokeWidth(offset2);
        canvas.drawCircle(cx, cy, radiusOut, paint);

        paint.setShader(null);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(96);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("05:25", cx, cy + radius * 3 / 5, paint);
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
        float period = (float) (2 * Math.PI / w);
        int y;
        int offset = (startY - endY) / (10 - gear - 1);
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

    public void setGear(int gear) {
        this.gear = gear;
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void addGear() {
        gear = Math.min(5, ++gear);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void deleteGear() {
        gear = Math.max(0, --gear);
        ViewCompat.postInvalidateOnAnimation(this);
    }
}
