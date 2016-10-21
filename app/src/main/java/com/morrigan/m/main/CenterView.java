package com.morrigan.m.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 中心view
 * Created by y on 2016/10/22.
 */
public class CenterView extends View {

    private Paint paint = new Paint();

    public CenterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int w = getWidth();
        final int h = getHeight();
        final int cx = w / 2;
        final int cy = h / 2;
        float radius = w / 2 - 15;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xff7128bd);
        canvas.drawCircle(cx, cy, radius, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(30);
        paint.setColor(0xfff8f9ff);
        canvas.drawCircle(cx, cy, radius, paint);
    }
}
