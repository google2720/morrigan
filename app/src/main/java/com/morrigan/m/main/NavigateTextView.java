package com.morrigan.m.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 导航的布局需要下面有一根白色的线
 * Created by y on 2016/10/2.
 */
public class NavigateTextView extends TextView {

    private Paint paint;
    private int strokeWidth = 1;

    public NavigateTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(0xff5B73EE);
        paint.setStrokeWidth(strokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float startX = getPaddingLeft();
        float startY = getHeight() - strokeWidth;
        float stopX = getRight() - getPaddingRight();
        float stopY = getHeight() - strokeWidth;
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }
}
