package com.morrigan.m.about;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 导航的布局需要下面有一根白色的线
 * Created by y on 2016/10/2.
 */
public class LineTextView extends TextView {

    private Paint paint;
    private int strokeWidth = 1;

    public LineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(0xffd7d7d7);
        paint.setStrokeWidth(strokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float startX = getPaddingLeft();
        float startY = getHeight() - strokeWidth;
        float stopX = getRight();
        float stopY = getHeight() - strokeWidth;
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }
}
