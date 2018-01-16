package com.morrigan.m.device;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 半园背景文本控件
 * Created by y on 2016/10/13.
 */
public class SemiParkTextView extends TextView {

    private Paint paint = new Paint();
    private RectF oval = new RectF();

    public SemiParkTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(0xfff0f0f0);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int w = getWidth();
        final int h = getHeight();
        oval.left = 0;
        oval.top = 0;
        oval.right = w;
        oval.bottom = h * 2;
        canvas.drawArc(oval, -180, 180, false, paint);
        super.onDraw(canvas);
    }
}
