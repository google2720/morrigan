package com.morrigan.m.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * 星标控件
 * Created by y on 2016/10/22.
 */
public class StarView extends View {

    private Paint paint = new Paint();
    private Paint textPaint1 = new Paint();
    private Paint textPaint2 = new Paint();
    private Paint textPaint3 = new Paint();
    private Rect boundText1 = new Rect();
    private Rect boundText2 = new Rect();
    private int offset = 2;

    public StarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        float density = getResources().getDisplayMetrics().density;
        offset *= density;
        paint.setAntiAlias(true);
        textPaint1.setAntiAlias(true);
        textPaint1.setColor(Color.WHITE);
        textPaint1.setTextSize(96);
        textPaint1.getTextBounds("88", 0, "88".length(), boundText1);
        textPaint2.setAntiAlias(true);
        textPaint2.setColor(Color.WHITE);
        textPaint2.setTextSize(48);
        textPaint2.getTextBounds("star", 0, "star".length(), boundText2);
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
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xff9438ca);
        canvas.drawCircle(cx, cy, radius, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(cx, cy, radius, paint);
        canvas.drawCircle(cx, cy, radius - 20, paint);

        canvas.drawText("88", (w - boundText1.width() - boundText2.width() - offset) / 2, cy, textPaint1);
        canvas.drawText("star", (w - boundText1.width() - boundText2.width()) / 2 + boundText1.width() + offset, cy, textPaint2);
    }
}
