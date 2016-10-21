package com.morrigan.m.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * 电池电量
 * Created by y on 2016/10/22.
 */
public class BatteryView extends View {

    private Paint paint = new Paint();
    private Paint textPaint1 = new Paint();
    private Paint textPaint2 = new Paint();
    private Paint textPaint3 = new Paint();
    private Rect boundText1 = new Rect();
    private Rect boundText2 = new Rect();
    private int offset = 2;
    private Rect rect = new Rect();
    private PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);

    public BatteryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        float density = getResources().getDisplayMetrics().density;
        offset *= density;
        paint.setAntiAlias(true);
        textPaint1.setAntiAlias(true);
        textPaint1.setColor(Color.WHITE);
        textPaint1.setTextSize(96);
        textPaint1.getTextBounds("20", 0, "20".length(), boundText1);
        textPaint2.setAntiAlias(true);
        textPaint2.setColor(Color.WHITE);
        textPaint2.setTextSize(48);
        textPaint2.getTextBounds("%", 0, "%".length(), boundText2);
        textPaint3.setAntiAlias(true);
        textPaint3.setColor(Color.WHITE);
        textPaint3.setTextSize(48);
        textPaint3.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int w = getWidth();
        final int h = getHeight();
        final int cx = w / 2;
        final int cy = h / 2;
        float radius = w / 2 - 15;

        int sc = canvas.saveLayer(0, 0, w, h, null, Canvas.ALL_SAVE_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xff9438ca);
        canvas.drawCircle(cx, cy, radius, paint);
        rect.left = 0;
        rect.top = h * 70 / 100;
        rect.right = w;
        rect.bottom = h;
        paint.setColor(0xffc34fdc);
        paint.setXfermode(xfermode);
        canvas.drawRect(rect, paint);
        paint.setXfermode(null);
        canvas.restoreToCount(sc);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(30);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(cx, cy, radius, paint);

        paint.setStrokeWidth(10);
        paint.setColor(0xffc5e3c9);
        canvas.drawCircle(cx, cy, radius, paint);

        canvas.drawText("20", (w - boundText1.width() - boundText2.width() - offset) / 2, cy, textPaint1);
        canvas.drawText("%", (w - boundText1.width() - boundText2.width()) / 2 + boundText1.width() + offset, cy, textPaint2);
        canvas.drawText("电量", cx, cy + boundText1.height() + offset, textPaint3);
    }
}
