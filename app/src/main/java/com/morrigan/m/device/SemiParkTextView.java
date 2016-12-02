package com.morrigan.m.device;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 半园背景文本控件
 * Created by y on 2016/10/13.
 */
public class SemiParkTextView extends TextView {

    private Paint paint;

    public SemiParkTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(0xfff0f0f0);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int w = getWidth();
        final int h = getHeight();
        final int r = w / 2;
        canvas.drawCircle(w / 2, h, r, paint);
        super.onDraw(canvas);
    }
}
